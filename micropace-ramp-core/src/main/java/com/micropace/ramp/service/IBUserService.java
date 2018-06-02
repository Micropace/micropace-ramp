package com.micropace.ramp.service;

import com.baomidou.mybatisplus.service.IService;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.enums.RegisterStatusEnum;

import java.util.List;

public interface IBUserService extends IService<BUser> {

    /**
     * 根据openid查找Lord记录
     *
     * @param idWxApp 公众号记录ID
     * @param openid  Lord用户openid
     * @return Lord记录
     */
    BUser selectByOpenid(Long idWxApp, String openid);

    /**
     * 根据分配的二维码ID查找B用户记录
     *
     * @param idQrcode 二维码记录ID
     * @return B用户记录
     */
    BUser selectByBindedQrcodeId(Long idQrcode);

    /**
     * 根据注册的手机号查询B用户记录
     *
     * @param mobile 手机号
     * @return B用户记录
     */
    BUser selectByMobile(String mobile);


    /**
     * 根据真实姓名查询B用户记录
     *
     * @param name 真实姓名
     * @return B用户记录
     */
    BUser selectByName(String name);

    /**
     * 激活用户
     *
     * @param idWxApp 公众号记录ID
     * @param openid  B用户openid
     * @return true 激活成功
     */
    boolean active(Long idWxApp, String openid);

    /**
     * 逻辑删除用户
     *
     * @param idWxApp 公众号记录ID
     * @param openid  B用户openid
     */
    void deleteByOpenid(Long idWxApp, String openid);

    /**
     * 查询B用户是否已注册过
     *
     * @param idWxApp B公众号记录ID
     * @param openid  B用户openid
     * @return true 已注册过
     */
    boolean isRegisted(Long idWxApp, String openid);

    /**
     * 查询B用户注册是否等待审核
     * @param idWxApp B公众号记录ID
     * @param openid B用户openid
     * @return true 待审核
     */
    boolean isRegistProcessing(Long idWxApp, String openid);

    /**
     * 查询注册申请不同审核状态的B用户列表
     *
     * @param idWxApp 公众号记录ID
     * @param status  审核状态
     * @return B用户列表
     */
    List<BUser> selectSpecifiedStatusList(Long idWxApp, RegisterStatusEnum status);
}
