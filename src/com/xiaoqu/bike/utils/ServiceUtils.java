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
	static String imei="";			//imei码
	static int batteryLevel=6;		//电池电量
	
	public static void serverService(ChannelHandlerContext ctx,NettyMessage nettyMessage){
		
		
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("接受信息的协议号：  "+nettyMessage.getKey());
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		int batteryLevel=6;		//电池电量
//		String imei="";			//imei码
		switch (nettyMessage.getKey()) { // 业务逻辑
		case "01": // 登陆信息
			System.out.println("登陆信息");
			logger.info("登录信息");
			nettyMessage.getInfoBytes();
			imei = Hex.encodeHexString(nettyMessage.getInfoBytes()).substring(1);
//			System.out.println("设备imei码为："+imei);
			boolean flag = dataService.selectBatteryInfoByImei(imei);
			if(flag){
				ctx.writeAndFlush(Common.loginBack);	//已注册设备，发送登陆响应包
			}else{
				ctx.writeAndFlush(Common.loginfalseBack);	//未注册设备，发送响应包
				imei=null;
			}
			
			System.out.println("设备imei码为："+imei+"   #登陆响应已发送");
			

			break;
		case "13": // 状态信息
			byte[] infoBytes2 = nettyMessage.getInfoBytes();
			batteryLevel=infoBytes2[1];
			System.out.println("状态信息");
			byte [] head={0x78,0x78};
			byte [] info={0x05,0x13};
			i+=i;
//			byte[] statusBack = AddSerNoUtil.add(head, info, i);
			
			ctx.writeAndFlush(Common.statusBack);
			System.out.println("心跳包响应已发送");
			break;

		case "12": // GPS 定位数据
			
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
			SimpleDateFormat sd=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
			
			@SuppressWarnings({ "deprecation", "static-access" })
			long utcTime = date.UTC(year, month, day, hrs, min, sec);//上传的时间信息
			System.out.println("时间"+sd.format(utcTime));
			logger.info("上传时间为"+sd.format(utcTime));
			byte[] lat = Arrays.copyOfRange(infoBytes, 7, 11);
			String latencodeHexString = Hex.encodeHexString(lat);
			System.out.println(latencodeHexString);
			double ss = Long.parseLong(latencodeHexString, 16);	//纬度
			double a=30000;
			double c=ss/a; 
			System.out.println(c);
			int d= (int) (c/60); double e=(c%60)/100;
			String latitude=(double)d+e+"";
			System.out.println("纬度"+latitude);
			
			byte[] longit = Arrays.copyOfRange(infoBytes, 11, 15);
			String longitencodeHexString = Hex.encodeHexString(longit);
			System.out.println(longitencodeHexString);
			double ss1 = Long.parseLong(longitencodeHexString, 16);		//经度
			double c1=ss1/a; 
			int d1= (int) (c1/60); double e1=(c%60)/100;
			String longitude=(double)d1+e1+"";
			System.out.println("经度"+longitude);
			
			int speed=infoBytes[15];		//当前速度
			System.out.println("速度"+speed);
			String MCC_MNC="";		//移动运营商
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
			System.out.println("运营商"+MCC_MNC);
			byte[] LACByte = Arrays.copyOfRange(infoBytes, 21, 23);
			String LACStr = Hex.encodeHexString(LACByte);		//GSM网络位置区号
			System.out.println("GSM网络位置区号"+LACStr);
			byte[] CellIdByte = Arrays.copyOfRange(infoBytes, 23, 26);
			String CellIdStr=Hex.encodeHexString(CellIdByte);	
			System.out.println("ID"+CellIdStr);
			int serialNo = nettyMessage.getSerialNo(); 		//信息序列号
			System.out.println("信息序列号"+serialNo);
			BatteryStatusInfo batteryStatusInfo=new BatteryStatusInfo(  imei, longitude, latitude, batteryLevel, (short) 1, Date2Data.getSqlDate());
			try {
				dataService.saveBatteryStatus(batteryStatusInfo);
			} catch (Exception e2) {
				logger.error("设备未注册，无法写入GPS数据");
			}
			break;
		case "15":	//指令响应
			logger.info("指令响应");
		case "16": // 报警数据
			logger.warn("终端上传警报");
			ctx.writeAndFlush(Common.WarnBack);	//发送报警响应
			break;

		case "17": // 透传信息数据   
			logger.info("终端上传透传数据");
			ctx.writeAndFlush(Common.TCBack);		//透传数据响应
			break;
		case "18": // LBS 定位数据包
			ctx.writeAndFlush(Common.LBSBack);
			System.out.println("LBS响应已发送");
			break;
		case "21": // 终端字符串回复
//			Timer tim = new Timer();	// 每分钟请求一次位置信息
//			TimerTask task = new TimerTask() {
//
//				@Override
//				public void run() {
//					ctx.write(Common.askLocation);
//					System.out.println("发送一次指令");
//
//				}
//			};
//			tim.schedule(task, 60000l); 

			break;

		default:
			break;
		}
		System.out.println("设备ID: "+imei);
		System.out.println("电池电量："+batteryLevel);
	}
}
