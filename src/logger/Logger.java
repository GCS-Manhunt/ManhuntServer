package logger;

import java.util.ArrayList;
import java.util.Hashtable;

public class Logger {
    private Hashtable<String, String> log;
    private int interval;
    private long start;
    long count;

    public Logger(){
        this.log = new Hashtable<>();
        this.interval = 1000;
        this.count = 0;
    }

    public void init(){
        this.start = System.currentTimeMillis();
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public void log(String k, String v){
        this.log.put(k,v);
    }

    public void printlog(){
        long now = System.currentTimeMillis();
        if(now - start >= interval) {
            for (String k : log.keySet()) {
                count++;
                System.out.println(count + ") " + k + ": " + log.get(k));
            }
            log.clear();
            start = now;
        }
    }
}
