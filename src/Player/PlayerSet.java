package Player;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

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
            }
        }
    }

    public Player getPlayer(UUID uuid){
        return playerList.get(uuid);
    }

    //returns the N closest players to a source player

    public Player[] getNClosest(UUID origin, int n){
        ArrayList<Player> players = new ArrayList<Player>();
        for(int i = 0; i < uuids.size(); i++){
            if(players.size() < n) {
                players.add(getPlayer(uuids.get(i)));
            }else{
                for(int j = 0; i < n; i++){
                    if(getPlayer(uuids.get(i)).distance(getPlayer(origin)) > getPlayer(players.get(j).uuid).distance(getPlayer(origin))){
                        players.set(j, getPlayer(uuids.get(i)));
                    }
                }
            }
        }
        return players.toArray(new Player[players.size()]);
    }

}
