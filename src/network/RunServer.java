package network;

import network.event.EventKick;
import network.event.EventPing;
import network.event.EventSendClientDetails;

public class RunServer
{
    public static void main(String[] args)
    {
        NetworkEventMap.register(EventPing.class);
        NetworkEventMap.register(EventSendClientDetails.class);
        NetworkEventMap.register(EventKick.class);

        new Server(8080).run();
    }
}
