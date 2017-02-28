package com.xiaoqu.bike.utils;


public class AddSerNoUtil {
	//输入消息头，信息体，和序号，返回响应信息
	public static byte[] add(byte [] head,byte [] info,int i){
		byte [] serNo=new byte[4];
		serNo[3]=(byte)((i>>24) & 0xFF);
		serNo[2]=(byte)((i>>16) & 0xFF);
		serNo[1]=(byte)((i>>8) & 0xFF);
		serNo[0]=(byte)(i & 0xFF);
		
		byte [] b=new byte[info.length+serNo.length];
		System.arraycopy(info, 0, b, 0, info.length);
		System.arraycopy(serNo, 0, b, info.length, serNo.length);
		
		int getCrc16 = CRCUtil.GetCrc16(b, b.length);
		String checkstr = Integer.toHexString(getCrc16);
		checkstr = checkstr.substring(checkstr.length() - 4);
		int int1 = Integer.parseInt(checkstr, 16);
		
		byte[] src = new byte[6];  
		src[3] =  (byte) ((int1>>24) & 0xFF);  
		src[2] =  (byte) ((int1>>16) & 0xFF);  
		src[1] =  (byte) ((int1>>8) & 0xFF);    
		src[0] =  (byte) (int1 & 0xFF);
		src[4]=(byte)0x0d;
		src[5]=(byte)0x0a;
		byte [] a=new byte[b.length+src.length];
		System.arraycopy(b, 0, a, 0, b.length);
		System.arraycopy(src, 0, a, b.length, src.length);
		
		byte [] c=new byte[a.length+2];
		System.arraycopy(head, 0, c, 0, head.length);
		System.arraycopy(a, 0, c, head.length, c.length);
		return c;
		
	}
}
