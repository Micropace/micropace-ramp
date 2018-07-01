package com.micropace.ramp.core.dispatch.handler;

import com.micropace.ramp.core.dispatch.builder.ReplyNewsBuilder;
import com.micropace.ramp.core.dispatch.builder.ReplyTextBuilder;
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

            // 处理物流公众号的菜单点击事件回复
            WxMpXmlOutMessage reply = this.handleClpSignMenuMessage(wxMessage, weixinService);
            if(reply != null) {
                return reply;
            }


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

    private WxMpXmlOutMessage
    handleClpSignMenuMessage(WxMpXmlMessage wxMessage, WxMpService weixinService) {
        String wxOriginId = wxMessage.getToUser();
        String eventKey   = wxMessage.getEventKey();
        // 物流公众号ID：gh_c1fa7b2721b5
        if(wxOriginId.equals("gh_c1fa7b2721b5")) {
            // key_zhaoshang 招商合作
            if(eventKey.equals("key_zhaoshang")) {
                String text = "物流年会的招商合作事宜，请直接拨打下方电话咨询详情。\n" +
                        "联系人：吴海波/宇颖/李斐/高威\n" +
                        "联系电话：010-68392974/2278/2292/1021";
                return new ReplyTextBuilder().build(text, wxMessage, weixinService);
            }
            // key_dingyue 我要订阅
            if(eventKey.equals("key_dingyue")) {
                String text = "读者您好，《中国物流与采购》杂志接受订阅！您可通过登录我刊官网进行网上订阅或者给我刊邮箱发送订阅邮件，订阅详情咨询请您拨打：010-68392214";
                return new ReplyTextBuilder().build(text, wxMessage, weixinService);
            }
            // key_tougao 我要投稿
            if(eventKey.equals("key_tougao")) {
                String text = "本刊接受投稿，如有投稿意向，请您发送稿件到cflpma@126.com邮箱。\n" +
                        "投稿详情咨询，请您拨打：010-68392284/2187";
                return new ReplyTextBuilder().build(text, wxMessage, weixinService);
            }
            // key_hezuo 新媒体合作
            if(eventKey.equals("key_hezuo")) {
                String text = "新媒体合作请直接拨打电话：010-68392284/2187";
                return new ReplyTextBuilder().build(text, wxMessage, weixinService);
            }
            // 物流年鉴图文
            if(eventKey.equals("key_nianjian")) {
                return new ReplyNewsBuilder().build("", wxMessage, weixinService);
            }
        }
        return null;
    }
}
