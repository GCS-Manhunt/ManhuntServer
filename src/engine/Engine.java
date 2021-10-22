package engine;

import player.*;

import java.util.*;

public class Engine {

    public PlayerSet hiders;
    public PlayerSet seekers;
    public PlayerSet quarantined;
    public GeoFence fence;
    public int[] startTime;



    public Engine(int capacity){
        hiders = new PlayerSet(capacity);
        seekers = new PlayerSet(capacity);
        quarantined = new PlayerSet(capacity);
        startTime = new int[] {0, 0};
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

    public Player kickPlayer(UUID uuid){
        Player p = null;
        p = seekers.removePlayer(uuid);
        if(p == null){
            return hiders.removePlayer(uuid);
        }
        return p;
    }

    public void inFence(){
        for(UUID uuid : seekers.uuids){
            if(!fence.in(seekers.getPlayer(uuid))){
                quarantined.addPlayer(seekers.removePlayer(uuid));
            }
        }
        for(UUID uuid : hiders.uuids){
            if(!fence.in(hiders.getPlayer(uuid))){
                quarantined.addPlayer(hiders.removePlayer(uuid));
            }
        }
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

}
