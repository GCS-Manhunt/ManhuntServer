package network.event;

import io.netty.buffer.ByteBuf;

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

    }
}
