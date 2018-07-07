package com.micropace.ramp.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropace.ramp.base.entity.thirdpart.QueryLoveselfQrcodeResp;
import com.micropace.ramp.core.service.IThirdPartApiCallService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IThirdPartApiCallServiceImpl implements IThirdPartApiCallService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient httpclient = HttpClients.createDefault();

    @Override
    public String getLoveselfQrcodePic(String scene) {

        String qrcodeUrl = null;

        String getQrcodeUrl = LOVESELF_HOST + "api/qrcode?scene=" + scene;
        HttpGet httpget = new HttpGet(getQrcodeUrl);
        try {
            String data = this.executeHttpRequest(httpget);
            QueryLoveselfQrcodeResp resp = mapper.readValue(data, QueryLoveselfQrcodeResp.class);
            if (resp != null && resp.getData() != null) {
                qrcodeUrl = LOVESELF_HOST + resp.getData().getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qrcodeUrl;
    }

    @Override
    public String createLoveselfQrcode(String scene) {
        String qrcodeUrl = null;

        String postQrcodeUrl = LOVESELF_HOST + "api/qrcode";

        // 构造POST的参数
        Map<String, String> param = new HashMap<>();
        param.put("scene", scene);

        try {
            HttpPost httppost = new HttpPost(postQrcodeUrl);
            httppost.addHeader("Content-Type", "application/json");
            httppost.setEntity(this.formatPostParamEntity(param));

            String data = this.executeHttpRequest(httppost);
            QueryLoveselfQrcodeResp resp = mapper.readValue(data, QueryLoveselfQrcodeResp.class);
            if(resp != null && resp.getData() != null) {
                qrcodeUrl = LOVESELF_HOST + resp.getData().getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qrcodeUrl;
    }

    private HttpEntity formatPostParamEntity(Map<String, String> param) {
        HttpEntity httpEntity = null;
        try {
            String json = mapper.writeValueAsString(param);
            httpEntity = new StringEntity(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpEntity;
    }

    private String executeHttpRequest(HttpUriRequest request) {
        String data = null;
        try {
            logger.info("execute {} http request [{}]", request.getMethod(), request.getURI());
            CloseableHttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                data = EntityUtils.toString(entity, "UTF-8");
                logger.info("response data of {} is {}", request.getURI(), data);
            }
            response.close();
        } catch (IOException e) {
            logger.error("execute http request error, message: {}", e.getMessage(), e.getCause());
        }
        return data;
    }
}