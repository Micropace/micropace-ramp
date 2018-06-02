package com.micropace.ramp.wechat.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.enums.ErrorMsg;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.base.constant.GlobalConst;
import com.micropace.ramp.base.enums.WxAppCategoryEnum;
import com.micropace.ramp.core.config.GlobalParamManager;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.base.enums.WxTypeEnum;
import com.micropace.ramp.core.service.IWxAppService;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 公众号管理
 * B类型的公众号可以托管多个
 * C有两种类型：C公众号，C小程序，可以托管多个
 *
 * @author Sufrajet
 */
@RestController
@RequestMapping("/api/admin/wxapp")
public class WxAppController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GlobalParamManager globalParamManager;
    @Autowired
    private IWxAppService iWxAppService;

    @Value("${server.address}")
    private String serverAddress;

    /**
     * 获取公众号信息
     *
     * @param id 公众号记录ID
     * @return ResponseMsg
     */
    @GetMapping
    public ResponseMsg index(@RequestParam("id") Long id) {
        return success(iWxAppService.selectById(id));
    }

    /**
     * 查询当前所有已托管的公众号
     *
     * @param page     指定分页
     * @param pageSize 分页大小
     * @return ResponseMsg
     */
    @GetMapping("/trust")
    public ResponseMsg index(@RequestParam(name = "page", required = false) Integer page,
                             @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (page == null || pageSize == null) {
            return success(iWxAppService.selectAll());
        }
        return success(iWxAppService.selectPage(new Page<>(page, pageSize)));
    }

    /**
     * 获取当前所有已托管的B类型公众号
     *
     * @param page     指定分页
     * @param pageSize 分页大小
     * @return ResponseMsg
     */
    @GetMapping("/trust/b")
    public ResponseMsg getBLists(@RequestParam(name = "page", required = false) Integer page,
                                 @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        return success(iWxAppService.selectAllByCategory(WxAppCategoryEnum.TYPE_B));
    }

    /**
     * 获取当前所有已托管的C类型公众号
     *
     * @return ResponseMsg
     */
    @GetMapping("/trust/c")
    public ResponseMsg getCLists() {
        return success(iWxAppService.selectAllByCategory(WxAppCategoryEnum.TYPE_C));
    }

    /**
     * 获取当前所有已托管的C类型小程序(小程序一定是C类型的)
     *
     * @return ResponseMsg
     */
    @GetMapping("/trust/miniapp")
    public ResponseMsg getMiniappCList() {
        return success(iWxAppService.selectAllByType(WxTypeEnum.APP_MINI));
    }

    /**
     * 托管公众号
     *
     * @param wxApp 公众号
     * @return ResponseMsg
     */
    @PostMapping("/trust")
    public ResponseMsg insert(@RequestBody WxApp wxApp) {
        // check param
        if (iWxAppService.selectByWxOriginId(wxApp.getWxId()) != null) {
            return error(ErrorMsg.WX_APP_REPEAT_TRUST);
        }
        if (wxApp.getAppId() == null || wxApp.getSecret() == null || wxApp.getToken() == null) {
            return error(ErrorMsg.WX_APP_PARAM_ILLEGAL);
        }
        if (wxApp.getIndustry() == null) {
            return error(ErrorMsg.WX_APP_INDUSTRY_PARAM_ERROR);
        }

        if (iWxAppService.insert(wxApp)) {
            // 创建成功后，创建该公众号的消息服务接口
            globalParamManager.addWxApp(wxApp);
            String url = String.format(GlobalConst.WECHAT_SERVER_TRUST_URL, serverAddress, wxApp.getWxId());

            Map<String, String> result = new HashMap<>();
            result.put("wxId", wxApp.getWxId());
            result.put("url", url);
            return success(result);
        }
        return error(ErrorMsg.WX_APP_TRUST_FAILED);
    }

    /**
     * 更新已托管的公众号信息
     *
     * @param wxApp 公众号最新信息
     * @return ResponseMsg
     */
    @PutMapping("trust")
    public ResponseMsg update(@RequestBody WxApp wxApp) {
        // check param
        if (iWxAppService.selectByWxOriginId(wxApp.getWxId()) == null) {
            return error(ErrorMsg.WX_APP_REPEAT_TRUST);
        }
        if (wxApp.getAppId() == null || wxApp.getSecret() == null || wxApp.getToken() == null) {
            return error(ErrorMsg.WX_APP_PARAM_ILLEGAL);
        }
        if (wxApp.getIndustry() == null) {
            return error(ErrorMsg.WX_APP_INDUSTRY_PARAM_ERROR);
        }

        if (iWxAppService.updateById(wxApp)) {
            globalParamManager.resetWxApp(wxApp);
            return success();
        }
        return error(ErrorMsg.WX_APP_UPDATE_FAILED);
    }

    /**
     * 获取公众号的自定义菜单
     *
     * @param id 公众号记录ID
     * @return 自定义菜单JSON
     */
    @GetMapping("/menu")
    public ResponseMsg getWxMenu(@RequestParam("id") Long id) {
        WxApp wxApp = iWxAppService.selectById(id);
        if (wxApp != null) {
            WxMpService wxMpService = globalParamManager.getMpService(wxApp.getWxId());
            if (wxMpService != null) {
                try {
                    WxMpMenu.WxMpConditionalMenu wxMenu = wxMpService.getMenuService().menuGet().getMenu();
                    return success(wxMenu);
                } catch (WxErrorException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return error(ErrorMsg.WX_APP_GET_MENU_FAILED);
    }

    /**
     * 创建公众号的自定义菜单
     * 菜单示例：
     * <pre>
     *         {
     *             "buttons":[
     *                 {
     *                     "type":"click",
     *                     "name":"主菜单",
     *                     "key":"key1",
     *                 },
     *                 {
     *                     "type":"view",
     *                     "name":"链接示例",
     *                     "url":"https://www.baidu.com",
     *                 },
     *                 {
     *                     "name":"多级菜单",
     *                     "subButtons":[
     *                         {
     *                             "type":"click",
     *                             "name":"子菜单一",
     *                             "key":"key2",
     *                         },
     *                         {
     *                             "type":"click",
     *                             "name":"子菜单二",
     *                             "key":"key3",
     *                         }
     *                     ]
     *                 }
     *             ]
     *         }
     * </pre>
     * @param params 参数 id 公众号记录ID menu 菜单JSON字符串
     * @return ResponseMsg
     */
    @PutMapping("/menu")
    public ResponseMsg setWxMenu(@RequestBody Map<String, String> params) {
        Long id = Long.parseLong(params.get("id"));
        String menuJson = params.get("menu");

        // 菜单反序列化检测
        WxMenu wxMenu = null;
        try {
            wxMenu = mapper.readValue(menuJson, WxMenu.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wxMenu == null) {
            return error(ErrorMsg.WX_APP_MENU_FORMAT_ERROR);
        }

        WxApp wxApp = iWxAppService.selectById(id);
        if (wxApp != null) {
            WxMpService wxMpService = globalParamManager.getMpService(wxApp.getWxId());
            if (wxMpService != null) {
                try {
                    wxMpService.getMenuService().menuCreate(wxMenu);
                    return success();
                } catch (WxErrorException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return error(ErrorMsg.WX_APP_CREATE_MENU_FAILED);
    }

    /**
     * 删除公众号的自定义菜单
     *
     * @param id 公众号记录ID
     * @return ResponseMsg
     */
    @DeleteMapping("/menu")
    public ResponseMsg deleteWxMenu(@RequestParam("id") Long id) {
        WxApp wxApp = iWxAppService.selectById(id);
        if (wxApp != null) {
            WxMpService wxMpService = globalParamManager.getMpService(wxApp.getWxId());
            if (wxMpService != null) {
                try {
                    wxMpService.getMenuService().menuDelete();
                    return success();
                } catch (WxErrorException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return error(ErrorMsg.WX_APP_DELETE_MENU_FAILED);
    }
}
