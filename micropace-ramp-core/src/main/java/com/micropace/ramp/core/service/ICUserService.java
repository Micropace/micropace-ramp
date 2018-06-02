package com.micropace.ramp.core.service;

import com.baomidou.mybatisplus.service.IService;
import com.micropace.ramp.base.entity.CUser;

public interface ICUserService extends IService<CUser> {

    CUser selectByOpenid(Long idWxApp, String openid);

    boolean active(Long idWxApp, String openid);

    void deleteByOpenid(Long idWxApp, String openid);
}
