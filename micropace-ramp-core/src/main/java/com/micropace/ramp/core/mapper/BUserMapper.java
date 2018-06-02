package com.micropace.ramp.core.mapper;

import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.BUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BUserMapper extends IMyBatisSuperMapper<BUser> {

    @Select("select * from b_user where id_wx_app=#{idWxApp} and openid=#{openid}")
    BUser selectByOpenid(@Param("idWxApp") Long idWxApp, @Param("openid") String openid);

    @Select("select * from b_user where id_qrcode=#{idQrcode}")
    BUser selectByBindedQrcodeId(@Param("idQrcode") Long idQrcode);

    @Select("select * from b_user where mobile=#{mobile}")
    BUser selectByMobile(@Param("mobile") String mobile);

    @Select("select * from b_user where name=#{name}")
    BUser selectByName(@Param("name") String name);

    @Update("update b_user set is_deleted=0 where id_wx_app=#{idWxApp} and openid=#{openid}")
    int active(@Param("idWxApp") Long idWxApp, @Param("openid") String openid);

    @Update("update b_user set is_deleted=1 where id_wx_app=#{idWxApp} and openid=#{openid}")
    void deleteByOpenid(@Param("idWxApp") Long idWxApp, @Param("openid") String openid);

    /**
     * 查询公众号下指定审核状态的Lord列表
     *
     * @param idWxApp 公众号记录ID
     * @param status  状态
     * @return Lord列表
     */
    @Select("select * from b_user where id_wx_app=#{idWxApp} and status=#{status}")
    List<BUser> selectSpecifiedStatusList(@Param("idWxApp") Long idWxApp, @Param("status") Integer status);

}
