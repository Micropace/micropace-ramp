package com.micropace.ramp.core.service;

/**
 * 外部RESTFul接口调用
 *
 * @author Suffrajet
 */
public interface IThirdPartApiCallService {

    String LOVESELF_HOST = "https://micropace.top/";

    /**
     * 获取爱自己小程序的题目二维码
     *
     * @param scene 场景值
     * @return 二维码的链接 host/name.jpg 为图片链接
     */
    String getLoveselfQrcodePic(String scene);

    /**
     * 创建爱自己小程序的二维码
     *
     * @param scene 场景值
     * @return 二维码的链接 host/name.jpg 为图片链接
     */
    String createLoveselfQrcode(String scene);
}