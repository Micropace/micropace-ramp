package com.micropace.ramp.wechat.service;

import com.baomidou.mybatisplus.service.IService;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxAppType;

import java.util.List;

public interface IWxAppService extends IService<WxApp> {

    /**
     * 根据公众号原始ID查询已托管的公众号记录
     *
     * @param wxId 公众号原始ID
     * @return 公众号记录
     */
    WxApp selectByWxOriginId(String wxId);

    /**
     * 查询所有已托管公众号
     *
     * @return 列表
     */
    List<WxApp> selectAll();

    /**
     * 查询指定类型的所有已托管公众号
     *
     * @param type 公众号分类
     * @return 列表
     */
    List<WxApp> selectAllByType(WxAppType type);
}
