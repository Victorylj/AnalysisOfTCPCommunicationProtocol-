package com.xiaoqu.bike.HLK;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogLevel;

	/*
	 * 初始化组件
	 */
public class HLKInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p=ch.pipeline();
//		ByteBuf delimiter1 = Unpooled.copiedBuffer("7878".getBytes());
//		ByteBuf delimiter2 = Unpooled.copiedBuffer("0D0A".getBytes());
//		ByteBuf [] delimiters={delimiter1,delimiter2};
//		p.addLast(new DelimiterBasedFrameDecoder(1024, delimiters));
		
		p.addLast("logging", new LoggingHandler(LogLevel.INFO));
		p.addLast("decoder",new ByteArrayDecoder());//解码器
		p.addLast("encoder",new ByteArrayEncoder());//编码器
//		p.addLast(new StringDecoder());
		p.addLast(new HLKHandler());	//添加逻辑处理器
		
	}

}
