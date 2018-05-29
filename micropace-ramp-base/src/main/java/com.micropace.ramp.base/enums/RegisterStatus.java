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
public enum RegisterStatus {
    /** 未注册 */
    DEFAULT(0, "未注册"),
    /** 已申请，等待审核中，不允许重新申请注册 */
    PROCESSING(1, "等待审核中"),
    /** 审核已通过，不允许重新申请注册 */
    SUCCESSED(2, "审核已通过"),
    /** 审核未通过，允许重新申请注册 */
    FAILED(3, "审核未通过");

    /** 编号 */
    private Integer code;
    /** 描述 */
    private String desc;

    public static String getDescByCode(Integer code) {
        for(RegisterStatus item : values()) {
            if(item.getCode().equals(code)) {
                return item.getDesc();
            }
        }
        return null;
    }
}
