package com.micropace.ramp.base.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.micropace.ramp.base.common.MyBatisSuperEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@TableName("b_user")
public class BUser extends MyBatisSuperEntity<BUser> {
    @TableField("openid")
    private String openid;
    @TableField("unionid")
    private String unionid;
    @TableField("id_wx_app")
    private Long idWxApp;
    @TableField("id_qrcode")
    private Long idQrcode;
    @TableField("bind_at")
    private String bindAt;
    /** 注册状态,0:未注册, 1:审核中, 2:审核通过 */
    @TableField("status")
    private Integer status;
    @TableField("nickname")
    private String nickname;
    /** 用户性别, 0: 未设置，1: 男，2: 女 */
    @TableField("sex")
    private Integer sex;
    @TableField("country")
    private String country;
    @TableField("province")
    private String province;
    @TableField("city")
    private String city;
    @TableField("avatar")
    private String avatar;
    @TableField("mobile")
    private String mobile;
    @TableField("name")
    private String name;
    @TableField("brand")
    private String brand;
    @TableField("region")
    private String region;
    @TableField("address")
    private String address;
    @TableField("description")
    private String description;
    @TableField("is_deleted")
    private Integer isDeleted;
}
