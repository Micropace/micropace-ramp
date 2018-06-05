package com.micropace.ramp.core.mapper;

import com.micropace.ramp.base.common.IMyBatisSuperMapper;
import com.micropace.ramp.base.entity.BUserBindQrcode;
import com.micropace.ramp.base.entity.Qrcode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BUserBindQrcodeMapper extends IMyBatisSuperMapper<BUserBindQrcode> {

    /**
     * 根据B用户ID查询绑定的二维码列表
     * @param idBuser B用户ID
     * @return 二维码列表
     */
    @Select("select qrcode.* from b_user\n" +
            "        left join b_user_bind_qrcode on b_user_bind_qrcode.id_buser = b_user.id\n" +
            "        left join qrcode on qrcode.id = b_user_bind_qrcode.id_qrcode\n" +
            "        where b_user.id=#{idBuser}")
    List<Qrcode> selectQrcodesByBuserId(@Param("idBuser") Long idBuser);
}
