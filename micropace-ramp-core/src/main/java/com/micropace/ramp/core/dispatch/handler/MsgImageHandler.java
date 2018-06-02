package com.micropace.ramp.core.dispatch.handler;

import com.micropace.ramp.core.dispatch.builder.ReplyTextBuilder;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MsgImageHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        if (wxMessage.getMsgType().equals(WxConsts.XmlMsgType.IMAGE)) {
            //TODO 接收处理用户发送的图片消息
            try {
                String content = "收到上传的图片, " + wxMessage.getPicUrl();
                return new ReplyTextBuilder().build(content, wxMessage, null);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
