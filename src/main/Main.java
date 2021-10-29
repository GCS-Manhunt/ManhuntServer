package main;

import engine.Engine;
import network.NetworkEventMap;
import network.Server;
import network.SynchronizedList;
import network.event.*;
import player.Player;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static network.Server.eventsIn;

public class Main {

    public static ArrayList<Player> playersAddQueue;
    public static ArrayList<Player> playersDelQueue;
    public static Engine engine;
    public static Server server;
    public static final boolean RUN = true;
    public static final int NDISPLAY = 3;
    public static final double INRANGE = 10;

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
        NetworkEventMap.register(EventSendLocation.class);
        NetworkEventMap.register(EventSendHeading.class);
        NetworkEventMap.register(EventSeekerProximity.class);
        NetworkEventMap.register(EventHiderProximity.class);

        //Print IP before init
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("Running @"+ ip);

        } catch (UnknownHostException e) {

            e.printStackTrace();
        }


        //Server Start
        new Thread() {
            @Override
            public void run() {
                server = new Server(8080);
                server.run();
            }
        }.start();

        while (RUN) {
            syncQueues();
            executeEvents();
            sendEvents();
            sendHeadings();
            //checkInRange(); -- Uncomment after fix
            engine.checkDisconnect();
            engine.checkRejoin();
        }
    }

    private static void checkInRange() {
        for (UUID p1 : engine.seekers.uuids) {
            for (UUID p2 : engine.hiders.uuids) {
                if (p1 != null && p2 != null && engine.seekers.inRange(p1, p2, INRANGE)) {
                    sendInRange(p1, p2);
                    break;
                }

            }
        }
    }

    private static void sendInRange(UUID origin, UUID target) {
        if (server == null) {
            return;
        }
        synchronized (server.connections) {
            for (int i = 0; i < server.connections.size(); i++) {
                if(server.connections.get(i).clientID == origin){
                    synchronized (server.connections.get(i).events) {
                        server.connections.get(i).events.add(new EventSeekerProximity(engine.hiders.getPlayer(target).uname, target, true));
                    }
                }else if (server.connections.get(i).clientID == target){
                    synchronized (server.connections.get(i).events) {
                        server.connections.get(i).events.add(new EventHiderProximity(origin, true));
                    }
                }
            }
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

    public static void sendEvents(){
        if(server == null){
            return;
        }
        synchronized (server.connections) {
            for (int i = 0; i < server.connections.size(); i++) {
                server.connections.get(i).reply();
            }
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
                    Player[] closest = engine.hiders.getRelaventPlayers(player, NDISPLAY);
                    if(closest == null){
                        return;
                    }
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
