package engine;

import player.*;

import java.util.*;

public class Engine {

    public PlayerSet hiders;
    public PlayerSet seekers;
    public PlayerSet quarantined;
    public GeoFence fence;
    public int[] startTime;

    public Hashtable<Integer, UUID> codesTable = new Hashtable<Integer, UUID>();
    //public Hashtable<UUID, Integer> UUIDCodesTable = new Hashtable<UUID, Integer>();



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


    public void initializeCodes(){
        int size = hiders.playerList.size();
        for(UUID id : hiders.playerList.keySet()) {
            //get the player from the id
            Player player = hiders.getPlayer(id);
            boolean existDuplicate = true;
            while(existDuplicate) {
                player.code = (int)(Math.random()*1000000); //get a random 6 digit integer
                if(codesTable.get(player.code) == null) { //check if the player code is already in the hash map
                    codesTable.put(player.code, id);
                    //UUIDCodesTable.put(id, player.code);
                    existDuplicate = false;
                }
            }

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
