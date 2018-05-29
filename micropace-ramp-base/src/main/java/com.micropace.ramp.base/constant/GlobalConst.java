package com.micropace.ramp.base.constant;

/**
 * 全局静态常量定义
 *
 * @author Suffrajet
 */
public class GlobalConst {

    /** redis命令执行成功的返回值 */
    public static final String REDIS_OK = "OK";
    /** redis中PING命令的返回值 */
    public static final String REDIS_PING = "PONG";

    /** 微信公众号托管连接规则 */
    public static final String WECHAT_SERVER_TRUST_URL = "http://%s/wechat/portal?wxId=%s";

}
