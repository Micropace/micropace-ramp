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
    /** 公众号类型：0:订阅号，1: 企业号，2: 服务号, 3: 小程序 */
    @TableField("wx_type")
    private Integer wxType;
    /** 公众号所属分类：0:B类型公众号，1:C类型公众号 */
    @TableField("category")
    private Integer category;
    @TableField("industry")
    private String industry;
    @TableField("description")
    private String description;
}
