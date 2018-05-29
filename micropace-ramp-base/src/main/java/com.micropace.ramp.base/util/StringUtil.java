package com.micropace.ramp.base.util;

import java.util.Random;
import java.util.UUID;

/**
 * 字符串常用静态方法
 *
 * @author Suffrajet
 */
public class StringUtil {

    /**
     * 获取不带'-'的uuid字符串
     *
     * @return uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取随机的短信验证码
     *
     * @return 6位数字
     */
    public static String getValidateCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }
}
