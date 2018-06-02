package com.micropace.ramp.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二维码类型
 *
 * @author Suffrajet
 */
@Getter
@AllArgsConstructor
public enum QrCodeTypeEnum {
    PERMANENT(0, "永久二维码"),
    PROVISIONAL(1, "临时二维码");

    private Integer code;
    private String desc;
}
