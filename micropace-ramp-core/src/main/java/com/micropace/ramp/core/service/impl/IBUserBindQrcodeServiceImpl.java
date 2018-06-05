package com.micropace.ramp.core.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.BUserBindQrcode;
import com.micropace.ramp.base.entity.Qrcode;
import com.micropace.ramp.core.mapper.BUserBindQrcodeMapper;
import com.micropace.ramp.core.service.IBUserBindQrcodeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IBUserBindQrcodeServiceImpl extends ServiceImpl<BUserBindQrcodeMapper, BUserBindQrcode> implements IBUserBindQrcodeService {

    @Override
    public List<Qrcode> selectQrcodesByBuserId(Long idBuser) {
        return super.baseMapper.selectQrcodesByBuserId(idBuser);
    }
}
