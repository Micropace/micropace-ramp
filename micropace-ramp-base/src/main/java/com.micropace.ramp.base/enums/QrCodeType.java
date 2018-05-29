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
public enum QrCodeType {
    /** 永久的 */
    PERMANENT(0, "永久二维码"),
    /** 临时的 */
    PROVISIONAL(1, "临时二维码");

    /** 序号 */
    private Integer code;
    /** 描述 */
    private String desc;
}
