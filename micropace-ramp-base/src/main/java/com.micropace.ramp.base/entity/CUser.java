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
@TableName("c_user")
public class CUser extends MyBatisSuperEntity<CUser> {
    @TableField("openid")
    private String openid;
    @TableField("unionid")
    private String unionid;
    @TableField("id_wx_app")
    private Long idWxApp;
    @TableField("mobile")
    private String mobile;
    @TableField("nickname")
    private String nickname;
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
    @TableField("is_deleted")
    private Integer isDeleted;
}
