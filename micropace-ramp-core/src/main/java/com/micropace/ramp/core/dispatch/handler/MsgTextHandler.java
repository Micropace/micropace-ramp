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

        // 检查当前注册会话是否处于开启状态，是则由注册会话处理消息内容
        if (ibUserRegistService.isSessionActive(wxId, openid)) {
            int step = ibUserRegistService.getStep(wxId, openid);
            // step == 1，已开启注册会话，则进行手机号接收
            if(step == 1) {
                String errMsg = ibUserRegistService.submitMobile(wxId, openid, text);
                if (errMsg != null) {
                    return new ReplyTextBuilder().build(errMsg, wxMessage, weixinService);
                }
            }
            // step == 3，已发送短信验证码，则进行验证码校验
            if(step == 3) {
                String errMsg = ibUserRegistService.validate(wxId, openid, text);
                if(errMsg != null) {
                    return new ReplyTextBuilder().build(errMsg, wxMessage, weixinService);
                }
            }
        } else {
            // 如果消息是开启注册会话命令，则开启注册会话
            if("注册".equals(text)) {
                String errMsg = ibUserRegistService.applyRegist(wxId, openid);
                if (errMsg != null) {
                    return new ReplyTextBuilder().build(errMsg, wxMessage, weixinService);
                }
            }
        }

        // TODO 其它的会话过程检查，存在则交给这些会话处理消息内容

        //TODO 最后，如果没有开启的会话，则统一回复消息
        String content = "收到文本消息：" + text;
        return new ReplyTextBuilder().build(content, wxMessage, weixinService);
    }

}
