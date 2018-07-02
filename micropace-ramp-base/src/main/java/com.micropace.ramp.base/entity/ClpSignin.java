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
@TableName("clp_signin")
public class ClpSignin extends MyBatisSuperEntity<ClpSignin> {
    @TableField("name")
    private String name;
    @TableField("mobile")
    private String mobile;
    @TableField("company")
    private String company;
    @TableField("duty")
    private String duty;
    @TableField("sign_in_time")
    private String signInTime;
}
