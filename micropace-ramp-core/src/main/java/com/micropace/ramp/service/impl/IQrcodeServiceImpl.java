package com.micropace.ramp.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.Qrcode;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.QrCodeTypeEnum;
import com.micropace.ramp.core.GlobalParamCache;
import com.micropace.ramp.mapper.QrcodeMapper;
import com.micropace.ramp.service.IQrcodeService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IQrcodeServiceImpl extends ServiceImpl<QrcodeMapper, Qrcode> implements IQrcodeService {

    @Autowired
    private GlobalParamCache globalCache;
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public Qrcode selectBySceneStr(WxApp wxApp, String sceneStr) {
        return baseMapper.selectBySceneStr(wxApp.getId(), sceneStr);
    }

    @Override
    public Map<String, String> create(WxApp wxApp, String sceneStr, boolean isPermanent) {
        Map<String, String> result = null;

        WxMpService wxMpService = globalCache.getMpService(wxApp.getWxId());
        if(wxMpService != null) {
            try {
                WxMpQrCodeTicket ticket;
                if(isPermanent) {
                    ticket = wxMpService.getQrcodeService().qrCodeCreateLastTicket(sceneStr);
                } else {
                    ticket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(sceneStr, 2592000);
                }

                if(ticket != null) {
                    File file = wxMpService.getQrcodeService().qrCodePicture(ticket);
                    String url = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket(), false);

                    // 将二维码图片存储到本地
                    String path = uploadPath + "qrcode/";
                    File dir = new File(path);
                    if(!dir.exists()) {
                        if(dir.mkdirs()) {
                            String filename = sceneStr + ".jpg";
                            if(file.renameTo(new File(path + filename))) {

                                // 创建记录
                                Qrcode qrcode = new Qrcode();
                                qrcode.setIdWxApp(wxApp.getId());
                                qrcode.setSceneStr(sceneStr);
                                qrcode.setType(isPermanent ? QrCodeTypeEnum.PERMANENT.getCode() : QrCodeTypeEnum.PROVISIONAL.getCode());
                                qrcode.setTicket(ticket.getTicket());
                                qrcode.setWxurl(url);
                                qrcode.setLocalPath(path + filename);
                                qrcode.setFilename(filename);
                                qrcode.setIsBind(0);
                                qrcode.setExpire(ticket.getExpireSeconds());
                                if(super.baseMapper.insert(qrcode) == 1) {
                                    result = new HashMap<>();
                                    result.put("sceneStr", sceneStr);
                                    result.put("path", "qrcode/" + filename);
                                    result.put("filename", filename);
                                    if(!isPermanent) {
                                        result.put("expires", String.format("%d天", ticket.getExpireSeconds() / ( 24 * 60 * 60) ));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<Qrcode> selectBindedQrcodes(WxApp wxApp, boolean isPermanent) {
        Integer type = isPermanent ? QrCodeTypeEnum.PERMANENT.getCode() : QrCodeTypeEnum.PROVISIONAL.getCode();
        return super.baseMapper.selectSpecifiedTypeRecords(wxApp.getId(), type, 1);
    }

    @Override
    public List<Qrcode> selectIdleQrcodes(WxApp wxApp, boolean isPermanent) {
        Integer type = isPermanent ? QrCodeTypeEnum.PERMANENT.getCode() : QrCodeTypeEnum.PROVISIONAL.getCode();
        return super.baseMapper.selectSpecifiedTypeRecords(wxApp.getId(), type, 0);
    }

    @Override
    public Qrcode selectIdleOneByType(Long idWxApp, Integer type) {
        return super.baseMapper.selectOneNotBindedByType(idWxApp, type);
    }
}
