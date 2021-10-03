package main;

import engine.Engine;
import network.NetworkEventMap;
import network.Server;
import network.SynchronizedList;
import network.event.EventKick;
import network.event.EventPing;
import network.event.EventSendClientDetails;
import player.Player;

import java.sql.Array;
import java.util.ArrayList;

public class Main {

    public static ArrayList<Player> playersAddQueue;
    public static ArrayList<Player> playersDelQueue;
    private static Engine engine;

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

        //Server Start
        new Thread(){
            @Override
            public void run() {
                new Server(8080).run();
            }
        }.start();
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
