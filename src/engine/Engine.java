package engine;

import player.*;

import java.util.*;

public class Engine {

    public PlayerSet hiders;
    public PlayerSet seekers;
    public PlayerSet quarentined;
    public GeoFence fence;

    public Engine(int capacity){
        hiders = new PlayerSet(capacity);
        seekers = new PlayerSet(capacity);
        quarentined = new PlayerSet(capacity);
    }

    public void addPlayer(Player p){
        if(p.seeker){
            seekers.addPlayer(p);
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
                quarentined.addPlayer(seekers.removePlayer(uuid));
            }
        }
        for(UUID uuid : hiders.uuids){
            if(!fence.in(hiders.getPlayer(uuid))){
                quarentined.addPlayer(hiders.removePlayer(uuid));
            }
        }
    }

    public void disconnected(){
        for(UUID uuid : seekers.uuids){
            if(!seekers.getPlayer(uuid).status){
                quarentined.addPlayer(seekers.removePlayer(uuid));
            }
        }
        for(UUID uuid : hiders.uuids){
            if(!hiders.getPlayer(uuid).status){
                quarentined.addPlayer(hiders.removePlayer(uuid));
            }
        }
    }

    public void rejoin(){
        for(UUID uuid : quarentined.uuids){
            if(fence.in(quarentined.getPlayer(uuid))){
                Player p = quarentined.removePlayer(uuid);
                if(p.seeker && p.status){
                    seekers.addPlayer(p);
                }else if(p.status){
                    hiders.addPlayer(p);
                }
            }
        }
    }

}
