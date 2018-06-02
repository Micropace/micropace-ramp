package com.micropace.ramp.service;

import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.entity.CUser;

import java.util.List;

/**
 * B、C用户关系管理接口
 *
 * @author Suffrajet
 */
public interface IRelationService {

    /**
     * C用户关注指定的B用户
     * 关注只有一种方式，就是：C用户扫描了B用户绑定的带有场景值的二维码
     * 这个二维码是C公众号下的，场景值关联了B用户，扫描后用户就进入到了C公众号下。
     * 如果两者关系记录已存在，则忽略
     *
     * @param cUser      Fans
     * @param qrcodeWxId 二维码的公众号原始ID
     * @param sceneStr   Lord绑定的二维码场景值
     * @return true 成功
     */
    boolean followBUser(CUser cUser, String qrcodeWxId, String sceneStr);

    /**
     * 查询B、C两者是否存在关联关系
     *
     * @param idBuser B用户记录iD
     * @param idCuser C用户记录ID
     * @return true 存在
     */
    boolean isFollowed(Long idBuser, Long idCuser);

    /**
     * 查询指定C用户关注的B用户列表
     *
     * @param idCuser C用户ID
     * @return B用户列表
     */
    List<BUser> selectFollowedBusers(Long idCuser);

    /**
     * 查询B用户的关注者C用户列表
     *
     * @param idBuser B用户ID
     * @return 关注着列表
     */
    List<CUser> selectFollowers(Long idBuser);
}
