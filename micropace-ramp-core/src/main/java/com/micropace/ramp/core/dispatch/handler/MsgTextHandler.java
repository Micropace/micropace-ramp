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

        WxMpXmlOutMessage reply = this.handleRegisterSessionMsg(wxMessage, weixinService);
        if(reply != null) {
            return reply;
        }

        // TODO 其它的会话过程检查，存在则交给这些会话处理消息内容

        //TODO 最后，如果没有开启的会话，则统一回复消息
        String content = "收到文本消息：" + wxMessage.getContent();
        return new ReplyTextBuilder().build(content, wxMessage, weixinService);
    }

    /**
     * 检查当前注册会话是否处于开启状态
     * 是则由注册会话处理消息内容，否则返回null
     */
    private WxMpXmlOutMessage
    handleRegisterSessionMsg(WxMpXmlMessage wxMessage, WxMpService weixinService) {
        String wxId   = wxMessage.getToUser();
        String openid = wxMessage.getFromUser();
        String text   = wxMessage.getContent();

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
        return null;
    }

}
