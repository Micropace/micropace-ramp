package com.micropace.ramp.base.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMsg {
    // B用户相关错误码
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_NOT_REGIST(10002, "用户未注册"),
    REPEAT_CHECK_REGIST(10003, "用户注册申请已审核，不可重复审核"),
    NO_AVAILABLE_QRCODE(10004, "没有可用的二维码，请创建新的二维码后重试"),
    CHECK_REGIST_FAILED(10005, "审核用户注册申请失败"),
    ILLEGAL_MOBILE(10006, "手机号验证失败，请确保手机号是真实的"),
    REPEAT_REGIST_MOBILE(10007, "手机号已注册"),
    REGIST_CHECK_IS_PROCESSING(10008, "注册申请正在审核中，请勿重复提交"),
    REGIST_APPLY_SUCCESS(10009, "注册成功，申请已提交审核，请耐心等待!"),
    REGIST_APPLY_FAILED(10010, "注册失败"),
    UPDATE_USER_PROFILE_FAILED(10011, "信息更新失败"),
    REGIST_CHECK_DENY(10012, "注册申请审核未通过"),

    // 公众号相关错误码
    WX_APP_NOT_TRUST(20001, "公众号未托管"),
    WX_APP_REPEAT_TRUST(20002, "公众号已托管"),
    WX_APP_TRUST_FAILED(20003, "公众号托管失败"),
    WX_APP_PARAM_ILLEGAL(20004, "参数错误, 托管公众号失败"),
    WX_APP_INDUSTRY_PARAM_ERROR(20005, "公司名称未填写, 托管公众号失败"),
    WX_APP_NOT_FOUND(20006, "公众号记录不存在"),
    WX_APP_UPDATE_FAILED(20007, "更新公众号信息失败"),
    WX_APP_GET_MENU_FAILED(20008, "获取公众号自定义菜单失败"),
    WX_APP_MENU_FORMAT_ERROR(20009, "自定义菜单格式错误"),
    WX_APP_CREATE_MENU_FAILED(20010, "创建公众号自定义菜单失败"),
    WX_APP_DELETE_MENU_FAILED(20011, "删除公众号自定义菜单失败"),


    // 二维码相关错误码
    QRCODE_NOT_FOUND(30001, "指定场景值的二维码不存在"),
    QRCODE_ONLY_ALLOW_CTYPE(30002, "只允许为C类型公众号或小程序创建二维码"),
    REPEAT_SCENESTR(30003, "该场景值的二维码已存在, 请勿重复生成"),
    ILLEGAL_SCENESTR(30004, "创建二维码失败, 请确保场景值是数字类型字符串"),
    QUERY_QRCODE_FAILED(30005, "获取二维码失败");

    /** 错误码 */
    private int code;
    /** 错误描述 */
    private String desc;
}
