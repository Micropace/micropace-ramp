package com.micropace.ramp.core.service;

/**
 * B用户注册相关接口
 *
 * @author Suffrajet
 */
public interface IBUserRegistService {

    /**
     * 注册会话过期时间 5分钟
     */
    int SESSION_EXPIRE = 5 * 60;
    /**
     * 用户重复申请注册
     */
    String REPLY_REGISTER_APPLY_REPEAT = "您已注册过，如果需要更换注册手机号，请输入您的手机号，点击“发送”";
    /**
     * 用户已开启注册会话
     */
    String REPLY_REGISTER_SESSION_STARTED = "您已发送过注册命令，请输入您的手机号，点击“发送”";
    /**
     * 用户申请注册成功
     */
    String REPLY_REGISTER_APPLY_SUCCESS = "请输入您的手机号，点击“发送”";
    /**
     * 手机号格式验证失败
     */
    String REPLY_REGISTER_MOBILE_ILLIGAL = "手机号格式错误，请重新输入正确的手机号，点击“发送”";
    /**
     * 手机号验证成功
     */
    String REPLY_REGISTER_MOBILE_RECIEVED = "验证码已短信发送至目标手机，请您查看并输入验证码，点击“发送”";
    /**
     * 验证码错误
     */
    String REPLY_REGISTER_VALIDATE_FAILED = "您输入的验证码不正确，请重新输入您手机收到的短信验证码，点击“发送”";
    /**
     * 验证码正确，注册成功。
     */
    String REPLY_REGISTER_SUCCESS_FINISH = "注册已完成，审核通过后将为您分发二维码";

    /**
     * 检查注册会话是否已开启
     *
     * @param wxId   B公众号原始ID
     * @param openid B用户openid
     * @return true 已开启
     */
    boolean isSessionActive(String wxId, String openid);

    /**
     * 申请注册，开启注册会话，注册会话默认5分钟有效期
     *
     * @param wxId   B公众号原始ID
     * @param openid B用户openid
     * @return 错误信息
     */
    String applyRegist(String wxId, String openid);

    /**
     * 提交注册的手机号
     * 如果注册会话未开启，或者会话已超时则不进行终止，回复错误提示信息
     *
     * @param wxId   B公众号原始ID
     * @param openid B用户openid
     * @param mobile B用户申请注册的手机号
     * @return 错误信息
     */
    String submitMobile(String wxId, String openid, String mobile);

    /**
     * 验证手机验证码
     * 如果注册会话未开启，或者会话已超时则不进行终止，回复错误提示信息
     * 验证成功后为B用户绑定二维码
     * 不管验证是否成功，都将结束注册会话
     *
     * @param wxId         B公众号原始ID
     * @param openid       B用户openid
     * @param validateCode 发送给B用户申请注册手机号的验证码
     * @return 错误信息
     */
    String validate(String wxId, String openid, String validateCode);
}
