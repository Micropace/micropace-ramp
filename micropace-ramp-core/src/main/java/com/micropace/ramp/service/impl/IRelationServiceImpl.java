package com.micropace.ramp.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.micropace.ramp.base.entity.*;
import com.micropace.ramp.mapper.RelationMapper;
import com.micropace.ramp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IRelationServiceImpl extends ServiceImpl<RelationMapper, Relation> implements IRelationService {

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IBUserService IBUserService;
    @Autowired
    private IQrcodeService iQrcodeService;

    @Override
    public boolean followBUser(CUser cUser, String qrcodeWxId, String sceneStr) {
        WxApp qrcodeWxApp = iWxAppService.selectByWxOriginId(qrcodeWxId);
        if(qrcodeWxApp == null) {
            return false;
        }

        Qrcode qrcode = iQrcodeService.selectBySceneStr(qrcodeWxApp, sceneStr);
        if(qrcode == null) {
            return false;
        }

        BUser BUser = IBUserService.selectByBindedQrcodeId(qrcode.getId());
        if(BUser == null) {
            return false;
        }

        Relation relation = super.baseMapper.selectByBindedIds(BUser.getId(), cUser.getId());
        if(relation == null) {
            relation = new Relation();
            relation.setIdBuser(BUser.getId());
            relation.setIdCuser(cUser.getId());
            return super.baseMapper.insert(relation) == 1;
        } else {
            return true;
        }
    }

    @Override
    public boolean isFollowed(Long idBuser, Long idCuser) {
        return super.baseMapper.selectByBindedIds(idBuser, idCuser) != null;
    }

    @Override
    public List<BUser> selectFollowedBusers(Long idCuser) {
        return super.baseMapper.selectFollowedBusers(idCuser);
    }

    @Override
    public List<CUser> selectFollowers(Long idBuser) {
        return super.baseMapper.selectFollowers(idBuser);
    }
}
