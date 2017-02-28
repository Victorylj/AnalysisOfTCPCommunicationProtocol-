package com.xiaoqu.bike.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.xiaoqu.bike.common.Common;
import com.xiaoqu.bike.dao.BatteryInfoMapper;
import com.xiaoqu.bike.dto.BatteryStatusInfo;
import com.xiaoqu.bike.dto.NettyMessage;
import com.xiaoqu.bike.service.DataService;

import io.netty.channel.ChannelHandlerContext;

public class ServiceUtils {
	private static Logger logger = Logger.getLogger(ServiceUtils.class.getName());
	private static DataService dataService=new DataService();
	static int i=1;
	static String imei="";			//imei��
	static int batteryLevel=6;		//��ص���
	
	public static void serverService(ChannelHandlerContext ctx,NettyMessage nettyMessage){
		
		
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("������Ϣ��Э��ţ�  "+nettyMessage.getKey());
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		int batteryLevel=6;		//��ص���
//		String imei="";			//imei��
		switch (nettyMessage.getKey()) { // ҵ���߼�
		case "01": // ��½��Ϣ
			System.out.println("��½��Ϣ");
			logger.info("��¼��Ϣ");
			nettyMessage.getInfoBytes();
			imei = Hex.encodeHexString(nettyMessage.getInfoBytes()).substring(1);
//			System.out.println("�豸imei��Ϊ��"+imei);
			boolean flag = dataService.selectBatteryInfoByImei(imei);
			if(flag){
				ctx.writeAndFlush(Common.loginBack);	//��ע���豸�����͵�½��Ӧ��
			}else{
				ctx.writeAndFlush(Common.loginfalseBack);	//δע���豸��������Ӧ��
				imei=null;
			}
			
			System.out.println("�豸imei��Ϊ��"+imei+"   #��½��Ӧ�ѷ���");
			

			break;
		case "13": // ״̬��Ϣ
			byte[] infoBytes2 = nettyMessage.getInfoBytes();
			batteryLevel=infoBytes2[1];
			System.out.println("״̬��Ϣ");
			byte [] head={0x78,0x78};
			byte [] info={0x05,0x13};
			i+=i;
//			byte[] statusBack = AddSerNoUtil.add(head, info, i);
			
			ctx.writeAndFlush(Common.statusBack);
			System.out.println("��������Ӧ�ѷ���");
			break;

		case "12": // GPS ��λ����
			
			byte[] infoBytes = nettyMessage.getInfoBytes();
			int farmeNo=infoBytes[0];
			System.out.println(farmeNo);
			int year=infoBytes[1];
			int month=infoBytes[2];
			int day=infoBytes[3];
			int hrs=infoBytes[4];
			int min=infoBytes[5];
			int sec=infoBytes[6];
			Date date=new Date();
			SimpleDateFormat sd=new SimpleDateFormat("yyyy��MM��dd�� HHʱmm��ss��");
			
			@SuppressWarnings({ "deprecation", "static-access" })
			long utcTime = date.UTC(year, month, day, hrs, min, sec);//�ϴ���ʱ����Ϣ
			System.out.println("ʱ��"+sd.format(utcTime));
			logger.info("�ϴ�ʱ��Ϊ"+sd.format(utcTime));
			byte[] lat = Arrays.copyOfRange(infoBytes, 7, 11);
			String latencodeHexString = Hex.encodeHexString(lat);
			System.out.println(latencodeHexString);
			double ss = Long.parseLong(latencodeHexString, 16);	//γ��
			double a=30000;
			double c=ss/a; 
			System.out.println(c);
			int d= (int) (c/60); double e=(c%60)/100;
			String latitude=(double)d+e+"";
			System.out.println("γ��"+latitude);
			
			byte[] longit = Arrays.copyOfRange(infoBytes, 11, 15);
			String longitencodeHexString = Hex.encodeHexString(longit);
			System.out.println(longitencodeHexString);
			double ss1 = Long.parseLong(longitencodeHexString, 16);		//����
			double c1=ss1/a; 
			int d1= (int) (c1/60); double e1=(c%60)/100;
			String longitude=(double)d1+e1+"";
			System.out.println("����"+longitude);
			
			int speed=infoBytes[15];		//��ǰ�ٶ�
			System.out.println("�ٶ�"+speed);
			String MCC_MNC="";		//�ƶ���Ӫ��
			int MNCNum=infoBytes[20];
			if(MNCNum==00||MNCNum==02||MNCNum==07){
				MCC_MNC="China Mobile";
			}else if(MNCNum==01||MNCNum==06){
				MCC_MNC="China Unicom";
			}else if(MNCNum==03||MNCNum==05||MNCNum==11){
				MCC_MNC="China Telecom";
			}else if(MNCNum==020){
				MCC_MNC="China Tietong";
			}
			System.out.println("��Ӫ��"+MCC_MNC);
			byte[] LACByte = Arrays.copyOfRange(infoBytes, 21, 23);
			String LACStr = Hex.encodeHexString(LACByte);		//GSM����λ������
			System.out.println("GSM����λ������"+LACStr);
			byte[] CellIdByte = Arrays.copyOfRange(infoBytes, 23, 26);
			String CellIdStr=Hex.encodeHexString(CellIdByte);	
			System.out.println("ID"+CellIdStr);
			int serialNo = nettyMessage.getSerialNo(); 		//��Ϣ���к�
			System.out.println("��Ϣ���к�"+serialNo);
			BatteryStatusInfo batteryStatusInfo=new BatteryStatusInfo(  imei, longitude, latitude, batteryLevel, (short) 1, Date2Data.getSqlDate());
			try {
				dataService.saveBatteryStatus(batteryStatusInfo);
			} catch (Exception e2) {
				logger.error("�豸δע�ᣬ�޷�д��GPS����");
			}
			break;
		case "15":	//ָ����Ӧ
			logger.info("ָ����Ӧ");
		case "16": // ��������
			logger.warn("�ն��ϴ�����");
			ctx.writeAndFlush(Common.WarnBack);	//���ͱ�����Ӧ
			break;

		case "17": // ͸����Ϣ����   
			logger.info("�ն��ϴ�͸������");
			ctx.writeAndFlush(Common.TCBack);		//͸��������Ӧ
			break;
		case "18": // LBS ��λ���ݰ�
			ctx.writeAndFlush(Common.LBSBack);
			System.out.println("LBS��Ӧ�ѷ���");
			break;
		case "21": // �ն��ַ����ظ�
//			Timer tim = new Timer();	// ÿ��������һ��λ����Ϣ
//			TimerTask task = new TimerTask() {
//
//				@Override
//				public void run() {
//					ctx.write(Common.askLocation);
//					System.out.println("����һ��ָ��");
//
//				}
//			};
//			tim.schedule(task, 60000l); 

			break;

		default:
			break;
		}
		System.out.println("�豸ID: "+imei);
		System.out.println("��ص�����"+batteryLevel);
	}
}
