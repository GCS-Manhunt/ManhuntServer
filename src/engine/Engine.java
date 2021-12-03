package engine;

import player.*;

import java.util.*;

public class Engine {

    public PlayerSet hiders;
    public PlayerSet seekers;
    public PlayerSet quarantined;
    public GeoFence fence;
    public long startTime;
    public int gameDuration;

    public Hashtable<Integer, UUID> codesTable = new Hashtable<Integer, UUID>();

    public Engine(int capacity){
        hiders = new PlayerSet(capacity);
        seekers = new PlayerSet(capacity);
        quarantined = new PlayerSet(capacity);
        startTime = System.currentTimeMillis();
        gameDuration = 1;
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
            double addScore = 0; //reset for each new hider;
            for (Player p2 : seekers.playerList.values()) {
                if (!(p1 != null && p2 != null)){
                    return;
                }
                else if (hiders.inRange(p1, p2, 20)){
                    //if hiders is close to any
                    addScore = 10.0/5;
                }else if(hiders.inRange(p1, p2, 100)){
                    addScore = 2.0/5;
                }else if(hiders.inRange(p1, p2, 200)){
                    addScore = 1.0/5;
                }
            }
            p1.score += addScore;
        }
    }

    public static void mergeSort(String[][] a, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        String[][] l = new String[mid][2];
        String[][] r = new String[n - mid][2];

        for (int i = 0; i < mid; i++) {
            l[i] = a[i];
        }
        for (int i = mid; i < n; i++) {
            r[i - mid] = a[i];
        }
        mergeSort(l, mid);
        mergeSort(r, n - mid);

        merge(a, l, r, mid, n - mid);
    }
    public static void merge(
            String[][] a, String[][] l, String[][] r, int left, int right) {

        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (Double.parseDouble(l[i][1]) >= Double.parseDouble(r[j][1])) {
                a[k++] = l[i++];
            }
            else {
                a[k++] = r[j++];
            }
        }
        while (i < left) {
            a[k++] = l[i++];
        }
        while (j < right) {
            a[k++] = r[j++];
        }
    }
    public String[][] ranking(){
        int num_players = hiders.playerList.size() + seekers.playerList.size();
        String[][] ranking = new String[num_players][2];
        int counter = 0;
        for(UUID uuid : seekers.uuids) {
            ranking[counter][0] = uuid.toString();
            ranking[counter][1] = String.valueOf(seekers.getPlayer(uuid).score);
            counter++;
        }
        for(UUID uuid : hiders.uuids) {
            ranking[counter][0] = uuid.toString();
            ranking[counter][1] = String.valueOf(hiders.getPlayer(uuid).score);
            counter++;
        }
        mergeSort(ranking, num_players);
        return ranking;
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
}
