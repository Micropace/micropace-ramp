package com.micropace.ramp.core.dispatch.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

@Component
public class MenuHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        // click类型的菜单事件处理
        if(MenuButtonType.CLICK.equals(wxMessage.getEvent().toLowerCase())) {

            // TODO 点击事件处理

            String msg = String.format("类型: %s, 事件: %s, 自定义Key: %s",
                    wxMessage.getMsgType(), wxMessage.getEvent(), wxMessage.getEventKey());
            return WxMpXmlOutMessage.TEXT()
                    .content(msg)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }
        return null;
    }
}
