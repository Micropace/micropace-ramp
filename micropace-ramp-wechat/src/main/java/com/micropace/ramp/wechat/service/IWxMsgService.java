package com.micropace.ramp.wechat.service;

import com.micropace.ramp.base.entity.WxApp;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 微信公众号消息服务管理
 *
 * @author Suffrajet
 */
public interface IWxMsgService {

    /**
     * 获取指定微信公众号的本地存储配置
     *
     * @param wxApp 公众号
     * @return 本地存储配置
     */
    WxMpConfigStorage getMsgConfigStorage(WxApp wxApp);

    /**
     * 获取微信公众号的消息服务接口
     *
     * @param storage 公众号的本地存储配置
     * @return 消息服务接口
     */
    WxMpService getMsgService(WxMpConfigStorage storage);

    /**
     * 获取微信公众号的消息路由
     *
     * @param service 消息服务接口
     * @return 路由
     */
    WxMpMessageRouter getMsgRouter(WxMpService service);
}
