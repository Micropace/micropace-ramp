package com.micropace.ramp.core.dispatch.handler;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.micropace.ramp.base.entity.ClpSignin;
import com.micropace.ramp.base.util.ValidatorUtil;
import com.micropace.ramp.core.dispatch.builder.ReplyImageBuilder;
import com.micropace.ramp.core.dispatch.builder.ReplyTextBuilder;
import com.micropace.ramp.core.service.IBUserRegistService;
import com.micropace.ramp.core.service.IClpSigninService;
import com.micropace.ramp.core.service.ISmsService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class MsgTextHandler extends AbstractHandler {

    @Autowired
    private IBUserRegistService ibUserRegistService;

    @Autowired
    private ISmsService iSmsService;
    @Autowired
    private IClpSigninService iClpSigninService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        // 注册会话检测
        WxMpXmlOutMessage reply = this.handleRegisterSessionMsg(wxMessage, weixinService);
        if(reply != null) {
            return reply;
        }

        // TODO 其它的会话过程检查，存在则交给这些会话处理消息内容

        // 这里处理小程序二维码获取的关键字
        reply = this.handleLoveselfQrcodeQuery(wxMessage, weixinService);
        if(reply != null) {
            return reply;
        }

        // 这里处理物流公众号的签到关键字
        reply = this.handleClpSigninMessage(wxMessage, weixinService);
        if(reply != null) {
            return reply;
        }


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
    handleLoveselfQrcodeQuery(WxMpXmlMessage wxMessage, WxMpService weixinService) {
        String wxId   = wxMessage.getToUser();
        String openid = wxMessage.getFromUser();
        String text   = wxMessage.getContent();

        // 放在无量聚公众号里处理
        if(wxId.equals("gh_54d94f90fc35")) {

            // 获取上传的二维码图片素材列表
            List<WxMpMaterialFileBatchGetResult.WxMaterialFileBatchGetNewsItem> mediaItems = null;
            try {
                WxMpMaterialFileBatchGetResult picMedia = weixinService.getMaterialService()
                        .materialFileBatchGet(WxConsts.MediaFileType.IMAGE, 0, 20);
                mediaItems = picMedia.getItems();
            } catch (WxErrorException e) {
                e.printStackTrace();
            }

            if(mediaItems != null && mediaItems.size() > 0) {
                String scene = null;
                // mediaId = p4M33l5e65nhHj40G2GyezI_IgCTH3T9EbTNLEoLGhA
                if (text.equals("超级科室会1")) scene = "10001";
                // mediaId = CDATA[p4M33l5e65nhHj40G2Gye59uAW895vyVi9mjk8W5t4A
                if (text.equals("超级科室会2")) scene = "10002";
                // mediaId = CDATA[p4M33l5e65nhHj40G2Gye3xi3KT0yDysWn8GT-_2pgA
                if (text.equals("超级拜访")) scene = "10003";

                String mediaId = this.getQrcodeMediaId(mediaItems, scene);
                if(scene != null && mediaId != null) {
                    return new ReplyImageBuilder().build(mediaId, wxMessage, weixinService);
                }
            }
        }
        return null;
    }
    private String getQrcodeMediaId(List<WxMpMaterialFileBatchGetResult.WxMaterialFileBatchGetNewsItem> mediaItems, String scene) {
        for (WxMpMaterialFileBatchGetResult.WxMaterialFileBatchGetNewsItem mediaItem : mediaItems) {
            if(mediaItem.getName().equals(scene + ".jpg")) {
                return mediaItem.getMediaId();
            }
        }
        return null;
    }

    // 这里处理物流公众号的签到关键字
    private WxMpXmlOutMessage
    handleClpSigninMessage(WxMpXmlMessage wxMessage, WxMpService weixinService) {
        String wxOriginId = wxMessage.getToUser();
        String text = wxMessage.getContent();
        // wxid： gh_c1fa7b2721b5
        // appid: wxd92e67b8e6b91fb5
        // appsecret: 5c77ed02b2521aa77512c1cfeb339a2d
        if("gh_c1fa7b2721b5".equals(wxOriginId)) {
            if(text.length() == 11 && ValidatorUtil.isMobile(text)) {
                ClpSignin clpSignin = iClpSigninService.selectByMobile(text);
                if(clpSignin != null) {
                    // 1. 发送手机短信. 记录ID作为短信编号
                    SendSmsResponse response = iSmsService.sendClpSignInMessage(clpSignin.getMobile(), clpSignin.getName(), clpSignin.getId());
                    if(response != null) {
                        System.out.println(response.toString());
                        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        iClpSigninService.updateSignInTime(clpSignin.getId(), nowTime);
                    }
                    // 2. 回复成功TEXT消息
                    String successMsg = "您已签到成功，您的手机将收到一条签到确认短信。若您未收到短信，请向现场工作人员问询。";
                    return new ReplyTextBuilder().build(successMsg, wxMessage, weixinService);
                } else {
                    // 3. 回复失败TEXT消息
                    String errorMsg = "签到未成功，没有查到您的参会预约，请确认您发送的手机号码是否正确！";
                    return new ReplyTextBuilder().build(errorMsg, wxMessage, weixinService);
                }
            } else {
                String clpReply = "您的留言已收到，感谢对本平台的关注和大力支持\n" +
                        "如需检索物流行业信息，请点击菜单栏↓↓↓里的“号内搜”，输入关键词获取。\n" +
                        "如有意见或者建议，请直接给小编留言，我们会在看到的第一时间回复您";
                return new ReplyTextBuilder().build(clpReply, wxMessage, weixinService);
            }
        }
        return null;
    }
}
