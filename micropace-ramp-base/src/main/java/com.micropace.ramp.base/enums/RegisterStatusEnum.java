package com.micropace.ramp.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * B用户申请注册流程状态定义
 *
 * @author Suffrajet
 */
@Getter
@AllArgsConstructor
public enum RegisterStatusEnum {
    DEFAULT(0, "未注册"),
    /** 不允许重新申请注册 */
    PROCESSING(1, "已申请，等待审核中"),
    /** 不允许重新申请注册 */
    SUCCESSED(2, "审核已通过"),
    /** 允许重新申请注册 */
    FAILED(3, "审核未通过");

    public static String getDescByCode(Integer code) {
        for(RegisterStatusEnum item : values()) {
            if(item.getCode().equals(code)) {
                return item.getDesc();
            }
        }
        return null;
    }

    private Integer code;
    private String desc;
}
