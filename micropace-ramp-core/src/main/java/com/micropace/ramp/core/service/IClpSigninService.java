package com.micropace.ramp.core.service;

import com.baomidou.mybatisplus.service.IService;
import com.micropace.ramp.base.entity.ClpSignin;

public interface IClpSigninService extends IService<ClpSignin> {

    ClpSignin selectByMobile(String mobile);

    boolean updateSignInTime(Long id, String signInTime);
}
