package com.xiaoqu.bike.dao;

import com.xiaoqu.bike.dto.BatteryInfo;

public interface BatteryInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BatteryInfo record);

    int insertSelective(BatteryInfo record);

    BatteryInfo selectByPrimaryKey(Integer id);
    
    BatteryInfo selectByImei(String imei);

    int updateByPrimaryKeySelective(BatteryInfo record);

    int updateByPrimaryKey(BatteryInfo record);
}