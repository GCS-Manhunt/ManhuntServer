package network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import network.event.EventKick;
import network.event.INetworkEvent;

public class Server
{
	public static final SynchronizedList<INetworkEvent> eventsIn = new SynchronizedList<>();

	public int port;
	public EventLoopGroup bossGroup;
	public EventLoopGroup workerGroup;
	public SynchronizedList<ServerHandler> connections = new SynchronizedList<ServerHandler>();

	public Server instance = this;
	public ChannelFuture channel;

	public Server(int port)
	{
		this.port = port;
	}

	public void run()
	{
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();

		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				public void initChannel(SocketChannel ch) {
					ch.pipeline().addLast(new ServerHandler(instance));
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);

			channel = b.bind(port).sync();

			channel.channel().closeFuture().sync();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			close();
		}
	}

	public void close()
	{  
		this.close("The host has ended the party");
	}
	
	public void close(String reason)
	{    	
		synchronized(this.connections)
		{
			for (int i = 0; i < this.connections.size(); i++)
			{
				ServerHandler c = this.connections.get(i);
				c.sendEventAndClose(new EventKick(reason));
			}

			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			connections.clear();
		}
	}    
}