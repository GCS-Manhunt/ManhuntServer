package network.event;

import io.netty.buffer.ByteBuf;
import main.Main;
import network.ServerHandler;

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
        UUID uuid_seeker = this.clientID;
        UUID uuid_hider = Main.engine.codesTable.get(this.code);
        if(uuid_hider == null) {
            //send confirmation event
            for(int i = 0; i < Main.server.connections.size(); i++) {
                if(Main.server.connections.get(i).clientID == uuid_seeker){
                    Main.server.connections.get(i).events.add(new EventCodeConfirmation(""));
                }
            }
            return;
        } else {
            //convert to seeker
            Main.engine.makeSeeker(uuid_hider);
            //send confirmation event
            for(int i = 0; i < Main.server.connections.size(); i++) {
                if(Main.server.connections.get(i).clientID == uuid_seeker){
                    Main.server.connections.get(i).events.add(new EventCodeConfirmation(Main.engine.hiders.getPlayer(uuid_hider).uname));
                }
            }
            //change score
            Main.engine.seekers.getPlayer(uuid_seeker).score += 1; //Make an algorithm for this
        }
    }
}
