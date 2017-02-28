package com.xiaoqu.bike.HLK;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.xiaoqu.bike.dto.NettyMessage;
import com.xiaoqu.bike.service.DataService;
import com.xiaoqu.bike.utils.CRCUtil;
import com.xiaoqu.bike.utils.ServiceUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/*
 * 逻辑控制器
 */
public class HLKHandler extends SimpleChannelInboundHandler<Object> {
	private static Logger logger = Logger.getLogger(HLKHandler.class.getName());
	private static DataService dataService=new DataService();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		byte[] bytes = (byte[]) msg;
		int length2 = bytes.length;
		if (length2 == 0) {
			return;
		}
		String data = Hex.encodeHexString(bytes);
		logger.info("HLKHandle 接收的数据" + data + ctx.channel().remoteAddress());

		// 包头包尾的合法性判断
		String packetHeader = data.substring(0, 4);
		if (!packetHeader.equals("7878") && !packetHeader.equals("7979")) {
			logger.error(String.format("包头不合法 ：%x%x", bytes[0], bytes[1]) + ctx.channel().remoteAddress());
			return;
		}
		if (!data.endsWith("0d0a")) {
			logger.error(String.format("包头不合法 ：%x%x", bytes[bytes.length - 1], bytes[bytes.length])
					+ ctx.channel().remoteAddress());
			return;
		}

		// 对7878与7979分别处理
		String lengthStr = "";
		int defaultV = -1;
		int packetLengthCount = 0;
		if (packetHeader.equals("7878")) {
			// 包头为7878
			// 包长度 receiveBytes 长度=协议号+信息内容+信息序列号+错误校验
			packetLengthCount = 1;
			lengthStr = Hex.encodeHexString(Arrays.copyOfRange(bytes, 2, 2 + packetLengthCount));
			defaultV = 5;
			logger.info("传递数据为7878格式    " + ctx.channel().remoteAddress());

		} else if (packetHeader.equals("7979")) {
			// 包头为7979
			// 包长度 receiveBytes 长度=协议号+信息内容+信息序列号+错误校验
			packetLengthCount = 2;
			lengthStr = Hex.encodeHexString(Arrays.copyOfRange(bytes, 2, 2 + packetLengthCount));
			defaultV = 6;
			logger.info("传递数据为7979格式(透传数据)    " + ctx.channel().remoteAddress());

		}

		// 包长度验证
		int comandLength = Integer.parseInt(lengthStr, 16);
		if (bytes.length - defaultV < comandLength) {// 如果数据包长小于包头包长 则return
			// "接收数据长度小于命令长度定义，接收到断包
			logger.error("接收数据长度小于命令长度定义，接收到断包");
			return;
		} else if (bytes.length - defaultV > comandLength) {// 如果数据包长小于包头包长
															// 则return
			logger.error("接收数据长度大于命令长度定义，接收到粘包");
			return;
		}
		// 错误校验
		byte[] check = Arrays.copyOfRange(bytes, 2, bytes.length-4);
		int getCrc16 = CRCUtil.GetCrc16(check, check.length);
		String checkstr = Integer.toHexString(getCrc16);
		checkstr = checkstr.substring(checkstr.length() - 4);
		// 传递过来的校验值
		String dataCheck = data.substring(data.length() - 8, data.length() - 4);
		if (!checkstr.equals(dataCheck)) {
			logger.error("未能通过错误检验");
			return;
			
		}
		
		NettyMessage nettyMessage=new NettyMessage();
		// 消息分解
		// 协议号
		String key = Hex.encodeHexString(Arrays.copyOfRange(bytes, packetLengthCount + 2, packetLengthCount + 3));
		System.out.println(key);
		nettyMessage.setKey(key);
		// 信息主体
		byte[] infoBytes = Arrays.copyOfRange(bytes, packetLengthCount + 3, bytes.length - 6);
		String infoStr = Hex.encodeHexString(infoBytes);
		System.out.println(infoStr);
		nettyMessage.setInfoBytes(infoBytes);
		// 序列号
		byte[] serialBytes = Arrays.copyOfRange(bytes, bytes.length - 6, bytes.length - 4);
		String serial = Hex.encodeHexString(serialBytes);
		int serialNo = Integer.parseInt(serial, 16);
		System.out.println(serial);
		nettyMessage.setSerialNo(serialNo);
		//处理业务
		ServiceUtils.serverService(ctx, nettyMessage);
		

		

	}

}
