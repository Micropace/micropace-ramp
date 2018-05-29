package com.micropace.ramp.base.common;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * Entity基类
 */

@Getter
@Setter
public class MyBatisSuperEntity<T extends Model> extends Model<T> {
    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String updatedAt;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
