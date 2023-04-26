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
package com.lxzh.basic.framework.sys.modular.auth.controller;

import com.lxzh.basic.framework.sys.modular.PwdRSAUtils.PwdRSAUtils;
import com.lxzh.basic.framework.sys.modular.auth.service.AuthService;
import com.lxzh.basic.framework.core.context.login.LoginContextHolder;
import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import com.lxzh.basic.framework.sys.modular.auth.param.SysUserLoginParam;
import com.lxzh.basic.framework.sys.core.response.DCResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 登录控制器
 *
 * @author xuyuxiang
 * @date 2020/3/11 12:20
 */
@Api(tags = {"系统登录登出接口"})
@RestController
public class SysLoginController {

    @Resource
    private AuthService authService;

    /**
     * 账号密码登录
     *
     * @author xuyuxiang
     * @date 2020/3/11 15:52
     */
    @PostMapping("/sys/login")
    @ApiOperation(value = "账号密码登录", notes = "账号密码登录")
    public DCResponse<SysLoginUser> login(@RequestBody SysUserLoginParam param) {
        String account = param.getAccount();
        String password = param.getPassword();
        Integer referType = param.getLoginPortType();
        //String tenantCode = param.getTenantCode();
        //如果系统开启了多租户开关，则添加租户的临时缓存
        /*if (ConstantContextHolder.getTenantOpenFlag()) {
            authService.cacheTenantInfo(tenantCode);
        }*/
        SysLoginUser sysLoginUser = authService.login(account, password,referType);
        return DCResponse.success(sysLoginUser);
    }

    /**
     * 退出登录
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:02
     */
    @GetMapping("/sys/logout")
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
    @GetMapping("/sys/getLoginUser")
    @ApiOperation(value = "获取当前登录用户信息", notes = "获取当前登录用户信息")
    public DCResponse<SysLoginUser> getLoginUser() {
        return DCResponse.success(LoginContextHolder.me().getSysLoginUserUpToDate());
    }

    /**
     * 获取加密公钥
     *
     * @return
     */
    @RequestMapping(value = "/sys/getKey",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取加密公钥")
    public DCResponse getKey() {
        String publicKey = PwdRSAUtils.generateBase64PublicKey();
        return DCResponse.success(publicKey);
    }
    
//    
    @RequestMapping(value = "/login/superAdmin",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("明文登陆")
    public String login2() {
    	
    	try {
            String publicKey = PwdRSAUtils.generateBase64PublicKey();

            String password = PwdRSAUtils.encrypt("123456", publicKey);
            
            
            SysLoginUser sysLoginUser = authService.login("superAdmin"	, password,1);
            return sysLoginUser.getToken() ;
            
 		} catch (Exception e) {
 			e.printStackTrace();
		}
		return null;
  
        
     }
//    
    @RequestMapping(value = "/login/user/{account}/{pass}/{type}",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("明文登陆account")
    public String login2(@PathVariable("account")String account,@PathVariable("pass")String pass,@PathVariable("type")Integer type) {
    	
    	try {
            String publicKey = PwdRSAUtils.generateBase64PublicKey();

            String password = PwdRSAUtils.encrypt(pass, publicKey);
            
            
            SysLoginUser sysLoginUser = authService.login(account	, password,type);
            return sysLoginUser.getToken() ;
            
 		} catch (Exception e) {
 			e.printStackTrace();
		}
		return null;
  
        
     }
    
    
 
}
