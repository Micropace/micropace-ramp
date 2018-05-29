package com.micropace.ramp.wechat.service.impl;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropace.ramp.base.enums.SmsTemplateType;
import com.micropace.ramp.wechat.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ISmsServiceImpl implements ISmsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IAcsClient acsClient;

    @Override
    public SendSmsResponse send(String target, String signName, String templateCode, Map<String, String> templateParam) {
        SendSmsResponse response = null;

        //组装短信发送请求对象
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(target);
        //必填:短信签名
        request.setSignName(signName);
        //必填:短信模板
        request.setTemplateCode(templateCode);
        try {
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            String param = mapper.writeValueAsString(templateParam);
            logger.info("Send sms message to: {}, param: {}", target, param);
            request.setTemplateParam(param);
            response = acsClient.getAcsResponse(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public SendSmsResponse sendValidateCode(String target, String validateCode) {
        Map<String, String> param = new HashMap<>();
        param.put("code", validateCode);
        return this.send(
                target,
                SmsTemplateType.SMS_TEMPLATE_LOVESELF.getDesc(),
                SmsTemplateType.SMS_TEMPLATE_LOVESELF.getCode(),
                param);
    }

    @Override
    public QuerySendDetailsResponse query(String target, String day, Long pageSize, Long currentPage) {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(target);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        request.setSendDate(day);
        //必填-页大小
        request.setPageSize(pageSize);
        //必填-当前页码从1开始计数
        request.setCurrentPage(currentPage);

        //hint 此处可能会抛出异常，注意catch

        try {
            return acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
