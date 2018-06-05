package com.micropace.ramp.wechat.controller.ramp;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.common.ErrorMsg;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.entity.BUserBindQrcode;
import com.micropace.ramp.base.entity.Qrcode;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.RegisterStatusEnum;
import com.micropace.ramp.base.enums.QrCodeTypeEnum;
import com.micropace.ramp.core.service.IBUserBindQrcodeService;
import com.micropace.ramp.core.service.IBUserService;
import com.micropace.ramp.core.service.IQrcodeService;
import com.micropace.ramp.core.service.IWxAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * B用户管理接口
 *
 * @author Suffrajet
 */
@RestController
@RequestMapping("/api/admin/lord")
public class ManageBUserController extends BaseController {
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IBUserService iBuserService;
    @Autowired
    private IQrcodeService iQrcodeService;
    @Autowired
    private IBUserBindQrcodeService bindQrcodeService;

    /**
     * Admin审核B用户的注册申请
     * 审核通过：为B用户分配一个永久二维码并绑定
     * TODO 托管多C公众号的情况下，审核的时候需要知道B用户申请的是哪个C公众号。
     *
     * @param id       B用户ID
     * @param isPassed 是否通过审核 1 通过，0 不通过
     * @return ResponseMsg
     */
    @GetMapping("/check")
    public ResponseMsg checkRegister(@RequestParam("id") Long id,
                                     @RequestParam("isPassed") Integer isPassed) {
        BUser bUser = iBuserService.selectById(id);
        if (bUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }

        if (!RegisterStatusEnum.SUCCESSED.getCode().equals(bUser.getStatus())
                && bUser.getMobile() == null) {
            return error(ErrorMsg.USER_NOT_REGIST);
        }

        // 不可重复审核
        if (RegisterStatusEnum.SUCCESSED.getCode().equals(bUser.getStatus())
                || RegisterStatusEnum.FAILED.getCode().equals(bUser.getStatus())) {
            return error(ErrorMsg.REPEAT_CHECK_REGIST);
        }

        if (isPassed == 0) {
            bUser.setStatus(RegisterStatusEnum.FAILED.getCode());
            if (iBuserService.updateById(bUser)) {
                return success();
            }
        } else {
            // 获取一个未被绑定的永久二维码

            // TODO 这里有点问题，需要指定是C公众号的二维码，创建的时候也需要指定C公众号。

            Qrcode qrcode = iQrcodeService.selectIdleOneByType(bUser.getIdWxApp(), QrCodeTypeEnum.PERMANENT.getCode());
            if (qrcode == null) {
                // 尝试生成永久二维码
                String sceneStr = bUser.getMobile() + System.currentTimeMillis();
                WxApp wxApp = iWxAppService.selectById(bUser.getIdWxApp());
                if(wxApp != null) {
                    Map<String, String> result = iQrcodeService.create(wxApp, sceneStr, true);
                    if(result != null) {
                        qrcode = iQrcodeService.selectBySceneStr(wxApp, sceneStr);
                    }
                }
            }
            if(qrcode == null) {
                return error(ErrorMsg.NO_AVAILABLE_QRCODE);
            }
            // 绑定二维码
            bUser.setStatus(RegisterStatusEnum.SUCCESSED.getCode());
            if (iBuserService.updateById(bUser)) {

                // 创建绑定记录
                BUserBindQrcode bindQrcode = new BUserBindQrcode();
                bindQrcode.setIdBuser(bUser.getId());
                bindQrcode.setIdQrcode(qrcode.getId());
                if(bindQrcodeService.insert(bindQrcode)) {

                    // 更新二维码绑定状态
                    qrcode.setIsBind(1);
                    iQrcodeService.updateById(qrcode);
                    Map<String, String> result = new HashMap<>();
                    result.put("openid", bUser.getOpenid());
                    result.put("filename", qrcode.getFilename());
                    result.put("sceneStr", qrcode.getSceneStr());
                    result.put("url", qrcode.getWxurl());
                    result.put("path", "/qrcode/" + qrcode.getFilename());
                    return success(result);
                }
            }
        }

        return error(ErrorMsg.CHECK_REGIST_FAILED);
    }

    /**
     * 获取注册审核已通过的B用户列表
     *
     * @param idWxApp 公众号记录ID
     * @return ResponseMsg
     */
    @GetMapping("/pass/list")
    public ResponseMsg getPassedLords(@RequestParam("idWxApp") Long idWxApp) {
        WxApp wxApp = iWxAppService.selectById(idWxApp);
        if (wxApp == null) {
            return error(ErrorMsg.WX_APP_NOT_TRUST);
        }
        List<BUser> passedBUsers = iBuserService.selectSpecifiedStatusList(idWxApp, RegisterStatusEnum.SUCCESSED);
        return success(passedBUsers);
    }

    /**
     * 获取注册待审核的B用户列表
     *
     * @param idWxApp 公众号记录ID
     * @return ResponseMsg
     */
    @GetMapping("/processing/list")
    public ResponseMsg getProcessingLords(@RequestParam("idWxApp") Long idWxApp) {
        WxApp wxApp = iWxAppService.selectById(idWxApp);
        if (wxApp == null) {
            return error(ErrorMsg.WX_APP_NOT_TRUST);
        }
        List<BUser> passedBUsers = iBuserService.selectSpecifiedStatusList(idWxApp, RegisterStatusEnum.PROCESSING);
        return success(passedBUsers);
    }

    /**
     * 获取注册审核未通过的B用户列表
     *
     * @param idWxApp 公众号记录ID
     * @return ResponseMsg
     */
    @GetMapping("/unpassed/list")
    public ResponseMsg getUnPassedLords(@RequestParam("idWxApp") Long idWxApp) {
        WxApp wxApp = iWxAppService.selectById(idWxApp);
        if (wxApp == null) {
            return error(ErrorMsg.WX_APP_NOT_TRUST);
        }
        List<BUser> passedBUsers = iBuserService.selectSpecifiedStatusList(idWxApp, RegisterStatusEnum.FAILED);
        return success(passedBUsers);
    }
}
