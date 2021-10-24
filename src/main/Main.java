package main;

import engine.Engine;
import network.NetworkEventMap;
import network.Server;
import network.SynchronizedList;
import network.event.*;
import player.Player;

import java.sql.Array;
import java.util.ArrayList;
import static network.Server.eventsIn;

public class Main {

    public static ArrayList<Player> playersAddQueue;
    public static ArrayList<Player> playersDelQueue;
    public static Engine engine;
    public static Server server;
    public static final boolean RUN = true;
    public static final int NDISPLAY = 3;

    public static void main(String[] args) {
        //Initialize Queues
        playersAddQueue = new ArrayList<Player>();
        playersDelQueue = new ArrayList<Player>();

        //Initialize Engine
        engine = new Engine(100); //Will be user adjustable later

        syncQueues();

        //Event Registration
        NetworkEventMap.register(EventPing.class);
        NetworkEventMap.register(EventSendClientDetails.class);
        NetworkEventMap.register(EventKick.class);
        NetworkEventMap.register(EventAcceptConnection.class);
        NetworkEventMap.register(EventSendPlayerIdentity.class);



        //Server Start
        new Thread(){
            @Override
            public void run() {
                server = new Server(8080).run();
            }
        }.start();

        while(RUN){
            syncQueues();
            executeEvents();
            sendHeadings();
        }
    }

    public static void executeEvents(){
        synchronized (eventsIn){
            for(int i = 0; i < eventsIn.size(); i++) {
                eventsIn.get(i).execute();
            }
            eventsIn.clear();
        }
    }

    public static void sendHeadings(){
        if(server == null){
            return;
        }
        synchronized (server.connections) {
            for (int i = 0; i < server.connections.size(); i++) {
                Player player = engine.seekers.getPlayer(server.connections.get(i).clientID);
                if (player != null) {
                    Player[] closest = engine.seekers.getNClosest(player.uuid, NDISPLAY);
                    for (Player p : closest) {
                        synchronized (server.connections.get(i).events) {
                            server.connections.get(i).events.add(new EventSendHeading(player.heading(p), p.uuid));
                        }
                    }
                }
            }
        }
    }

    public static void syncQueues(){
        synchronized (playersAddQueue){
            if(playersAddQueue.size() > 0)
                engine.addPlayer(playersAddQueue.remove(0));
        }
        synchronized (playersDelQueue){
            if(playersDelQueue.size() > 0)
                engine.kickPlayer(playersDelQueue.remove(0).uuid);
        }
    }

}
