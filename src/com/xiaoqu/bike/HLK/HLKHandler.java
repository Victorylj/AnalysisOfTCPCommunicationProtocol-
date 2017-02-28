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
 * �߼�������
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
		logger.info("HLKHandle ���յ�����" + data + ctx.channel().remoteAddress());

		// ��ͷ��β�ĺϷ����ж�
		String packetHeader = data.substring(0, 4);
		if (!packetHeader.equals("7878") && !packetHeader.equals("7979")) {
			logger.error(String.format("��ͷ���Ϸ� ��%x%x", bytes[0], bytes[1]) + ctx.channel().remoteAddress());
			return;
		}
		if (!data.endsWith("0d0a")) {
			logger.error(String.format("��ͷ���Ϸ� ��%x%x", bytes[bytes.length - 1], bytes[bytes.length])
					+ ctx.channel().remoteAddress());
			return;
		}

		// ��7878��7979�ֱ���
		String lengthStr = "";
		int defaultV = -1;
		int packetLengthCount = 0;
		if (packetHeader.equals("7878")) {
			// ��ͷΪ7878
			// ������ receiveBytes ����=Э���+��Ϣ����+��Ϣ���к�+����У��
			packetLengthCount = 1;
			lengthStr = Hex.encodeHexString(Arrays.copyOfRange(bytes, 2, 2 + packetLengthCount));
			defaultV = 5;
			logger.info("��������Ϊ7878��ʽ    " + ctx.channel().remoteAddress());

		} else if (packetHeader.equals("7979")) {
			// ��ͷΪ7979
			// ������ receiveBytes ����=Э���+��Ϣ����+��Ϣ���к�+����У��
			packetLengthCount = 2;
			lengthStr = Hex.encodeHexString(Arrays.copyOfRange(bytes, 2, 2 + packetLengthCount));
			defaultV = 6;
			logger.info("��������Ϊ7979��ʽ(͸������)    " + ctx.channel().remoteAddress());

		}

		// ��������֤
		int comandLength = Integer.parseInt(lengthStr, 16);
		if (bytes.length - defaultV < comandLength) {// ������ݰ���С�ڰ�ͷ���� ��return
			// "�������ݳ���С������ȶ��壬���յ��ϰ�
			logger.error("�������ݳ���С������ȶ��壬���յ��ϰ�");
			return;
		} else if (bytes.length - defaultV > comandLength) {// ������ݰ���С�ڰ�ͷ����
															// ��return
			logger.error("�������ݳ��ȴ�������ȶ��壬���յ�ճ��");
			return;
		}
		// ����У��
		byte[] check = Arrays.copyOfRange(bytes, 2, bytes.length-4);
		int getCrc16 = CRCUtil.GetCrc16(check, check.length);
		String checkstr = Integer.toHexString(getCrc16);
		checkstr = checkstr.substring(checkstr.length() - 4);
		// ���ݹ�����У��ֵ
		String dataCheck = data.substring(data.length() - 8, data.length() - 4);
		if (!checkstr.equals(dataCheck)) {
			logger.error("δ��ͨ���������");
			return;
			
		}
		
		NettyMessage nettyMessage=new NettyMessage();
		// ��Ϣ�ֽ�
		// Э���
		String key = Hex.encodeHexString(Arrays.copyOfRange(bytes, packetLengthCount + 2, packetLengthCount + 3));
		System.out.println(key);
		nettyMessage.setKey(key);
		// ��Ϣ����
		byte[] infoBytes = Arrays.copyOfRange(bytes, packetLengthCount + 3, bytes.length - 6);
		String infoStr = Hex.encodeHexString(infoBytes);
		System.out.println(infoStr);
		nettyMessage.setInfoBytes(infoBytes);
		// ���к�
		byte[] serialBytes = Arrays.copyOfRange(bytes, bytes.length - 6, bytes.length - 4);
		String serial = Hex.encodeHexString(serialBytes);
		int serialNo = Integer.parseInt(serial, 16);
		System.out.println(serial);
		nettyMessage.setSerialNo(serialNo);
		//����ҵ��
		ServiceUtils.serverService(ctx, nettyMessage);
		

		

	}

}
