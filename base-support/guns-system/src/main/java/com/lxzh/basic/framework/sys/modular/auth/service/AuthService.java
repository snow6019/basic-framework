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
package com.lxzh.basic.framework.sys.modular.auth.service;

import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;
import com.lxzh.basic.framework.sys.modular.auth.param.SysUserLoginParam;
import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证相关service
 *
 * @author xuyuxiang
 * @date 2020/3/11 14:14
 */
public interface AuthService {

    /**
     * 账号密码登录
     *
     * @param account  账号
     * @param password 密码
     * @return token
     * @author xuyuxiang
     * @date 2020/3/11 15:57
     */
    SysLoginUser login(String account, String password, Integer referType);

    /**
     * 从request获取token
     *
     * @param request request
     * @return token
     * @author xuyuxiang
     * @date 2020/3/13 11:41
     */
    String getTokenFromRequest(HttpServletRequest request);

    /**
     * 根据token获取登录用户信息
     *
     * @param token token
     * @return 当前登陆的用户信息
     * @author xuyuxiang
     * @date 2020/3/13 11:59
     */
    SysLoginUser getLoginUserByToken(String token);

    /**
     * 退出登录
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:03
     */
    Boolean logout();

    /**
     * 设置SpringSecurityContext上下文，方便获取用户
     *
     * @param sysLoginUser 当前登录用户信息
     * @author xuyuxiang
     * @date 2020/3/19 19:59
     */
    void setSpringSecurityContextAuthentication(SysLoginUser sysLoginUser);

    /**
     * 获取SpringSecurityContext中认证信息
     *
     * @return 认证信息
     * @author xuyuxiang
     * @date 2020/3/19 20:02
     */
    Authentication getAuthentication();

    /**
     * 校验token是否正确
     *
     * @param token token
     * @author xuyuxiang
     * @date 2020/5/28 9:57
     */
    void checkToken(String token);

    /**
     * 临时缓存租户信息
     *
     * @param tenantCode 多租户编码
     * @author fengshuonan
     * @date 2020/9/3 21:22
     */
    void cacheTenantInfo(String tenantCode);

    /**
     * 根据系统用户构造用户登陆信息
     *
     * @param sysUser 系统用户
     * @return 用户信息
     * @author xuyuxiang
     * @date 2020/9/20 15:21
     **/
    SysLoginUser genSysLoginUser(SysUser sysUser);

    boolean modifyPassword(String oldPassword, String newPassword,Long userId);

    String loginByOpenId(String openid,Integer loginType);

    String sendWxMessage(String phone);

    /**
     * 验证手机号
     * @param code
     * @param phone
     * @return
     */
    boolean verifyCode(String code, String phone);

    SysLoginUser loginByPhoneCode(SysUserLoginParam param);

    SysLoginUser wxlogin(String account, String password, Integer loginPortType);
}
