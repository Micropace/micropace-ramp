package com.micropace.ramp.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信公众号类型
 *
 * @author Suffrajet
 */
@Getter
@AllArgsConstructor
public enum WxTypeEnum {
    APP_SUBSCRIBE(0, "订阅号"),
    APP_SERVICE(1, "服务号"),
    APP_ENTERPRISE(2, "企业号"),
    APP_MINI(3, "小程序");

    public static WxTypeEnum getByCode(Integer code) {
        for(WxTypeEnum e : values()) {
            if(e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    private Integer code;
    private String desc;
}
