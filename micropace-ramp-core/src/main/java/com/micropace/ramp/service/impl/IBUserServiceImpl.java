package com.micropace.ramp.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.enums.RegisterStatusEnum;
import com.micropace.ramp.mapper.BUserMapper;
import com.micropace.ramp.service.IBUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IBUserServiceImpl extends ServiceImpl<BUserMapper, BUser> implements IBUserService {

    @Override
    public BUser selectByOpenid(Long idWxApp, String openid) {
        return super.baseMapper.selectByOpenid(idWxApp, openid);
    }

    @Override
    public BUser selectByBindedQrcodeId(Long idQrcode) {
        return super.baseMapper.selectByBindedQrcodeId(idQrcode);
    }

    @Override
    public BUser selectByMobile(String mobile) {
        return super.baseMapper.selectByMobile(mobile);
    }

    @Override
    public BUser selectByName(String name) {
        return super.baseMapper.selectByName(name);
    }

    @Override
    public boolean active(Long idWxApp, String openid) {
        return super.baseMapper.active(idWxApp, openid) == 1;
    }

    @Override
    public void deleteByOpenid(Long idWxApp, String openid) {
        super.baseMapper.deleteByOpenid(idWxApp, openid);
    }

    @Override
    public boolean isRegisted(Long idWxApp, String openid) {
        BUser bUser = super.baseMapper.selectByOpenid(idWxApp, openid);
        if(bUser != null) {
            return RegisterStatusEnum.SUCCESSED.getCode().equals(bUser.getStatus());
        }
        return false;
    }

    @Override
    public boolean isRegistProcessing(Long idWxApp, String openid) {
        BUser bUser = super.baseMapper.selectByOpenid(idWxApp, openid);
        if(bUser != null) {
            return RegisterStatusEnum.PROCESSING.getCode().equals(bUser.getStatus());
        }
        return false;
    }

    @Override
    public List<BUser> selectSpecifiedStatusList(Long idWxApp, RegisterStatusEnum status) {
        return super.baseMapper.selectSpecifiedStatusList(idWxApp, status.ordinal());
    }
}
