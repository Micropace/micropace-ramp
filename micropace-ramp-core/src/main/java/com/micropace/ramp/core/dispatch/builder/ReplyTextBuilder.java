package com.micropace.ramp.core.dispatch.builder;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public class ReplyTextBuilder extends AbstractBuilder {

    @Override
    public WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage, WxMpService service) {
        return WxMpXmlOutMessage
                .TEXT()
                .content(content)
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build();
    }
}
