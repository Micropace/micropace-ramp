package com.micropace.ramp.core.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.ClpSignin;
import com.micropace.ramp.core.mapper.ClpSigninMapper;
import com.micropace.ramp.core.service.IClpSigninService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IClpSigninServiceImpl extends ServiceImpl<ClpSigninMapper, ClpSignin> implements IClpSigninService {

    @Override
    public ClpSignin selectByMobile(String mobile) {
        return super.baseMapper.selectByMobile(mobile);
    }

    @Override
    public boolean updateSignInTime(Long id, String signInTime) {
        return super.baseMapper.updateSignInTime(id, signInTime) == 1;
    }

    @Override
    public List<ClpSignin> selectAll() {
        return super.baseMapper.selectAll();
    }
}
