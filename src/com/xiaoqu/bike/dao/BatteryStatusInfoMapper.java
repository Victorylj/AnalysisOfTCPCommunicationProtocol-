package com.xiaoqu.bike.dao;

import com.xiaoqu.bike.dto.BatteryStatusInfo;

public interface BatteryStatusInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BatteryStatusInfo record);

    int insertSelective(BatteryStatusInfo record);

    BatteryStatusInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BatteryStatusInfo record);

    int updateByPrimaryKey(BatteryStatusInfo record);
}