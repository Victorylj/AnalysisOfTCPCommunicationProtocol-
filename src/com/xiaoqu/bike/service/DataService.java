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
    //�ύоƬ�ϴ�������
	public void saveBatteryStatus(BatteryStatusInfo BatteryStatusInfo){
		 SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	
	        	BatteryStatusInfoMapper batteryStatusInfoMapper = sqlSession.getMapper(BatteryStatusInfoMapper.class);
	        	batteryStatusInfoMapper.insertSelective(BatteryStatusInfo);
	            
	            sqlSession.commit();// ����һ��Ҫ�ύ����Ȼ���ݽ���ȥ���ݿ���
	        } finally {
	            sqlSession.close();
	        }
	}
	
	//��ѯ���豸�Ƿ���ע��
	public boolean selectBatteryInfoByImei(String imei){
		boolean flag=false;
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			BatteryInfoMapper batteryInfoMapper = sqlSession.getMapper(BatteryInfoMapper.class);
			BatteryInfo batteryInfo = batteryInfoMapper.selectByImei(imei);
			if(batteryInfo==null){
				System.out.println("���豸��δע�ᣡ");
			}else{
				flag=true;
			}
		} finally {
			sqlSession.close();
		}
		
		return flag;
	}
}
