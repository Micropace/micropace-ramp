package com.micropace.ramp.wechat.controller.ramp;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.entity.ClpSignin;
import com.micropace.ramp.core.service.IClpSigninService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clp")
public class ClpSigninDetailController extends BaseController {

    @Autowired
    private IClpSigninService iClpSigninService;

    @GetMapping("/signin/detail")
    public String getSigninDetail() {
        List<ClpSignin> signins = iClpSigninService.selectAll();
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>签到信息:</h2>");
        for (ClpSignin signin : signins) {
            if(signin.getSignInTime() != null) {
                builder.append(String.format("<h4>序号[%d]: %s: %s</h4>", signin.getId(), signin.getName(), signin.getSignInTime()));
            } else {
                builder.append(String.format("<h4>序号[%d]: %s: 未签到</h4>", signin.getId(), signin.getName()));
            }
        }
        return builder.toString();
    }
}
