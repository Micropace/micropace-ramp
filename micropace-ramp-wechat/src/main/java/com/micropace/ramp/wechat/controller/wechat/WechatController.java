package com.micropace.ramp.wechat.controller.wechat;

import com.micropace.ramp.wechat.core.initializer.GlobalParamManager;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 微信公众号认证接口
 *
 * @author Suffrajet
 */
@RestController
@RequestMapping("/api/service")
public class WechatController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        WxMpService wxMpService = GlobalParamManager.getInstance().getService(wxId);
        if (wxMpService != null) {
            if (wxMpService.checkSignature(timestamp, nonce, signature)) {
                return echostr;
            }
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

        WxMpService wxMpService = GlobalParamManager.getInstance().getService(wxId);
        if (wxMpService != null) {
            if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
                logger.error("illegal params");
                return null;
            }

            String reply = null;
            if (encType == null) {
                // 明文传输的消息
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
                WxMpXmlOutMessage outMessage = this.route(wxId, inMessage);
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
                WxMpXmlOutMessage outMessage = this.route(wxId, inMessage);
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

    private WxMpXmlOutMessage route(String wxId, WxMpXmlMessage message) {
        try {
            WxMpMessageRouter router = GlobalParamManager.getInstance().getRouter(wxId);
            if (router != null) {
                return router.route(message);
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }
}
