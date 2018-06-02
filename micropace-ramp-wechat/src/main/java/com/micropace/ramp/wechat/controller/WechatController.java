package com.micropace.ramp.wechat.controller;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxTypeEnum;
import com.micropace.ramp.core.GlobalParamManager;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 微信公众号认证接口
 *
 * @author Suffrajet
 */
@RestController
@RequestMapping("/api/service")
public class WechatController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GlobalParamManager globalParamManager;

    /** 自定义配置域名，www.name.com */
    @Value("${server.domain}")
    private String serverDomain;

    /**
     * 微信服务器认证, url中需自带wxId字段，该字段是公众号的原始ID
     *
     * @param wxId      公众号的原始ID，作为公众号的标识
     * @param signature 来自微信服务器 签名字段
     * @param timestamp 来自微信服务器 时间戳
     * @param nonce     来自微信服务器 随机数
     * @param echostr   来自微信服务器 随机字符串
     * @return echostr
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authentication(@RequestParam("wxId") String wxId,
                                 @RequestParam(name = "signature", required = false) String signature,
                                 @RequestParam(name = "timestamp", required = false) String timestamp,
                                 @RequestParam(name = "nonce", required = false) String nonce,
                                 @RequestParam(name = "echostr", required = false) String echostr) {

        this.logger.info("Recieve authentication message from wechat：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("illegal params");
        }

        WxApp wxApp = globalParamManager.getWxApp(wxId);
        if (wxApp != null) {
            // 服务号和订阅号的验证处理
            if (WxTypeEnum.APP_SUBSCRIBE.getCode().equals(wxApp.getWxType())
                    || WxTypeEnum.APP_SERVICE.getCode().equals(wxApp.getWxType())) {
                WxMpService wxMpService = globalParamManager.getMpService(wxId);
                if (wxMpService != null) {
                    if (wxMpService.checkSignature(timestamp, nonce, signature)) {
                        return echostr;
                    }
                }
            }
            // 企业号的验证处理
            if (WxTypeEnum.APP_ENTERPRISE.getCode().equals(wxApp.getWxType())) {
                WxCpService wxCpService = globalParamManager.getCpService(wxId);
                if (wxCpService != null) {
                    if (wxCpService.checkSignature(timestamp, nonce, signature, echostr)) {
                        return echostr;
                    }
                }
            }
            // TODO 小程序的验证处理
        }
        return null;
    }

    /**
     * 分发处理来自微信服务器的消息
     *
     * @param requestBody  XML格式消息内容
     * @param wxId         公众号的原始ID，作为公众号的标识
     * @param signature    来自微信服务器 签名字段
     * @param timestamp    来自微信服务器 时间戳
     * @param nonce        来自微信服务器 随机数
     * @param encType      消息加密类型
     * @param msgSignature 消息签名
     * @return XML格式消息回复
     */
    @PostMapping(produces = "application/xml;charset=utf-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("wxId") String wxId,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {

        this.logger.info("Recieve message from wechat：\n[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        WxApp wxApp = globalParamManager.getWxApp(wxId);
        if (wxApp != null) {
            // 服务号和订阅号的消息路由
            if (WxTypeEnum.APP_SUBSCRIBE.getCode().equals(wxApp.getWxType())
                    || WxTypeEnum.APP_SERVICE.getCode().equals(wxApp.getWxType())) {
                return this.routerMpMessage(wxApp, requestBody, signature, timestamp, nonce, encType, msgSignature);
            }
            // 企业号的消息路由
            if (WxTypeEnum.APP_ENTERPRISE.getCode().equals(wxApp.getWxType())) {
                return this.routerCpMessage(wxApp, requestBody, signature, timestamp, nonce, encType, msgSignature);
            }
        }
        return null;
    }

    /**
     *
     * 前端首页入口，跳转到网页授权URL
     *
     * <p>
     *     url: http://host/api/service/oauth/login
     *     param: wxId=xxxxx
     * </p>
     * <p>
     *     callback: http:host/api/service/oauth/callback
     *     param: code=xxxx, state=xxxxx
     * </p>
     *
     * 其中，wxId必须配置，是作为识别公众号的标识
     * 为了在回调接口中识别公众号，将state设置为wxId的值
     *
     * @param wxId 公众号原始ID
     * @param response void 将会发起Oauth2请求。
     */
    @GetMapping("/oauth/login")
    public void index(@RequestParam("wxId") String wxId,
                      HttpServletResponse response) {
        WxApp wxApp = globalParamManager.getWxApp(wxId);
        if (wxApp != null) {
            WxMpService wxMpService = globalParamManager.getMpService(wxId);
            if (wxMpService != null) {
                String redirectUri = String.format("http://%s/api/service/oauth/callback", serverDomain);
                String url = wxMpService.oauth2buildAuthorizationUrl(redirectUri, WxConsts.OAuth2Scope.SNSAPI_USERINFO, wxId);
                try {
                    response.sendRedirect(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * 网页授权回调接口
     *
     *  @param code Oauth接口返回的code值
     * @param state 自定义state参数，约定state为公众号的原始ID，以识别公众号
     * @return 授权后的用户信息
     */
    @GetMapping("/oauth/callback")
    public ResponseMsg oauthCallback(@RequestParam("code") String code,
                                     @RequestParam("state") String state) {
        WxMpService wxMpService = globalParamManager.getMpService(state);
        if (wxMpService != null) {
            try {
                WxMpOAuth2AccessToken accessToken = wxMpService.oauth2getAccessToken(code);
                WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(accessToken, null);
                return success(wxMpUser);

            } catch (WxErrorException e) {
                e.printStackTrace();
            }

            // TODO token的缓存处理，后续怎么跳转页面等。
            // TODO 这里可以进行跳转，携带用户信息参数

            return success();
        }
        return error("");
    }

    private WxMpXmlOutMessage route(String wxId, WxMpXmlMessage message) {
        try {
            WxMpMessageRouter router = globalParamManager.getMpRouter(wxId);
            if (router != null) {
                return router.route(message);
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

    private String routerMpMessage(WxApp wxApp, String requestBody, String signature, String timestamp, String nonce, String encType, String msgSignature) {
        WxMpService wxMpService = globalParamManager.getMpService(wxApp.getWxId());
        if (wxMpService != null) {
            if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
                logger.error("illegal params");
                return null;
            }

            String reply = null;
            if (encType == null) {
                // 明文传输的消息
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
                WxMpXmlOutMessage outMessage = this.route(wxApp.getWxId(), inMessage);
                if (outMessage == null) {
                    return "";
                }
                reply = outMessage.toXml();
            } else if ("aes".equals(encType)) {
                // aes加密的消息
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
                        requestBody,
                        wxMpService.getWxMpConfigStorage(),
                        timestamp,
                        nonce,
                        msgSignature);
                WxMpXmlOutMessage outMessage = this.route(wxApp.getWxId(), inMessage);
                if (outMessage == null) {
                    return "";
                }
                reply = outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
            }

            this.logger.debug("Reply message content：\n{}", reply);
            return reply;
        }
        return null;
    }

    private String routerCpMessage(WxApp wxApp, String requestBody, String signature, String timestamp, String nonce, String encType, String msgSignature) {
        // TODO
        return null;
    }
}
