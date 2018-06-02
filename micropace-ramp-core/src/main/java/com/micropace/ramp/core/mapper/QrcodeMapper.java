package com.micropace.ramp.core.mapper;


import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.Qrcode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QrcodeMapper extends IMyBatisSuperMapper<Qrcode> {

    @Select("select * from qrcode where id_wx_app=#{idWxApp} and scene_str=#{sceneStr}")
    Qrcode selectBySceneStr(@Param("idWxApp") Long idWxApp, @Param("sceneStr") String sceneStr);

    @Select("select * from qrcode where id_wx_app=#{idWxApp} and type=#{type} and is_bind=#{isBind}")
    List<Qrcode> selectSpecifiedTypeRecords(@Param("idWxApp") Long idWxApp, @Param("type") Integer type, @Param("isBind") Integer isBind);

    @Select("select * from qrcode where id_wx_app=#{idWxApp} and type=#{type} and is_bind=0 limit 1")
    Qrcode selectOneNotBindedByType(@Param("idWxApp") Long idWxApp, @Param("type") Integer type);

}
