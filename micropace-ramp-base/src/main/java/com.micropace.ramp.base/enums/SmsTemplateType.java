package com.micropace.ramp.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信模版类型
 *
 * @author Suffrajet
 */
@Getter
@AllArgsConstructor
public enum SmsTemplateType {
    SMS_TEMPLATE_LOVESELF(0, "SMS_134520038", "爱自己");

    /** 序号 */
    private int index;
    /** 编码 */
    private String code;
    /** 描述 */
    private String desc;
}
