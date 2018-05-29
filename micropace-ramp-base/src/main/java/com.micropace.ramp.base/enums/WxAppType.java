package com.micropace.ramp.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信公众号分类
 *
 * @author Suffrajet
 */
@Getter
@AllArgsConstructor
public enum WxAppType {
    /** B类型公众号 */
    TYPE_B(0, "B类型公众号"),
    /** C类型公众号 */
    TYPE_C(1, "C类型公众号"),
    /** C类型小程序 */
    TYPE_MINIAPP(2, "C类型小程序");

    /** 编号 */
    private Integer code;
    /** 描述 */
    private String desc;
}
