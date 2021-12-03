package main;

import engine.Engine;
import logger.Logger;
import network.NetworkEventMap;
import network.Server;
import network.event.*;
import player.Player;
import player.PlayerSet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static network.Server.eventsIn;

public class Main {

    public static ArrayList<Player> playersAddQueue;
    public static ArrayList<Player> playersDelQueue;
    public static Engine engine;
    public static Server server;
    public static final boolean RUN = true;
    public static final int NDISPLAY = 3;
    public static final double INRANGE = 20;
    public static Logger logger;

    //Initializing inRange
    public static HashMap<Player, Player> inRange;

    public static void main(String[] args) {
        //Initialize Queues
        playersAddQueue = new ArrayList<Player>();
        playersDelQueue = new ArrayList<Player>();

        //Initialize Engine
        engine = new Engine(100); //Will be user adjustable later

        //Initialize inRange
        inRange = new HashMap<Player, Player>();

        syncQueues();

        //Event Registration
        NetworkEventMap.register(EventPing.class);
        NetworkEventMap.register(EventSendClientDetails.class);
        NetworkEventMap.register(EventKick.class);
        NetworkEventMap.register(EventAcceptConnection.class);
        NetworkEventMap.register(EventSendLocation.class);
        NetworkEventMap.register(EventSendHeading.class);
        NetworkEventMap.register(EventSeekerProximity.class);
        NetworkEventMap.register(EventHiderProximity.class);
        NetworkEventMap.register(EventEnterCode.class);
        NetworkEventMap.register(EventMakeSeeker.class);
        NetworkEventMap.register(EventSendScore.class);
        NetworkEventMap.register(EventCodeConfirmation.class);

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

        //Logger
        logger = new Logger();
        logger.setInterval(3000);
        long last = 0;
        while (RUN) {
            long now = System.currentTimeMillis()/1000;
            if(last < now){
                last = now;
                engine.hiderScore();
                sendHeadings();
                rank();
                checkInRange();
                stillInRange();
            }
            logger.printlog();
            syncQueues();
            executeEvents();
            sendEvents();
            engine.checkDisconnect();
        }
    }

    private static void checkInRange() {
        for (Player p1 : engine.seekers.playerList.values()) {
            for (Player p2 : engine.hiders.playerList.values()) {
                if (p1 != null && p2 != null && PlayerSet.inRange(p1, p2, INRANGE)) {
                    inRange.put(p1,p2);
                    sendInRange(p1, p2);
                    break;
                }

            }
        }
    }

    public static void rank(){
        String[][] rankings = engine.ranking();
        if(rankings.length >= 1) {
            synchronized (server.connections) {
                for (int j = 0; j < server.connections.size(); j++) {
                    if(server.connections.get(j).clientID.equals(UUID.fromString(rankings[0][0]))){
                        synchronized (server.connections.get(j).events) {
                            server.connections.get(j).events.add(new EventSendScore((int)(Double.parseDouble(rankings[0][1])), 1, 0));
                        }
                    }
                }
            }
            int rank = 1;
            double dist = 0;
            for (int i = 1; i < rankings.length; i++) {
                if(Double.parseDouble(rankings[i][1]) == Double.parseDouble(rankings[i-1][1])){

                }else{
                    rank = i+1;
                    dist = Double.parseDouble(rankings[i-1][1]) - Double.parseDouble(rankings[i][1]);
                    synchronized (server.connections) {
                        for (int j = 0; j < server.connections.size(); j++) {
                            if (server.connections.get(j).clientID.equals(UUID.fromString(rankings[i][0]))) {
                                synchronized (server.connections.get(j).events) {
                                    server.connections.get(j).events.add(new EventSendScore((int)(Double.parseDouble(rankings[i][1])), rank, (int)dist));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void stillInRange(){
        if(inRange != null && inRange.size() > 0){
            for(Player k : inRange.keySet()){
                Player v = inRange.get(k);
                if (k != null && v != null && PlayerSet.inRange(k, v, INRANGE)) {
                    sendInRange(k, v);
                    break;
                } else {
                    sendOutOfRange(k,v);
                }
            }
        }
    }

    private static void sendInRange(Player origin, Player target) {
        if (server == null) {
            return;
        }
        synchronized (server.connections) {
            for (int i = 0; i < server.connections.size(); i++) {
                if(server.connections.get(i).clientID.equals(origin.uuid)){
                    synchronized (server.connections.get(i).events) {
                        server.connections.get(i).events.add(new EventSeekerProximity(target.uname, target.uuid, true));
                    }
                }else if (server.connections.get(i).clientID.equals(target.uuid)){
                    synchronized (server.connections.get(i).events) {
                        server.connections.get(i).events.add(new EventHiderProximity(origin.uuid, true));
                    }
                }
            }
        }
    }

    private static void sendOutOfRange(Player origin, Player target) {
        if (server == null) {
            return;
        }
        synchronized (server.connections) {
            for (int i = 0; i < server.connections.size(); i++) {
                if(server.connections.get(i).clientID.equals(origin.uuid)){
                    synchronized (server.connections.get(i).events) {
                        server.connections.get(i).events.add(new EventSeekerProximity(target.uname, target.uuid, false));
                    }
                }else if (server.connections.get(i).clientID.equals(target.uuid)){
                    synchronized (server.connections.get(i).events) {
                        server.connections.get(i).events.add(new EventHiderProximity(origin.uuid, false));
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

    public void hiderScore(){
        /*
        hider score updates;
         */
        int addScore = 0; //the score add for each player.

        for (Player p1 : engine.hiders.playerList.values()) {

            addScore = 0; //reset for each new hider;
            for (Player p2 : engine.seekers.playerList.values()) {
                //System.out.println(p1.distance(p2));
                if (p1 != null && p2 != null){
                    System.out.println("From Engine hiderScore: got both p1 and p2 null");
                }
                /*
                different score for different range;
                 */
                else if (engine.hiders.inRange(p1, p2, 20)){
                    //if hiders is close to any
                    addScore = 10;
                }else if(engine.hiders.inRange(p1, p2, 100)){
                    addScore = 3;
                }else if(engine.hiders.inRange(p1, p2, 200)){
                    addScore = 1;
                }
            }
            p1.score += addScore;
        }
    }

}
