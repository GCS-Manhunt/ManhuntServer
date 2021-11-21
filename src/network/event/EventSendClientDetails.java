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
		synchronized (Main.server.connections){
			Main.server.connections.add(s);
		}
		s.clientID = this.clientID;
		if (Main.engine.quarantined.getPlayer(this.clientID) != null){
			//check if the used to be connected
			s.player = Main.engine.quarantined.getPlayer(this.clientID);
		}else{
			//if the player has never connected before.
			s.player = new Player(this.clientID, this.username);
		}

		String[] rules = new String[] {"code", "code", "code", "and code"};
		int code = (int)(Math.random()*1000000);
		while(Main.engine.codesTable.get(code) != null){
			code = (int)(Math.random()*1000000);
		}
		s.player.code = code;
		Main.engine.codesTable.put(code, this.clientID);

		s.sendEvent(new EventAcceptConnection("ManHunt", "Hunt A13",
				"Sunday 2pm", rules, code));
		System.out.println(s.player.toString() + " just joined!");
		synchronized (Main.engine){
			if(Main.engine.seekers.uuids.size() == 0){
				s.player.seeker = true;
				s.sendEvent(new EventMakeSeeker());
			}
			System.out.println(Main.engine.seekers.uuids.size());
		}
		synchronized (Main.playersAddQueue) {
			Main.playersAddQueue.add(s.player);
		}
	}
}
