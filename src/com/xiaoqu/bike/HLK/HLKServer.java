package com.xiaoqu.bike.HLK;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HLKServer {
	
		
		
		private static final int PORT = 8080;
	    private ServerBootstrap serverBootstrap;
	    private EventLoopGroup bossGroup = new NioEventLoopGroup();
	    private EventLoopGroup workerGroup = new NioEventLoopGroup();

	    public void start() throws Exception{

	        serverBootstrap = new ServerBootstrap();
	        serverBootstrap.option(ChannelOption.SO_BACKLOG, 3000);//存放请求队列长度
	        serverBootstrap.group(bossGroup,workerGroup)
	                .channel(NioServerSocketChannel.class)
	                .handler(new LoggingHandler(LogLevel.INFO))
	                .option(ChannelOption.TCP_NODELAY, true)				//消息即时发送
	                .childOption(ChannelOption.SO_KEEPALIVE, true)			//启用长链接
	                .childHandler(new HLKInitializer ());

//	        serverBootstrap.bind(PORT).sync().channel();
//	        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
	        Channel ch = serverBootstrap.bind(PORT).sync().channel();

	        System.err.printf("访问地址 http://127.0.0.1:%d/'", PORT);
	        
	        ch.closeFuture().sync();

	    }

	    public void close() {
	        bossGroup.shutdownGracefully();
	        workerGroup.shutdownGracefully();
	    }

	    public static void main(String args[]) {

	    	HLKServer server = new HLKServer();

	    	try {
				server.start ();
				
			} catch (Exception e) {
				server.close();
			}
	        server.close();
	    }
}


