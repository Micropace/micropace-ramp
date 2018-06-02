package com.micropace.ramp.base.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * B用户网页授权token存储类
 *
 * @author suffrajet
 */
@Getter
@Setter
@ToString
@TableName("b_access")
public class BAccess {
    @TableField("id_buser")
    private Long idBuser;
    @TableField("access_token")
    private String accessToken;
    @TableField("refresh_token")
    private String refreshToken;
    @TableField("token_refresh_at")
    private String tokenRefreshAt;
    @TableField("expire")
    private Integer expire;
}
