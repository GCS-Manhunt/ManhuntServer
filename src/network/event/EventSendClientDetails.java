package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;
import network.ServerHandler;
import player.Player;
import main.Main;

import java.util.UUID;

// Event that is sent from client to server specifying the client's details (version, id, username)
// First event sent from client
public class EventSendClientDetails extends PersonalEvent implements IServerThreadEvent
{
	public int version;
	public UUID clientID;
	public String username;
	
	public EventSendClientDetails()
	{
		
	}
	
	public EventSendClientDetails(int version, UUID clientID, String username)
	{
		this.version = version;
		this.clientID = clientID;
		this.username = username;
	}
	
	@Override
	public void write(ByteBuf b) 
	{
		b.writeInt(this.version);
		NetworkUtils.writeString(b, clientID.toString());
		NetworkUtils.writeString(b, username);
	}
	
	@Override
	public void read(ByteBuf b) 
	{
		this.version = b.readInt();
		this.clientID = UUID.fromString(NetworkUtils.readString(b));
		this.username = NetworkUtils.readString(b);
	}

	@Override
	public void execute()
	{
		// Don't use this
	}

	@Override
	public void execute(ServerHandler s)
	{
		if (Main.engine.quarantined.getPlayer(this.clientID) != null){
			//check if the used to be connected
			s.player = Main.engine.quarantined.getPlayer(this.clientID);
		}else{
			//if he the player has never connected before.
			s.player = new Player(this.clientID, this.username);
		}

		synchronized (Main.playersAddQueue){
			Main.playersAddQueue.add(s.player);
		}
		String[] rules = new String[] {"code", "code", "code", "and code"};
		s.sendEvent(new EventAcceptConnection("ManHunt", "Hunt A13",
				"Sunday 2pm", rules));
		System.out.println(s.player.toString() + " just joined!");
		System.out.println(Main.engine.seekers);
		System.out.println(Main.engine.hiders);
		System.out.println(Main.engine.quarantined);
	}
}
