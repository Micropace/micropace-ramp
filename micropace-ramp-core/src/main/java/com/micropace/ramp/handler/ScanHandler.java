package com.micropace.ramp.handler;

import com.micropace.ramp.builder.TextBuilder;
import com.micropace.ramp.base.entity.CUser;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.service.ICUserService;
import com.micropace.ramp.service.IRelationService;
import com.micropace.ramp.service.IWxAppService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScanHandler extends AbstractHandler {

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private ICUserService ICUserService;
    @Autowired
    private IRelationService iRelationService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        // 只有已关注C公众号的用户，扫描了携带场景值的公众号二维码后才会进入到这里
        // 此处处理Fans用户和Lord的关系绑定
        try {
            String qrcodeWxId = wxMessage.getToUser();
            String openid = wxMessage.getFromUser();
            String sceneStr = wxMessage.getEventKey();

            WxApp wxApp = iWxAppService.selectByWxOriginId(qrcodeWxId);
            if(wxApp != null) {
                CUser fans = ICUserService.selectByOpenid(wxApp.getId(), openid);
                if(fans != null) {
                    if (iRelationService.followBUser(fans, wxApp.getWxId(), sceneStr)) {
                        logger.info("Create fans {} relation", fans.getNickname());
                        return new TextBuilder().build("扫码事件, 场景值: " + wxMessage.getEventKey(), wxMessage, weixinService);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }
}
