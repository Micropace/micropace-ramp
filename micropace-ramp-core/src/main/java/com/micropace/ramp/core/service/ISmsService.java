package com.micropace.ramp.core.service;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;

import java.util.Map;

/**
 * 短信相关接口
 *
 * @author Suffrajet
 */
public interface ISmsService {

    /**
     * 向指定目标手机号发送短信
     *
     * @param target        目标手机号
     * @param signName      短信签名
     * @param templateCode  短信模板
     * @param templateParam 模板中的变量值
     * @return 短信发送结果
     */
    SendSmsResponse
    send(String target, String signName, String templateCode, Map<String, String> templateParam);

    /**
     * 向指定目标手机号发送验证码短信
     *
     * @param target       目标手机号
     * @param validateCode 验证码
     * @return 短信发送结果
     */
    SendSmsResponse
    sendValidateCode(String target, String validateCode);

    /**
     * 查询指定目标手机号的发送历史记录
     *
     * @param target      目标手机号
     * @param day         天 yyyyMMdd
     * @param pageSize    页大小
     * @param currentPage 当前页码
     * @return 历史记录列表
     */
    QuerySendDetailsResponse
    query(String target, String day, Long pageSize, Long currentPage);

}
