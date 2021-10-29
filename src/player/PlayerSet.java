package player;

import java.util.*;

public class PlayerSet {
    public int capacity;
    public Hashtable<UUID, Player> playerList;
    public ArrayList<UUID> uuids;

    public PlayerSet(int capacity){
        this.capacity = capacity;
        playerList = new Hashtable<UUID, Player>();
        uuids = new ArrayList<UUID>();
    }

    public boolean addPlayer(Player p){
        if(uuids.size() != playerList.size()){
            cleanPlayers();
        }
        if (playerList.size() < capacity) {
            playerList.put(p.uuid, p);
            uuids.add(p.uuid);
            return true;
        } else {
            return false;
        }
    }

    public Player removePlayer(UUID uuid){
        if(uuids.size() != playerList.size()){
            cleanPlayers();
        }
        uuids.remove(uuid);
        return playerList.remove(uuid);
    }

    //Eliminates bad uuids from uuids

    private void cleanPlayers() {
        for(int i = 0; i < uuids.size(); i++){
            if(!playerList.containsKey(uuids.get(i))){
                uuids.remove(i);
                i--; //don't skip players after remove uuid;
            }
        }
    }

    public Player getPlayer(UUID uuid){
        return playerList.get(uuid);
    }

    //returns the N closest players to a s
    //
    // Source player

    public Player[] getNClosest(Player origin, int n){
        ArrayList<Player> players = new ArrayList<Player>();
        for(int i = 0; i < uuids.size(); i++){
            if(players.size() < n) {
                players.add(getPlayer(uuids.get(i)));
            }else{
                for(int j = 0; i < n; i++){
                    if(getPlayer(uuids.get(i)).distance(origin) > getPlayer(players.get(j).uuid).distance(origin)){
                        players.set(j, getPlayer(uuids.get(i)));
                    }
                }
            }
        }
        return players.toArray(new Player[players.size()]);
    }

    public Player[] getRelaventPlayers(Player origin, int max){
        if(max < 1 || origin == null){
            return null;
        }
        ArrayList<Player> nclosest = new ArrayList<Player>(Arrays.asList(getNClosest(origin, max)));
        if(nclosest.size() == 0){
            return null;
        }
        double adist = nclosest.get(0).distance(origin);
        int i = 1;
        while (i < nclosest.size()){
            if (nclosest.get(i).distance(origin) > 1.5 * adist){
                nclosest.remove(i);
            }
        }
        return nclosest.toArray(new Player[nclosest.size()]);
    }


    public boolean inRange(UUID origin, UUID target, double dist){
        Player o = getPlayer(origin);
        Player t = getPlayer(target);
        return o.distance(t) < dist;
    }

    public String toString(){
        return this.playerList.toString();
    }
}
