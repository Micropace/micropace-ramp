package com.micropace.ramp.core.mapper;

import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.WxApp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface WxAppMapper extends IMyBatisSuperMapper<WxApp> {

    @Select("select * from wx_app where wx_id = #{wxId}")
    WxApp selectByWxOriginId(@Param("wxId") String wxId);

    @Select("select * from wx_app")
    List<WxApp> selectAll();

    @Select("select * from wx_app where type=#{type}")
    List<WxApp> selectAllByType(@Param("type") Integer type);

    @Select("select * from wx_app where category=#{category}")
    List<WxApp> selectAllByCategory(@Param("category") Integer category);
}
