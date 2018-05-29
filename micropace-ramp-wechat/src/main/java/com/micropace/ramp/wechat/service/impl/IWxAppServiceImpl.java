package com.micropace.ramp.wechat.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxAppType;
import com.micropace.ramp.wechat.mapper.WxAppMapper;
import com.micropace.ramp.wechat.service.IWxAppService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IWxAppServiceImpl extends ServiceImpl<WxAppMapper, WxApp> implements IWxAppService {

    @Override
    public WxApp selectByWxOriginId(String wxId) {
        return super.baseMapper.selectByWxOriginId(wxId);
    }

    @Override
    public List<WxApp> selectAll() {
        return super.baseMapper.selectAll();
    }

    @Override
    public List<WxApp> selectAllByType(WxAppType type) {
        return super.baseMapper.selectAllByType(type.getCode());
    }
}
