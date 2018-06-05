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
@TableName("b_user_bind_qrcode")
public class BUserBindQrcode extends MyBatisSuperEntity<BUserBindQrcode> {
    @TableField("id_buser")
    private Long idBuser;
    @TableField("id_qrcode")
    private Long idQrcode;
}
