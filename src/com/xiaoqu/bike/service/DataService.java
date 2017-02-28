package com.xiaoqu.bike.service;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.xiaoqu.bike.dao.BatteryInfoMapper;
import com.xiaoqu.bike.dao.BatteryStatusInfoMapper;
import com.xiaoqu.bike.dto.BatteryInfo;
import com.xiaoqu.bike.dto.BatteryStatusInfo;

public class DataService {
	
	private static SqlSessionFactory sqlSessionFactory;
    
	static {
		DataService.init();
	}
	private static void init(){
		String resource = "mybatisConfig.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
        } catch (IOException e) {
            System.out.println(e.getMessage());

        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}
    //提交芯片上传的数据
	public void saveBatteryStatus(BatteryStatusInfo BatteryStatusInfo){
		 SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	
	        	BatteryStatusInfoMapper batteryStatusInfoMapper = sqlSession.getMapper(BatteryStatusInfoMapper.class);
	        	batteryStatusInfoMapper.insertSelective(BatteryStatusInfo);
	            
	            sqlSession.commit();// 这里一定要提交，不然数据进不去数据库中
	        } finally {
	            sqlSession.close();
	        }
	}
	
	//查询该设备是否已注册
	public boolean selectBatteryInfoByImei(String imei){
		boolean flag=false;
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			BatteryInfoMapper batteryInfoMapper = sqlSession.getMapper(BatteryInfoMapper.class);
			BatteryInfo batteryInfo = batteryInfoMapper.selectByImei(imei);
			if(batteryInfo==null){
				System.out.println("该设备尚未注册！");
			}else{
				flag=true;
			}
		} finally {
			sqlSession.close();
		}
		
		return flag;
	}
}
