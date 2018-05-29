package com.micropace.ramp.wechat.mapper;

import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.CUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface CUserMapper extends IMyBatisSuperMapper<CUser> {

    @Select("select * from c_user where id_wx_app=#{idWxApp} and openid=#{openid}")
    CUser selectByOpenid(@Param("idWxApp") Long idWxApp, @Param("openid") String openid);

    @Update("update c_user set is_deleted=0 where id_wx_app=#{idWxApp} and openid=#{openid}")
    int active(@Param("idWxApp") Long idWxApp, @Param("openid") String openid);

    @Update("update c_user set is_deleted=1 where id_wx_app=#{idWxApp} and openid=#{openid}")
    void deleteByOpenid(@Param("idWxApp") Long idWxApp, @Param("openid") String openid);
}
