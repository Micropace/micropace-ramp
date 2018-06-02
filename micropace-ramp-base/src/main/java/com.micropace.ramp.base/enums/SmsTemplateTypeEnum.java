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
public enum SmsTemplateTypeEnum {
    SMS_TEMPLATE_LOVESELF(0, "SMS_134520038", "爱自己");

    private int index;
    private String code;
    private String desc;
}
