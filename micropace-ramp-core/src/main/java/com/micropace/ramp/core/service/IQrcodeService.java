package com.micropace.ramp.core.service;

import com.baomidou.mybatisplus.service.IService;
import com.micropace.ramp.base.entity.Qrcode;
import com.micropace.ramp.base.entity.WxApp;

import java.util.List;
import java.util.Map;

/**
 * 二维码管理接口
 * 二维码创建：带场景值的二维码是C公众号的，用户扫描带场景值的二维码后将关注C公众号、并与对应的B用户产生关联。
 *
 * @author Suffrajet
 */
public interface IQrcodeService extends IService<Qrcode> {

    /**
     * 根据场景值查询二维码信息
     *
     * @param wxApp    公众号
     * @param sceneStr 场景值 1-64位字符串
     * @return 二维码信息
     */
    Qrcode selectBySceneStr(WxApp wxApp, String sceneStr);

    /**
     * 创建二维码
     *
     * @param wxApp       公众号
     * @param sceneStr    场景值
     * @param isPermanent 是否永久
     * @return 结果信息
     */
    Map<String, String> create(WxApp wxApp, String sceneStr, boolean isPermanent);

    /**
     * 查询已绑定过的二维码信息列表
     *
     * @param wxApp       公众号
     * @param isPermanent 是否永久
     * @return 列表
     */
    List<Qrcode> selectBindedQrcodes(WxApp wxApp, boolean isPermanent);

    /**
     * 查询空闲的二维码信息列表
     *
     * @param wxApp       公众号
     * @param isPermanent 是否永久
     * @return 列表
     */
    List<Qrcode> selectIdleQrcodes(WxApp wxApp, boolean isPermanent);

    /**
     * 从指定公众号下获取一个未被绑定的二维码
     *
     * @param idWxApp 公众号记录ID
     * @param type    二维码类型
     * @return 二维码记录
     */
    Qrcode selectIdleOneByType(Long idWxApp, Integer type);
}
