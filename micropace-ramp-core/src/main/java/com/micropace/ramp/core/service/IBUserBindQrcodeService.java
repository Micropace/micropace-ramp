package com.micropace.ramp.core.service;

import com.baomidou.mybatisplus.service.IService;
import com.micropace.ramp.base.entity.BUserBindQrcode;
import com.micropace.ramp.base.entity.Qrcode;

import java.util.List;

/**
 * B用户二维码绑定信息接口
 *
 * @author Suffrajet
 */
public interface IBUserBindQrcodeService extends IService<BUserBindQrcode> {

    /**
     * 根据B用户ID查询绑定的二维码列表
     *
     * @param idBuser B用户ID
     * @return 二维码列表
     */
    List<Qrcode> selectQrcodesByBuserId(Long idBuser);
}
