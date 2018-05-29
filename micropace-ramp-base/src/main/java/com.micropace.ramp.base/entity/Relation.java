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
@TableName("relation")
public class Relation extends MyBatisSuperEntity<Relation> {
    @TableField("id_buser")
    private Long idBuser;
    @TableField("id_cuser")
    private Long idCuser;
}
