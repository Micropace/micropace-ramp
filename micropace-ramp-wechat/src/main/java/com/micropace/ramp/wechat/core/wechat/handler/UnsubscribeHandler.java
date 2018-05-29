package com.micropace.ramp.wechat.core.wechat.handler;

import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxAppType;
import com.micropace.ramp.wechat.service.ICUserService;
import com.micropace.ramp.wechat.service.IBUserService;
import com.micropace.ramp.wechat.service.IWxAppService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UnsubscribeHandler extends AbstractHandler {

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IBUserService IBUserService;
    @Autowired
    private ICUserService ICUserService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        String wxId = wxMessage.getToUser();
        String openId = wxMessage.getFromUser();
        this.logger.info("取消关注用户 OPENID: " +  openId);
        this.freezeUser(wxId, openId);
        return null;
    }

    /**
     * 逻辑删除用户
     */
    private void freezeUser(String wxId, String openid) {
        try {
            WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
            if (wxApp != null) {
                // B类型公众号的用户
                if (WxAppType.TYPE_B.getCode().equals(wxApp.getType())) {
                    IBUserService.deleteByOpenid(wxApp.getId(), openid);
                }
                // C类型公众号的用户
                if(WxAppType.TYPE_C.getCode().equals(wxApp.getType())) {
                    ICUserService.deleteByOpenid(wxApp.getId(), openid);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
