package com.micropace.ramp.wechat.controller.ramp;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.common.ErrorMsg;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.base.entity.Qrcode;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxAppCategoryEnum;
import com.micropace.ramp.core.service.IQrcodeService;
import com.micropace.ramp.core.service.IWxAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 二维码管理接口
 * 注意：所有带场景值的二维码都是C公众号下的。
 * 目前只允许托管一个C公众号
 *
 * @author Suffrajet
 */
@RestController
@RequestMapping("/api/admin/qrcode")
public class QrCodeController extends BaseController {

    @Autowired
    private IWxAppService iWxAppService;
    @Autowired
    private IQrcodeService iQrcodeService;

    /**
     * 获取指定场景值的二维码
     *
     * @param wxId     C公众号原始ID
     * @param sceneStr 场景值
     * @return ResponseMsg
     */
    @GetMapping("")
    public ResponseMsg getQrcodeInfo(@RequestParam("wxId") String wxId,
                                     @RequestParam("sceneId") String sceneStr) {
        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        if (wxApp == null) {
            return error(ErrorMsg.WX_APP_NOT_TRUST);
        }

        Qrcode qrcode = iQrcodeService.selectBySceneStr(wxApp, sceneStr);
        if (qrcode != null) {
            return success(qrcode);
        } else {
            return error(ErrorMsg.QRCODE_NOT_FOUND);
        }
    }

    /**
     * 生成二维码
     * 临时二维码默认有效期为30天
     *
     * @param params 参数 wxId: C公众号原始ID, sceneStr: 场景值, isPermanent: 1|0 是否是永久的
     * @return ResponseMsg
     */
    @PostMapping("")
    public ResponseMsg createQrcode(@RequestBody Map<String, String> params) {

        String wxId = params.get("wxId");
        String sceneStr = params.get("sceneStr");
        Integer isPermanent = Integer.parseInt(params.get("isPermanent"));

        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        if (wxApp == null) {
            return error(ErrorMsg.WX_APP_NOT_TRUST);
        }
        // 确保公众号是平台已托管的C类型公众号
        if(!WxAppCategoryEnum.TYPE_C.getCode().equals(wxApp.getCategory())) {
            return error(ErrorMsg.QRCODE_ONLY_ALLOW_CTYPE);
        }

        Qrcode qrcode = iQrcodeService.selectBySceneStr(wxApp, sceneStr);
        if (qrcode != null) {
            return error(ErrorMsg.REPEAT_SCENESTR);
        }

        // 创建二维码图片
        Map<String, String> result = iQrcodeService.create(wxApp, sceneStr, isPermanent == 1);
        if (result != null) {
            return success(result);
        }
        return error(ErrorMsg.ILLEGAL_SCENESTR);
    }

    /**
     * 查询已绑定的二维码记录列表
     *
     * @param wxId        C公众号原始ID
     * @param isPermanent 是否永久
     * @return 记录列表
     */
    @GetMapping("/bind/list")
    public ResponseMsg getBindedRecords(@RequestParam("wxId") String wxId,
                                        @RequestParam("isPermanent") Integer isPermanent) {
        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        return wxApp != null ?
                success(this.getQrcodeList(wxApp, isPermanent == 1, true))
                :
                error(ErrorMsg.WX_APP_NOT_TRUST);
    }

    /**
     * 查询空闲的二维码记录列表
     *
     * @param wxId        C公众号原始ID
     * @param isPermanent 是否永久
     * @return 记录列表
     */
    @GetMapping("/idle/list")
    public ResponseMsg getIdleRecords(@RequestParam("wxId") String wxId,
                                      @RequestParam("isPermanent") Integer isPermanent) {
        WxApp wxApp = iWxAppService.selectByWxOriginId(wxId);
        return wxApp != null ?
                success(this.getQrcodeList(wxApp, isPermanent == 1, false))
                :
                error(ErrorMsg.WX_APP_NOT_TRUST);
    }

    private List<Qrcode> getQrcodeList(WxApp wxApp, boolean isPermanent, boolean isBind) {
        List<Qrcode> records;
        if (isBind) {
            records = iQrcodeService.selectBindedQrcodes(wxApp, isPermanent);
        } else {
            records = iQrcodeService.selectIdleQrcodes(wxApp, isPermanent);
        }
        return records;
    }

}
