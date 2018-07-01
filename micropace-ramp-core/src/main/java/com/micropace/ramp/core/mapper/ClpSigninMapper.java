package com.micropace.ramp.core.mapper;

import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.ClpSignin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ClpSigninMapper extends IMyBatisSuperMapper<ClpSignin> {

    @Select("select * from clp_signin where mobile=#{mobile}")
    ClpSignin selectByMobile(@Param("mobile") String mobile);

    @Update("update clp_signin set sign_in_time=#{signInTime} where id=#{id}")
    int updateSignInTime(@Param("id") Long id, @Param("signInTime") String signInTime);
}
