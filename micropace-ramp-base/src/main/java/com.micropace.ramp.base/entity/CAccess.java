package com.micropace.ramp.base.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@TableName("c_access")
public class CAccess {
    @TableField("id_cuser")
    private Long idCuser;
    @TableField("access_token")
    private String accessToken;
    @TableField("refresh_token")
    private String refreshToken;
    @TableField("token_refresh_at")
    private String tokenRefreshAt;
    @TableField("expire")
    private Integer expire;
}
