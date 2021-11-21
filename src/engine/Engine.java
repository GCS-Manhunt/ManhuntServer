package engine;

import player.*;

import java.util.*;

public class Engine {

    public PlayerSet hiders;
    public PlayerSet seekers;
    public PlayerSet quarantined;
    public GeoFence fence;
    public long startTime;

    public int gameDuration; //unit: minute
    private int baseScore = 20; //this the score seeker get for find first person.

    public Hashtable<Integer, UUID> codesTable = new Hashtable<Integer, UUID>();



    public Engine(int capacity){
        hiders = new PlayerSet(capacity);
        seekers = new PlayerSet(capacity);
        quarantined = new PlayerSet(capacity);
        startTime = System.currentTimeMillis();
    }

    public void addPlayer(Player p){
        if(p.seeker){
            seekers.addPlayer(p);
            System.out.println("seeker added"+p.toString());
        }else{
            hiders.addPlayer(p);
        }
    }

    public void makeSeeker(UUID uuid){
        Player p = hiders.removePlayer(uuid);
        p.seeker = true;
        seekers.addPlayer(p);
    }

    public void makeRandomSeeker(){
        int size = hiders.playerList.size();
        int player = (int)(Math.random()*size); //returns a random integer between 0 (inclusive) and size (exclusive)
        int i = 0;
        for(UUID id : hiders.playerList.keySet()) {
            if(i==player) {
                makeSeeker(id);
                break;
            }
            i++;
        }
    }

    //update hider score base on their proximity situation with hiders
    public void hiderScore(){
        for (Player p1 : hiders.playerList.values()) {
            int addScore = 0; //reset for each new hider;
            for (Player p2 : seekers.playerList.values()) {
                if (!(p1 != null && p2 != null)){
                    return;
                }
                else if (hiders.inRange(p1, p2, 20)){
                    //if hiders is close to any
                    addScore = 10;
                }else if(hiders.inRange(p1, p2, 100)){
                    addScore = 2;
                }else if(hiders.inRange(p1, p2, 200)){
                    addScore = 1;
                }
            }
            p1.score += addScore;
        }
    }

    public Player kickPlayer(UUID uuid){
        Player p = null;
        p = seekers.removePlayer(uuid);
        if(p == null){
            return hiders.removePlayer(uuid);
        }
        return p;
    }

    public void checkDisconnect(){
        for(UUID uuid : seekers.uuids){
            if(!seekers.getPlayer(uuid).status){
                quarantined.addPlayer(seekers.removePlayer(uuid));
            }
        }
        for(UUID uuid : hiders.uuids){
            if(!hiders.getPlayer(uuid).status){
                quarantined.addPlayer(hiders.removePlayer(uuid));
            }
        }
    }

    public void checkRejoin(){
        for(UUID uuid : quarantined.uuids){
            if(fence.in(quarantined.getPlayer(uuid))){
                Player p = quarantined.removePlayer(uuid);
                if(p.seeker && p.status){
                    seekers.addPlayer(p);
                }else if(p.status){
                    hiders.addPlayer(p);
                }
            }
        }
    }

    public void seekerScore(UUID uuid) {

        Player p = seekers.getPlayer((uuid));

        if (p == null) {
            return;
        }

        p.score += baseScore + getParameter() * (hiders.uuids.size() - 1);
        //-1 for minus the player himself, leving amount of players are found.
    }

    public double getParameter() {
        int totalPlayer = hiders.uuids.size() + seekers.uuids.size();
        return (3 * gameDuration - baseScore) / (totalPlayer * totalPlayer);
    }

}



