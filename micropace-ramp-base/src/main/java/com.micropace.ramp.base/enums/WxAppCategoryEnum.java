package com.micropace.ramp.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公众号所属种类
 *
 * @author Suffrajet
 */
@Getter
@AllArgsConstructor
public enum WxAppCategoryEnum {
    TYPE_B(0, "B类型公众号"),
    TYPE_C(1, "C类型公众号");

    private Integer code;
    private String desc;
}
