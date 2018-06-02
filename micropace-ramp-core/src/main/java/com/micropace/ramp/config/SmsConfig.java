package com.micropace.ramp.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 短信相关配置
 *
 * @author Suffrajet
 */
@Component
public class SmsConfig {

    //产品名称:云通信短信API产品
    private static final String product = "Dysmsapi";
    //产品域名
    private static final String domain  = "dysmsapi.aliyuncs.com";
    //区域
    private static final String region  = "cn-hangzhou";

    @Value("${aliyun-sms.access-key}")
    private String accessKeyId;
    @Value("${aliyun-sms.access-secret}")
    private String accessKeySecret;

    @Bean
    public IAcsClient getSmsClient() {
        try {
            IClientProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint(region, region, product, domain);
            return new DefaultAcsClient(profile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
