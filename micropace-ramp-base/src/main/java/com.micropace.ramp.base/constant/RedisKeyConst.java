package com.micropace.ramp.base.constant;

/**
 * Redis键名规则定义
 *
 * @author Suffrajet
 */
public class RedisKeyConst {

    /**
     * <pre>
     * 微信公众号自定义会话机制的键名规则
     * 会话键名参数依次为：公众号原始ID，用户openid、会话类型后缀
     *
     * </pre>
     */
    public static final String WECHAT_SESSION_FORMATER = "app:%s:session:%s:%s";
}
