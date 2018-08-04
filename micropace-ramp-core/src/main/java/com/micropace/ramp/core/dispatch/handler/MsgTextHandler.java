package com.micropace.ramp.core.dispatch.handler;

import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.core.dispatch.builder.ReplyTextBuilder;
import com.micropace.ramp.core.service.*;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MsgTextHandler extends AbstractHandler {

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IBUserService ibUserService;
    @Autowired
    private IBUserRegistService ibUserRegistService;
    @Autowired
    private IThirdPartApiCallService iThirdPartApiCallService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        // 注册会话检测
        WxMpXmlOutMessage reply = this.handleRegisterSessionMsg(wxMessage, weixinService);
        if(reply != null) {
            return reply;
        }

        // 这里处理小程序二维码获取的关键字
        reply = this.handleLoveselfQrcodeQueryMsg(wxMessage, weixinService);
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

    // 这里处理小程序二维码获取的关键字
    private WxMpXmlOutMessage
    handleLoveselfQrcodeQueryMsg(WxMpXmlMessage wxMessage, WxMpService weixinService) {
        String wxId   = wxMessage.getToUser();
        String openid = wxMessage.getFromUser();
        String text   = wxMessage.getContent();

        // 放在无量聚公众号里处理
        if(wxId.equals("gh_54d94f90fc35")) {
            WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
            BUser bUser = ibUserService.selectByOpenid(wxApp.getId(), openid);
            if(bUser == null || bUser.getMobile() == null) {
                String reply = "请注册后重试";
                return new ReplyTextBuilder().build(reply, wxMessage, weixinService);
            }

            // 使用B公众号用户注册的手机号 TODO 这里关键字和题ID的对应需要一个表去维护
            String mobile = bUser.getMobile();
            String loveselfScene = null;
            if (text.equals("超级科室会1")) {
                loveselfScene = mobile + 1;
            }
            if (text.equals("超级科室会2")) {
                loveselfScene = mobile + 2;
            }
            if (text.equals("超级拜访")) {
                loveselfScene = mobile + 3;
            }
            System.out.println(loveselfScene);
            String qrcodeUrl = iThirdPartApiCallService.getLoveselfQrcodePic(loveselfScene);
            if(qrcodeUrl == null) {
                qrcodeUrl = iThirdPartApiCallService.createLoveselfQrcode(loveselfScene);
            }

            // 再次判断是否创建成功
            if(qrcodeUrl == null) {
                return new ReplyTextBuilder().build("您不是代表, 无法获取答题小程序二维码", wxMessage, weixinService);
            }

            WxMpXmlOutNewsMessage.Item art1 = new WxMpXmlOutNewsMessage.Item();
            art1.setTitle("答题二维码");
            art1.setPicUrl(qrcodeUrl);
            art1.setUrl(qrcodeUrl);
            return WxMpXmlOutMessage
                    .NEWS()
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .addArticle(art1)
                    .build();
        }
        return null;
    }
}
