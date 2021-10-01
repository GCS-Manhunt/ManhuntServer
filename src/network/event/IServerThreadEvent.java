package network.event;

import network.ServerHandler;

public interface IServerThreadEvent 
{
	void execute(ServerHandler s);
}
