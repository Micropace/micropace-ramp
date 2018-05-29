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
@TableName("qrcode")
public class Qrcode extends MyBatisSuperEntity<Qrcode> {
    @TableField("id_wx_app")
    private Long idWxApp;
    @TableField("scene_str")
    private String sceneStr;
    @TableField("type")
    private Integer type;
    @TableField("ticket")
    private String ticket;
    @TableField("wxurl")
    private String wxurl;
    /** 本地全路径 */
    @TableField("local_path")
    private String localPath;
    @TableField("filename")
    private String filename;
    @TableField("is_bind")
    private Integer isBind;
    @TableField("expire")
    private Integer expire;
}
