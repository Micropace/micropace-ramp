package com.micropace.ramp.wechat.config;

import com.baomidou.mybatisplus.mapper.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Mybatis公共字段自动填充注入处理
 *
 * @author Suffrajet
 */
public class MyMetaObjectHandler extends MetaObjectHandler {

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 创建实体对象时，自动生成创建时间、更新时间
     *
     * @param metaObject 实体对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Object createdAt = getFieldValByName("createdAt", metaObject);
        if (createdAt == null) {
            setFieldValByName("createdAt", sf.format(new Date()), metaObject);
            setFieldValByName("updatedAt", sf.format(new Date()), metaObject);
        }
    }

    /**
     * 更新实体对象时，自动生成更新时间
     *
     * @param metaObject 实体对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Object updatedAt = getFieldValByName("updatedAt", metaObject);
        if(updatedAt != null) {
            setFieldValByName("updatedAt", sf.format(new Date()), metaObject);
        }
    }
}
