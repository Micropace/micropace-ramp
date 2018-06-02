package com.micropace.ramp.wechat.controller.buser;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.enums.ErrorMsg;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.base.entity.CUser;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.base.entity.Qrcode;
import com.micropace.ramp.base.enums.RegisterStatusEnum;
import com.micropace.ramp.service.IBUserService;
import com.micropace.ramp.service.IQrcodeService;
import com.micropace.ramp.service.IRelationService;
import com.micropace.ramp.base.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * B公众号用户业务接口
 *
 * @author Suffrajet
 */
@RestController
@RequestMapping("/api/lord")
public class BUserController extends BaseController {

    @Autowired
    private IBUserService ibUserService;
    @Autowired
    private IQrcodeService iQrcodeService;
    @Autowired
    private IRelationService iRelationService;

    /**
     * 申请注册
     * 注册只需要手机号，其它信息后续页面中可补充完整
     *
     * @param params id B用户ID，mobile 手机号
     * @return ResponseMsg
     */
    @PostMapping("/register")
    public ResponseMsg register(@RequestBody Map<String, String> params) {
        Long id         = Long.parseLong(params.get("id"));
        String mobile   = params.get("mobile");

        // 参数校验
        BUser bUser = ibUserService.selectById(id);
        if (bUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }
        if (!ValidatorUtil.isMobile(mobile)) {
            return error(ErrorMsg.ILLEGAL_MOBILE);
        }
        if (ibUserService.isRegisted(bUser.getIdWxApp(), bUser.getOpenid())) {
            return error(ErrorMsg.REPEAT_REGIST_MOBILE);
        }
        if (ibUserService.isRegistProcessing(bUser.getIdWxApp(), bUser.getOpenid())) {
            return error(ErrorMsg.REGIST_CHECK_IS_PROCESSING);
        }

        //提交注册信息, 进入待审核状态
        bUser.setMobile(mobile);
        bUser.setStatus(RegisterStatusEnum.PROCESSING.getCode());
        if (ibUserService.updateById(bUser)) {
            return success(ErrorMsg.REGIST_APPLY_SUCCESS);
        }
        return error(ErrorMsg.REGIST_APPLY_FAILED);
    }

    /**
     * 查询注册申请的状态
     *
     * @param id B用户ID
     * @return ResponseMsg
     */
    @GetMapping("/register/status")
    public ResponseMsg getRegisterStatus(@RequestParam("id") Long id) {
        BUser bUser = ibUserService.selectById(id);
        if (bUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }

        Map<String, Object> result = new HashMap<>();
        int status = bUser.getStatus();
        result.put("state", status);
        String message = RegisterStatusEnum.getDescByCode(status);
        if(message != null) {
            result.put("message", message);
        }
        return success(result);
    }

    /**
     * 查询B用户Profile信息
     *
     * @param id B用户ID
     * @return ResponseMsg
     */
    @GetMapping("/profile")
    public ResponseMsg getProfile(@RequestParam("id") Long id) {
        BUser bUser = ibUserService.selectById(id);
        if (bUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }
        return success(bUser);
    }

    /**
     * 更新B用户Profile信息
     * 其中只允许更新包络: 姓名、手机号、所属行业名称、所在地址、描述信息
     *
     * @param params 参数
     * @return ResponseMsg
     */
    @PutMapping("/profile")
    public ResponseMsg updateProfile(@RequestBody Map<String, String> params) {
        Long id         = Long.parseLong(params.get("id"));
        String name     = params.get("name");
        String mobile   = params.get("mobile");
        String brand    = params.get("brand");
        String address  = params.get("address");
        String desc     = params.get("description");

        // 参数校验
        BUser bUser = ibUserService.selectById(id);
        if (bUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }

        if (mobile != null) {
            if (!ValidatorUtil.isMobile(mobile)) {
                return error(ErrorMsg.ILLEGAL_MOBILE);
            } else {
                bUser.setMobile(mobile);
            }
        }
        if(name != null) {
            bUser.setName(name);
        }
        if (brand != null) {
            bUser.setBrand(brand);
        }
        if (address != null) {
            bUser.setAddress(address);
        }
        if(desc != null) {
            bUser.setDescription(desc);
        }

        return ibUserService.updateById(bUser) ?
                success()
                :
                error(ErrorMsg.UPDATE_USER_PROFILE_FAILED);
    }

    /**
     * 查询分配的二维码信息
     *
     * @param id B用户ID
     * @return ResponseMsg
     */
    @GetMapping("/qrcode")
    public ResponseMsg getBindedQrcodeInfo(@RequestParam("id") Long id) {
        BUser bUser = ibUserService.selectById(id);
        if (bUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }
        if (bUser.getStatus() == null || bUser.getStatus() == RegisterStatusEnum.DEFAULT.getCode()) {
            return error(ErrorMsg.USER_NOT_REGIST);
        }
        if (bUser.getStatus() == RegisterStatusEnum.PROCESSING.getCode()) {
            return error(ErrorMsg.REGIST_CHECK_IS_PROCESSING);
        }
        if (bUser.getStatus() == RegisterStatusEnum.FAILED.getCode()) {
            return error(ErrorMsg.REGIST_CHECK_DENY);
        }
        Qrcode qrcode = iQrcodeService.selectById(bUser.getIdQrcode());
        if (qrcode != null) {
            Map<String, String> result = new HashMap<>();
            result.put("openid", bUser.getOpenid());
            result.put("filename", qrcode.getFilename());
            result.put("sceneStr", qrcode.getSceneStr());
            result.put("url", qrcode.getWxurl());
            result.put("path", "/qrcode/" + qrcode.getFilename());
            return success(result);
        }
        return error(ErrorMsg.QUERY_QRCODE_FAILED);
    }

    @GetMapping("/fans/list")
    public ResponseMsg getFansList(@RequestParam("id") Long id) {
        BUser BUser = ibUserService.selectById(id);
        if (BUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }
        List<CUser> fans = iRelationService.selectFollowers(BUser.getId());
        return success(fans);
    }
}
