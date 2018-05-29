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
@TableName("wx_app")
public class WxApp extends MyBatisSuperEntity<WxApp> {
    @TableField("wx_id")
    private String wxId;
    @TableField("app_id")
    private String appId;
    @TableField("secret")
    private String secret;
    @TableField("token")
    private String token;
    @TableField("aes_key")
    private String aesKey;
    /** 公众号类型{WxAppType} */
    @TableField("type")
    private Integer type;
    @TableField("industry")
    private String industry;
    @TableField("description")
    private String description;
}
