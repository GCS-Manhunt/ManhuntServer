package network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import main.Main;
import network.event.EventPing;
import network.event.INetworkEvent;
import player.Player;


import java.util.UUID;

public class ServerHandler extends ChannelInboundHandlerAdapter
{
	public MessageReader reader = new MessageReader();
	public SynchronizedList<INetworkEvent> events = new SynchronizedList<INetworkEvent>();

	public ChannelHandlerContext ctx;

	public Server server;

	public UUID clientID;

	public Player player;

	public long lastMessage = -1;
	public long latency = 0;

	public long latencySum = 0;
	public int latencyCount = 1;
	public long lastLatencyTime = 0;
	public long lastLatencyAverage = 0;

	public String username;

	public boolean closed = false;

	// SERVERSIDE
	public ServerHandler(Server s)
	{
		this.server = s;
	}

	// SERVERSIDE: When a client connects
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		this.ctx = ctx;

		if (ctx != null)
			this.reader.queue = ctx.channel().alloc().buffer();
		else
			this.reader.queue = Unpooled.buffer();
	}

	// SERVERSIDE: When a client disconnects
	@Override
	public void channelInactive(ChannelHandlerContext ctx)
	{
		ReferenceCountUtil.release(this.reader.queue);
		server.connections.remove(this);
	}

	// Packet received
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		if (closed)
			return;

		this.ctx = ctx;
		ByteBuf buffy = (ByteBuf) msg;
		boolean reply = this.reader.queueMessage(this, buffy, this.clientID);

		ReferenceCountUtil.release(msg);

		// Calculates latency
		if (reply)
		{
			if (lastMessage < 0)
				lastMessage = System.currentTimeMillis();

			long time = System.currentTimeMillis();
			latency = time - lastMessage;
			lastMessage = time;

			latencyCount++;
			latencySum += latency;

			if (time / 1000 > lastLatencyTime)
			{
				lastLatencyTime = time / 1000;
				lastLatencyAverage = latencySum / latencyCount;

				latencySum = 0;
				latencyCount = 0;
			}

			this.sendEvent(new EventPing());
		}
	}

	// Sends all queued packets
	public void reply()
	{
		synchronized (this.events)
		{
			for (int i = 0; i < this.events.size(); i++)
			{
				INetworkEvent e = this.events.get(i);
				this.sendEvent(e);
			}

			this.events.clear();
		}
	}

	public synchronized void sendEvent(INetworkEvent e)
	{
		ByteBuf b = ctx.channel().alloc().buffer();

		int i = NetworkEventMap.get(e.getClass());
		if (i == -1)
			throw new RuntimeException("The network event " + e.getClass() + " has not been registered!");

		b.writeInt(i);
		e.write(b);

		ByteBuf b2 = ctx.channel().alloc().buffer();
		b2.writeInt(b.readableBytes());
		b2.writeBytes(b);
		ctx.channel().writeAndFlush(b2);

		ReferenceCountUtil.release(b);
	}

	public synchronized Player sendEventAndClose(INetworkEvent e)
	{
		this.closed = true;
		this.sendEvent(e);
		synchronized (Main.playersDelQueue){
			if(player != null) {
				Main.playersDelQueue.add(player);
			}
		}

		if (ctx != null)
			ctx.close();

		return this.player;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();

		ctx.close();
	}
}