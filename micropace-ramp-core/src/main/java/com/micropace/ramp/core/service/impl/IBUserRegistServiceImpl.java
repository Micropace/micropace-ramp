package com.micropace.ramp.core.service.impl;

import com.micropace.ramp.base.common.RedisKeyBuilder;
import com.micropace.ramp.base.constant.RedisSuffixConst;
import com.micropace.ramp.core.config.RedisManager;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.entity.Session;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.RegisterStatusEnum;
import com.micropace.ramp.core.service.IBUserRegistService;
import com.micropace.ramp.core.service.IBUserService;
import com.micropace.ramp.core.service.IWxAppService;
import com.micropace.ramp.base.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IBUserRegistServiceImpl implements IBUserRegistService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisManager redisManager;
    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IBUserService IBUserService;

    @Override
    public boolean isSessionActive(String wxId, String openid) {
        String key = RedisKeyBuilder.getSesstionKey(wxId, openid, RedisSuffixConst.REGISTER);
        return redisManager.helper()
                .getObj(key, Session.class) != null;
    }

    @Override
    public String applyRegist(String wxId, String openid) {
        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        if(wxApp != null) {
            // 检查是否已注册过
            if (IBUserService.isRegisted(wxApp.getId(), openid)) {
                return REPLY_REGISTER_APPLY_REPEAT;
            }

            // 检查会话是否开启
            String key = RedisKeyBuilder.getSesstionKey(wxId, openid, RedisSuffixConst.REGISTER);
            Session session = redisManager.helper()
                    .getObj(key, Session.class);
            if(session != null && session.getCurrentStep() >= 1) {
                return REPLY_REGISTER_SESSION_STARTED;
            }

            // 创建注册会话，有效期3分钟 step == 1
            session = new Session(openid, 3);
            boolean result = redisManager.helper()
                    .setExObj(key, session, SESSION_EXPIRE);
            if (result) {
                return REPLY_REGISTER_APPLY_SUCCESS;
            } else {
                logger.error("Fail to create register session, wxId: {}, openid: {}", wxId, openid);
            }
        }
        return null;
    }

    @Override
    public String submitMobile(String wxId, String openid, String mobile) {
        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        if(wxApp != null) {
            // 检查该用户的会话是否存在和有效，是则发送验证码
            String key = RedisKeyBuilder.getSesstionKey(wxId, openid, RedisSuffixConst.REGISTER);
            Session session = redisManager.helper()
                    .getObj(key, Session.class);
            if(session != null) {
                // 手机号校验
                if(!ValidatorUtil.isMobile(mobile)) {
                    return REPLY_REGISTER_MOBILE_ILLIGAL;
                }

                // 将手机号存储到会话，并更新会话状态 step == 2
                session.next(mobile);

                // TODO 生成并发送验证码短信
                String validateCode = "12345";

                // 将验证码存储到会话，并更新会话状态 step == 3
                session.next(validateCode);
                boolean result = redisManager.helper()
                        .setExObj(key, session, SESSION_EXPIRE);
                if (result) {
                    return REPLY_REGISTER_MOBILE_RECIEVED;
                } else {
                    logger.error("Fail to update register session, wxId: {}, openid: {}", wxId, openid);
                }
            }
        }
        return null;
    }

    @Override
    public String validate(String wxId, String openid, String validateCode) {
        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        if(wxApp != null) {
            // 检查该用户的会话是否存在和有效，是则验证，验证通过后分配二维码。
            String key = RedisKeyBuilder.getSesstionKey(wxId, openid, RedisSuffixConst.REGISTER);
            Session session = redisManager.helper()
                    .getObj(key, Session.class);
            if (session != null) {

                // 取出会话中存储的手机号
                String mobile   = (String) session.getStepContent(2);
                // 取出会话中存储的验证码
                String thatCode = (String) session.getStepContent(3);

                // 验证码输入错误
                if (!validateCode.equals(thatCode)) {
                    return REPLY_REGISTER_VALIDATE_FAILED;
                } else {
                    // 结束会话，删除Session
                    redisManager.helper().deleteKey(key);

                    BUser bUser = IBUserService.selectByOpenid(wxApp.getId(), openid);
                    if(bUser != null) {
                        // 验证码正确，提交注册信息, 进入待审核状态
                        bUser.setMobile(mobile);
                        bUser.setStatus(RegisterStatusEnum.PROCESSING.getCode());
                        if (IBUserService.updateById(bUser)) {
                            return REPLY_REGISTER_SUCCESS_FINISH;
                        }
                    }
                }
            }
        }
        return null;
    }

}
