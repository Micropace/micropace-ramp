package com.micropace.ramp.base.common;

import com.micropace.ramp.base.constant.RedisKeyConst;

/**
 * Redis键名获取静态方法类
 *
 * @author Suffrajet
 */
public class RedisKeyBuilder {

    /**
     * <pre>
     * 获取微信公众号会话键名
     *
     * @param wxId   公众号原始ID
     * @param openid 公众号用户openid
     * @param suffix 会话类型后缀
     * @return 键名
     * </pre>
     */
    public static String getSesstionKey(String wxId, String openid, String suffix) {
        return String.format(RedisKeyConst.WECHAT_SESSION_FORMATER,
                wxId, openid, suffix);
    }
}
