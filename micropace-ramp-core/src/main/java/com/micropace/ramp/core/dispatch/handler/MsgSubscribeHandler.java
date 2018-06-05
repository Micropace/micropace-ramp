package com.micropace.ramp.core.dispatch.handler;

import com.micropace.ramp.base.enums.RegisterStatusEnum;
import com.micropace.ramp.base.enums.WxAppCategoryEnum;
import com.micropace.ramp.core.dispatch.builder.ReplyTextBuilder;
import com.micropace.ramp.base.entity.*;
import com.micropace.ramp.core.service.*;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户的关注有多种方式
 *
 * 1、搜索名称关注，普通类型的关注，创建或者激活公众号下用户。B、C公众号都存在这种情况。
 * 2、扫描公众号二维码关注：同1。
 * 3、扫描公众号下带场景值的二维码关注：这种方式下只处理C公众号的二维码扫描事件。
 *
 * @author Suffrajet
 */
@Component
public class MsgSubscribeHandler extends AbstractHandler {

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IBUserService IBUserService;
    @Autowired
    private ICUserService ICUserService;
    @Autowired
    private IRelationService iRelationService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());

        // 公众号原始ID
        String wxId = wxMessage.getToUser();
        // 获取微信用户基本信息
        WxMpUser userWxInfo = weixinService.getUserService()
                .userInfo(wxMessage.getFromUser(), null);

        // 创建或激活用户，同时如果是扫码带场景值的二维码，则进行额外的关系绑定处理
        WxMpXmlOutMessage reply = this.handleUserSubscribe(wxId, userWxInfo, wxMessage, weixinService);
        if (reply != null) {
            return reply;
        }

        // TODO 通用关注回复消息
        try {
            return new ReplyTextBuilder().build("感谢关注", wxMessage, weixinService);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 新关注用户当数据库不存在时创建，存在时则激活
     *
     * @param wxId 公众号原始ID
     * @param userWxInfo 用户基本信息
     * @return WxMpXmlOutMessage | null
     */
    private WxMpXmlOutMessage handleUserSubscribe(String wxId, WxMpUser userWxInfo, WxMpXmlMessage wxMessage, WxMpService weixinService) {
        WxMpXmlOutMessage reply = null;

        if (userWxInfo != null) {
            String openid = userWxInfo.getOpenId();
            try {
                WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
                if (wxApp != null) {
                    // B类型公众号的用户
                    if(WxAppCategoryEnum.TYPE_B.getCode().equals(wxApp.getCategory())) {
                        BUser bUser = IBUserService.selectByOpenid(wxApp.getId(), openid);
                        if(bUser == null) {
                            bUser = new BUser();
                            bUser.setOpenid(openid);
                            bUser.setUnionid(userWxInfo.getUnionId());
                            bUser.setIdWxApp(wxApp.getId());
                            bUser.setStatus(RegisterStatusEnum.DEFAULT.getCode());
                            this.fillBUser(bUser, userWxInfo);
                            if(IBUserService.insert(bUser)) {
                                logger.info("Create B user: {} success", userWxInfo.getNickname());
                            }
                        } else {
                            this.fillBUser(bUser, userWxInfo);
                            bUser.setIsDeleted(0);
                            if(IBUserService.updateById(bUser)) {
                                logger.info("Active B user: {}", userWxInfo.getNickname());
                            }
                        }
                    }
                    // C类型公众号的用户
                    if(WxAppCategoryEnum.TYPE_C.getCode().equals(wxApp.getCategory())) {
                        CUser cUser = ICUserService.selectByOpenid(wxApp.getId(), openid);
                        if(cUser == null) {
                            cUser = new CUser();
                            cUser.setOpenid(openid);
                            cUser.setUnionid(userWxInfo.getUnionId());
                            cUser.setIdWxApp(wxApp.getId());
                            this.fillCUser(cUser, userWxInfo);
                            if(ICUserService.insert(cUser)) {
                                logger.info("Create C user: {} success", userWxInfo.getNickname());
                            }
                        } else {
                            this.fillCUser(cUser, userWxInfo);
                            cUser.setIsDeleted(0);
                            if(ICUserService.updateById(cUser)) {
                                logger.info("Active C user: {}", userWxInfo.getNickname());
                            }
                        }
                        reply = this.bindC2B(wxApp, cUser, wxMessage, weixinService);
                    }
                }
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return reply;
    }

    /**
     * 处理用户未关注时，扫描带参数二维码事件
     * 已关注用户扫描带参数二维码事件将进入ScanHandler中。
     * 注意：
     * 1、只处理C公众号的二维码
     * 2、这里的EventKey是带前缀的"qrscene_"
     */
    private WxMpXmlOutMessage bindC2B(WxApp wxApp, CUser fans, WxMpXmlMessage wxMessage, WxMpService weixinService) throws Exception {
        WxMpXmlOutMessage reply = null;

        String eventKey = wxMessage.getEventKey();
        if(!eventKey.isEmpty()) {
            String sceneStr = eventKey.substring(8, eventKey.length());
            try {
                if (iRelationService.followBUser(fans, wxApp.getWxId(), sceneStr)) {
                    // TODO 关注B用户的回复消息
                    reply = new ReplyTextBuilder().build("感谢扫码关注", wxMessage, weixinService);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return reply;
    }

    private void fillBUser(BUser bUser, WxMpUser userWxInfo) {
        bUser.setNickname(userWxInfo.getNickname());
        bUser.setSex(userWxInfo.getSex());
        bUser.setCountry(userWxInfo.getCountry());
        bUser.setProvince(userWxInfo.getProvince());
        bUser.setCity(userWxInfo.getCity());
        bUser.setAvatar(userWxInfo.getHeadImgUrl());
    }

    private void fillCUser(CUser cUser, WxMpUser userWxInfo) {
        cUser.setNickname(userWxInfo.getNickname());
        cUser.setSex(userWxInfo.getSex());
        cUser.setCountry(userWxInfo.getCountry());
        cUser.setProvince(userWxInfo.getProvince());
        cUser.setCity(userWxInfo.getCity());
        cUser.setAvatar(userWxInfo.getHeadImgUrl());
    }
}
