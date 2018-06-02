package com.micropace.ramp.wechat.controller.cuser;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.enums.ErrorMsg;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.base.entity.CUser;
import com.micropace.ramp.base.entity.BUser;
import com.micropace.ramp.service.ICUserService;
import com.micropace.ramp.service.IRelationService;
import com.micropace.ramp.base.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fans")
public class CUserController extends BaseController {

    @Autowired
    private ICUserService icUserService;
    @Autowired
    private IRelationService iRelationServicel;

    /**
     * 查询已关注的B用户列表
     *
     * @param id C用户ID
     * @return ResponseMsg
     */
    @GetMapping("/follow/list")
    public ResponseMsg getFollowingLords(@RequestParam("id") Long id) {
        CUser cUser = icUserService.selectById(id);
        if (cUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }
        List<BUser> BUsers = iRelationServicel.selectFollowedBusers(cUser.getId());
        return success(BUsers);
    }

    /**
     * 查询C用户profile信息
     *
     * @param id C用户ID
     * @return ResponseMsg
     */
    @GetMapping("/profile")
    public ResponseMsg getProfile(@RequestParam("id") Long id) {
        CUser cUser = icUserService.selectById(id);
        if (cUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }
        return success(cUser);
    }

    /**
     * 更新C用户profile信息
     * 只允许更新手机号
     *
     * @param params 参数
     * @return ResponseMsg
     */
    @PutMapping("/profile")
    public ResponseMsg updateProfile(@RequestBody Map<String, String> params) {

        Long id = Long.parseLong(params.get("id"));
        String mobile = params.get("mobile");
        if (!ValidatorUtil.isMobile(mobile)) {
            return error(ErrorMsg.ILLEGAL_MOBILE);
        }

        CUser cUser = icUserService.selectById(id);
        if (cUser == null) {
            return error(ErrorMsg.USER_NOT_FOUND);
        }

        cUser.setMobile(mobile);
        return icUserService.updateById(cUser) ?
                success()
                :
                error(ErrorMsg.UPDATE_USER_PROFILE_FAILED);
    }

}
