package com.micropace.ramp.core.dispatch.builder;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;

public class ReplyNewsBuilder extends AbstractBuilder {

    @Override
    public WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage, WxMpService service) {

        WxMpXmlOutNewsMessage.Item art1 = new WxMpXmlOutNewsMessage.Item();
        art1.setTitle("关于组织编纂《中国物流年鉴》（2017版）的通知");
        art1.setPicUrl("http://6ac081b6.ngrok.io/0.jpeg");
        //art1.setUrl("https://mp.weixin.qq.com/s?__biz=MzA5MzUyMDgwNw==&tempkey=OTYzX20zQnpqUzlzcS9CSy9ocEZMTlpiZW82Nl9KTTY4R1Fqc1JnUGJlbVRGZ04zeHdFYnpMWDlPNkxsdGNBUWF4S21MNlhTWU85bVV5VnBPQXd2NW9JRDN3emktbTNJS0RCR3N5VnlpSmhTMG45QW9MZktqdGxaUTVvb01jU2t4c0NfaGF1RTJHbW45ZmlFTmpVSnlKcTU2RVhWSUtDcnQwcGQ4UUl5SWd%2Bfg%3D%3D&chksm=084737223f30be3441be1bf50bd6da5ae60f75fa721e6f8deb94c0c99d0d42b24aade9b37113#rd");
        art1.setDescription("关于组织编纂《中国物流年鉴》（2017版）的通知");

        WxMpXmlOutNewsMessage.Item art2 = new WxMpXmlOutNewsMessage.Item();
        art2.setTitle("订阅丨欢迎订阅《中国物流年鉴》（2016版）");
        //art2.setUrl("https://mp.weixin.qq.com/s?__biz=MzA5MzUyMDgwNw==&tempkey=OTYzXzcybEg5RmFmT2grTE5ZVU9MTlpiZW82Nl9KTTY4R1Fqc1JnUGJlbVRGZ04zeHdFYnpMWDlPNkxsdGNBWGpPTlljSkhkV3l0Nl9yLWY4T3BuS05zQkd3ZnZFOXBCcGE1UU40WXUwTHFiY1ljd2tyYUQ5aTNHODdfOHBvLUNueXBkUkVfaEpra2dISzZSem5kVUtaNGlWQlhhVy1YSDEyLXRqQkZleVF%2Bfg%3D%3D&chksm=084737223f30be34fcdf200ee93d04017e5539773995e9d3ba6d70b3538f18f592332e143ca3#rd");
        art2.setDescription("订阅丨欢迎订阅《中国物流年鉴》（2016版）");

        return WxMpXmlOutMessage
                .NEWS()
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .addArticle(art1)
                .addArticle(art2)
                .build();
    }
}
