package com.micropace.ramp.core.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxAppCategoryEnum;
import com.micropace.ramp.base.enums.WxTypeEnum;
import com.micropace.ramp.core.mapper.WxAppMapper;
import com.micropace.ramp.core.service.IWxAppService;
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
    public List<WxApp> selectAllByType(WxTypeEnum type) {
        return super.baseMapper.selectAllByType(type.getCode());
    }

    @Override
    public List<WxApp> selectAllByCategory(WxAppCategoryEnum category) {
        return super.baseMapper.selectAllByCategory(category.getCode());
    }
}
