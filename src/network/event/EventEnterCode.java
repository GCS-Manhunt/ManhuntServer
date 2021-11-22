package network.event;

import io.netty.buffer.ByteBuf;
import main.Main;
import network.ServerHandler;

import java.sql.SQLOutput;
import java.util.UUID;

public class EventEnterCode extends PersonalEvent{
    public int code;

    public EventEnterCode(int code) {
        this.code = code;
    }

    public EventEnterCode() {

    }

    @Override
    public void write(ByteBuf b) {
        b.writeInt(this.code);
    }

    @Override
    public void read(ByteBuf b) {
        this.code = b.readInt();
    }

    @Override
    public void execute() {
        synchronized (Main.engine) {
            UUID uuid_seeker = this.clientID;
            UUID uuid_hider = Main.engine.codesTable.get(this.code);
            if (uuid_hider == null) {
                //send confirmation event
                for (int i = 0; i < Main.server.connections.size(); i++) {
                    if (Main.server.connections.get(i).clientID.equals(uuid_seeker)) {
                        Main.server.connections.get(i).events.add(new EventCodeConfirmation("No Player Found!"));
                    }
                }
                return;
            } else {
                //send confirmation event
                for (int i = 0; i < Main.server.connections.size(); i++) {
                    if (Main.server.connections.get(i).clientID.equals(uuid_seeker)) {
                        try{
                            Main.server.connections.get(i).events.add(new EventCodeConfirmation("Found "+Main.engine.hiders.getPlayer(uuid_hider).uname));
                            System.out.println(Main.engine.hiders.getPlayer(uuid_hider).uname + " was found by " + Main.server.connections.get(i).player.uname);
                            double x = ((double) Main.engine.hiders.uuids.size()) / (Main.engine.seekers.uuids.size() + Main.engine.hiders.uuids.size());
                            Main.engine.seekers.getPlayer(uuid_seeker).score += 75*x*x + 25; //Make an algorithm for this
                        }catch(NullPointerException e){
                            Main.server.connections.get(i).events.add(new EventCodeConfirmation("No Player Found!"));
                        }
                    }
                }
                //Make Seeker
                try {
                    Main.engine.makeSeeker(uuid_hider);
                    for (int i = 0; i < Main.server.connections.size(); i++) {
                        if (Main.server.connections.get(i).clientID.equals(uuid_hider)) {
                            Main.server.connections.get(i).events.add(new EventMakeSeeker());
                        }
                    }
                } catch(NullPointerException e){
                }
            }
            Main.inRange.clear();
        }
    }
}
