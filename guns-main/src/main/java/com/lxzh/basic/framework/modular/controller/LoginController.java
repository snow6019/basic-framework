/*
Copyright [2020] [https://www.stylefeng.cn]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Guns采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Guns源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/stylefeng/guns-separation
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/stylefeng/guns-separation
6.若您的项目无法满足以上几点，可申请商业授权，获取Guns商业授权许可，请在官网购买授权，地址为 https://www.stylefeng.cn
 */
package com.lxzh.basic.framework.modular.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.lxzh.basic.framework.core.context.login.LoginContextHolder;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import com.lxzh.basic.framework.modular.service.AuthLoginService;
import com.lxzh.basic.framework.modular.utils.DCResponse;
import com.lxzh.basic.framework.modular.utils.KaptchaCodeUtil;
import com.lxzh.basic.framework.modular.vo.VerificationCodeVO;
import com.lxzh.basic.framework.sys.core.cache.KaptchaCache;
import com.lxzh.basic.framework.sys.modular.PwdRSAUtils.PwdRSAUtils;
import com.lxzh.basic.framework.sys.modular.auth.param.SysUserLoginParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 登录控制器
 *
 * @author xuyuxiang
 * @date 2020/3/11 12:20
 */
@Api(tags = {"后台登录登出接口"})
@RestController
@RequestMapping("/v1/ht")
public class LoginController {

    @Resource
    private AuthLoginService authService;
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private KaptchaCache kaptchaCache;

    /**
     * 账号密码登录
     *
     * @author xuyuxiang
     * @date 2020/3/11 15:52
     */
    @PostMapping("/login")
    @ApiOperation(value = "账号密码登录", notes = "账号密码登录")
    public DCResponse<SysLoginUser> login(@RequestBody SysUserLoginParam param) {
        String account = param.getAccount();
        String password = param.getPassword();
        Integer referType = param.getLoginPortType();
        if (!KaptchaCodeUtil.checkCode(param.getUuid(), param.getCode())) {
            throw new ServiceException(500, "验证码错误！");
        }
        SysLoginUser sysLoginUser = authService.login(account, password, referType);
        return DCResponse.success(sysLoginUser);
    }

    /**
     * 退出登录
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:02
     */
    @GetMapping("/logout")
    @ApiOperation(value = "退出登录", notes = "退出登录")
    public DCResponse<Boolean> logout() {
        return DCResponse.success(authService.logout());
    }

    /**
     * 获取当前登录用户信息
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/23 17:57
     */
    @GetMapping("/getLoginUser")
    @ApiOperation(value = "获取当前登录用户信息", notes = "获取当前登录用户信息")
    public DCResponse<SysLoginUser> getLoginUser() {
        return DCResponse.success(LoginContextHolder.me().getSysLoginUserUpToDate());
    }

    /**
     * 获取加密公钥
     *
     * @return
     */
    @RequestMapping(value = "/getKey", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取加密公钥")
    public DCResponse getKey() {
        String publicKey = PwdRSAUtils.generateBase64PublicKey();
        return DCResponse.success(publicKey);
    }

    @ApiOperation(value = "生成验证码")
    @GetMapping("/generate")
    public DCResponse<VerificationCodeVO> generate(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HttpSession session = request.getSession();
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");
        // create the text for the image
        String capText = defaultKaptcha.createText();
        String uuid = UUID.randomUUID().toString();
        BufferedImage bi = defaultKaptcha.createImage(capText);
        System.out.println(uuid + "===" + capText);
        ByteArrayOutputStream out = null;
        out = new ByteArrayOutputStream();
        try {
            out = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", out);
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(out.toByteArray());
            String captchaBase64 = "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
            kaptchaCache.put(uuid, capText);
            return DCResponse.success(VerificationCodeVO.builder().base64Code(captchaBase64).uuid(uuid).build());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
        return DCResponse.error(500, "生成失败");
    }
}
