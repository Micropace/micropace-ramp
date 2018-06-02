package com.micropace.ramp.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.CUser;
import com.micropace.ramp.mapper.CUserMapper;
import com.micropace.ramp.service.ICUserService;
import org.springframework.stereotype.Service;

@Service
public class ICUserServiceImpl extends ServiceImpl<CUserMapper, CUser> implements ICUserService {

    @Override
    public CUser selectByOpenid(Long idWxApp, String openid) {
        return super.baseMapper.selectByOpenid(idWxApp, openid);
    }

    @Override
    public boolean active(Long idWxApp, String openid) {
        return super.baseMapper.active(idWxApp, openid) == 1;
    }

    @Override
    public void deleteByOpenid(Long idWxApp, String openid) {
        super.baseMapper.deleteByOpenid(idWxApp, openid);
    }
}
