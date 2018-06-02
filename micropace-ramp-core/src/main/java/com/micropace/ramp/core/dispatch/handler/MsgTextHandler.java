package com.micropace.ramp.core.dispatch.handler;

import com.micropace.ramp.core.dispatch.builder.ReplyTextBuilder;
import com.micropace.ramp.core.service.IBUserRegistService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

@Component
public class MsgTextHandler extends AbstractHandler {

    @Autowired
    private IBUserRegistService ibUserRegistService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        String wxId   = wxMessage.getToUser();
        String openid = wxMessage.getFromUser();

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                    && weixinService.getKefuService().kfOnlineList()
                    .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        String text = wxMessage.getContent();

        // TODO 检查注册会话是否开启，开启则由注册会话接管消息处理
        if (ibUserRegistService.isSessionActive(wxId, openid)) {

        }

        //TODO 组装回复消息
        String content = "收到文本消息：" + text;
        return new ReplyTextBuilder().build(content, wxMessage, weixinService);
    }

}
