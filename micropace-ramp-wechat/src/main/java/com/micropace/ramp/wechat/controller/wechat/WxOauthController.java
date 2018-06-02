package com.micropace.ramp.wechat.controller.wechat;

import com.micropace.ramp.base.common.BaseController;
import com.micropace.ramp.base.common.ResponseMsg;
import com.micropace.ramp.core.GlobalParamCache;
import com.micropace.ramp.base.entity.WxApp;
import com.micropace.ramp.service.IWxAppService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/service/oauth")
public class WxOauthController extends BaseController {

    @Autowired
    private GlobalParamCache globalCache;
    @Autowired
    private IWxAppService iWxAppService;

    /** 自定义配置域名，www.name.com */
    @Value("${server.domain}")
    private String serverDomain;

    /**
     *
     * 前端首页入口，跳转到网页授权URL
     *
     * <p>
     *     url: http://host/api/service/oauth/login
     *     param: wxId=xxxxx
     * </p>
     * <p>
     *     callback: http:host/api/service/oauth/callback
     *     param: code=xxxx, state=xxxxx
     * </p>
     *
     * 其中，wxId必须配置，是作为识别公众号的标识
     * 为了在回调接口中识别公众号，将state设置为wxId的值
     *
     * @param wxId 公众号原始ID
     * @param response void 将会发起Oauth2请求。
     */
    @GetMapping("/login")
    public void index(@RequestParam("wxId") String wxId,
                      HttpServletResponse response) {
        WxApp wxApp = globalCache.getWxApp(wxId);
        if (wxApp != null) {
            WxMpService wxMpService = globalCache.getMpService(wxId);
            if (wxMpService != null) {
                String redirectUri = String.format("http://%s/api/service/oauth/callback", serverDomain);
                String url = wxMpService.oauth2buildAuthorizationUrl(redirectUri, WxConsts.OAuth2Scope.SNSAPI_USERINFO, wxId);
                try {
                    response.sendRedirect(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * 网页授权回调接口
     *
     * TODO token的缓存处理，后续怎么跳转页面等。
     *
     * @param code Oauth接口返回的code值
     * @param state 自定义state参数，约定state为公众号的原始ID，以识别公众号
     * @return 授权后的用户信息
     */
    @GetMapping("/callback")
    public ResponseMsg oauthCallback(@RequestParam("code") String code,
                                     @RequestParam("state") String state) {
        WxMpService wxMpService = globalCache.getMpService(state);
        if (wxMpService != null) {
            try {
                WxMpOAuth2AccessToken accessToken = wxMpService.oauth2getAccessToken(code);
                WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(accessToken, null);
                return success(wxMpUser);

            } catch (WxErrorException e) {
                e.printStackTrace();
            }

            // TODO 这里可以进行跳转，携带用户信息参数

            return success();
        }
        return error("");
    }
}
