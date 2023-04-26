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
package com.lxzh.basic.framework.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.context.constant.ConstantContextHolder;
import com.lxzh.basic.framework.core.dbs.CurrentDataSourceContext;
import com.lxzh.basic.framework.core.enums.CommonStatusEnum;
import com.lxzh.basic.framework.core.exception.AuthException;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.exception.enums.AuthExceptionEnum;
import com.lxzh.basic.framework.core.exception.enums.ServerExceptionEnum;
import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import com.lxzh.basic.framework.core.tenant.context.TenantCodeHolder;
import com.lxzh.basic.framework.core.tenant.context.TenantDbNameHolder;
import com.lxzh.basic.framework.core.tenant.entity.TenantInfo;
import com.lxzh.basic.framework.core.tenant.exception.TenantException;
import com.lxzh.basic.framework.core.tenant.exception.enums.TenantExceptionEnum;
import com.lxzh.basic.framework.core.tenant.service.TenantInfoService;
import com.lxzh.basic.framework.core.util.HttpServletUtil;
import com.lxzh.basic.framework.core.util.IpAddressUtil;
import com.lxzh.basic.framework.modular.service.AuthLoginService;
import com.lxzh.basic.framework.modular.utils.FinalStrCode;
import com.lxzh.basic.framework.modular.utils.PwdRSAUtils.PwdRSAUtils;
import com.lxzh.basic.framework.sys.core.cache.UserCache;
import com.lxzh.basic.framework.sys.core.enums.LogSuccessStatusEnum;
import com.lxzh.basic.framework.sys.core.jwt.JwtPayLoad;
import com.lxzh.basic.framework.sys.core.jwt.JwtTokenUtil;
import com.lxzh.basic.framework.sys.core.log.LogManager;
import com.lxzh.basic.framework.sys.modular.auth.eunms.LoginPortTypeEnum;
import com.lxzh.basic.framework.sys.modular.auth.factory.LoginUserFactory;
import com.lxzh.basic.framework.sys.modular.auth.param.SysUserLoginParam;
import com.lxzh.basic.framework.sys.modular.menu.service.SysMenuService;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserService;
import com.supone.sms.service.SMSSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 认证相关service实现类
 *
 * @author xuyuxiang
 * @date 2020/3/11 16:58
 */
@Transactional(rollbackFor = RuntimeException.class)
@Service
public class AuthLoginServiceImpl implements AuthLoginService, UserDetailsService {

    private static final Integer BUFFER_SIZE = 4096;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserCache userCache;
    @Resource(name = "weiMiSms")
    private SMSSender smsSender;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public SysLoginUser login(String account, String password, Integer referType) {

        if (ObjectUtil.hasEmpty(account, password)) {
            LogManager.me().executeLoginLog(account, LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_EMPTY.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_EMPTY);
        }

        SysUser sysUser = sysUserService.getUserByCount(account, referType);

        //用户不存在，账号或密码错误
        if (ObjectUtil.isEmpty(sysUser)) {
            LogManager.me().executeLoginLog(account, LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_ERROR.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_ERROR);
        }

        String passwordBcrypt = sysUser.getPassword();
        password = PwdRSAUtils.decryptBase64(password);
        //验证账号密码是否正确
        if (ObjectUtil.isEmpty(passwordBcrypt) || !BCrypt.checkpw(password, passwordBcrypt)) {
            LogManager.me().executeLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_ERROR.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_ERROR);
        }

        return this.doLogin(sysUser);
    }

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        String authToken = request.getHeader(CommonConstant.AUTHORIZATION);
        if (ObjectUtil.isEmpty(authToken)) {
            return null;
        } else {
            //token不是以Bearer打头，则响应回格式不正确
            /*if (!authToken.startsWith(CommonConstant.TOKEN_TYPE_BEARER)) {
                throw new AuthException(AuthExceptionEnum.NOT_VALID_TOKEN_TYPE);
            }
            try {
                authToken = authToken.substring(CommonConstant.TOKEN_TYPE_BEARER.length() + 1);
            } catch (StringIndexOutOfBoundsException e) {
                throw new AuthException(AuthExceptionEnum.NOT_VALID_TOKEN_TYPE);
            }*/
        }

        return authToken;
    }

    @Override
    public SysLoginUser getLoginUserByToken(String token) {

        //校验token，错误则抛异常
        this.checkToken(token);

        //根据token获取JwtPayLoad部分
        JwtPayLoad jwtPayLoad = JwtTokenUtil.getJwtPayLoad(token);

        //从redis缓存中获取登录用户
        Object cacheObject = userCache.get(jwtPayLoad.getUuid());
        Long userId = jwtPayLoad.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
//        if(ObjectUtil.isEmpty(cacheObject) || ObjectUtil.isEmpty(sysUser) || !Objects.nonNull(sysUser.getStatus()) || sysUser.getStatus()==1 || sysUser.getStatus()==2){
        if (ObjectUtil.isEmpty(sysUser) || !Objects.nonNull(sysUser.getStatus()) || sysUser.getStatus() == 1 || sysUser.getStatus() == 2) {
            throw new AuthException(AuthExceptionEnum.ACCOUNT_EXCIPTION);
        }
        if (!Objects.equals(sysUser.getLoginPortType(), LoginPortTypeEnum.NOT_RECTIFY.getCode()) && (StringUtils.isBlank(sysUser.getLastToken()) || !Objects.equals(sysUser.getLastToken(), token))) {
            throw new AuthException(AuthExceptionEnum.ACCOUNT_HAS_LOGIN_BY_OTHER);
        }
        //构造SysLoginUser
        SysLoginUser sysLoginUser1 = this.genSysLoginUser(sysUser);

        //用户不存在则表示登录已过期
        if (ObjectUtil.isEmpty(cacheObject)) {
            if (ObjectUtil.isEmpty(sysLoginUser1)) {
                throw new AuthException(AuthExceptionEnum.ACCOUNT_EXCIPTION);
            } else {
                cacheObject = sysLoginUser1;
            }
        }

        //转换成登录用户
        SysLoginUser sysLoginUser = (SysLoginUser) cacheObject;

        //用户存在, 无痛刷新缓存，在登录过期前活动的用户自动刷新缓存时间
        this.cacheLoginUser(jwtPayLoad, sysLoginUser);

        //返回用户
        return sysLoginUser;
    }

    @Override
    public Boolean logout() {

        HttpServletRequest request = HttpServletUtil.getRequest();

        if (ObjectUtil.isNotNull(request)) {

            //获取token
            String token = this.getTokenFromRequest(request);

            if (StrUtil.isNotBlank(token)) {
                //校验token，错误则抛异常，待确定
                this.checkToken(token);

                //根据token获取JwtPayLoad部分
                JwtPayLoad jwtPayLoad = JwtTokenUtil.getJwtPayLoad(token);

                //获取缓存的key
                String loginUserCacheKey = jwtPayLoad.getUuid();
                this.clearUser(loginUserCacheKey, jwtPayLoad.getAccount());
            }

            return true;

        } else {
            throw new ServiceException(ServerExceptionEnum.REQUEST_EMPTY);
        }
    }

    @Override
    public void setSpringSecurityContextAuthentication(SysLoginUser sysLoginUser) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        sysLoginUser,
                        null,
                        sysLoginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void checkToken(String token) {
        //校验token是否正确
        Boolean tokenCorrect = JwtTokenUtil.checkToken(token);
        if (!tokenCorrect) {
            throw new AuthException(AuthExceptionEnum.REQUEST_TOKEN_ERROR);
        }

        //校验token是否失效
        Boolean tokenExpired = JwtTokenUtil.isTokenExpired(token);
        if (tokenExpired) {
            throw new AuthException(AuthExceptionEnum.LOGIN_EXPIRED);
        }
    }

    @Override
    public void cacheTenantInfo(String tenantCode) {
        if (StrUtil.isBlank(tenantCode)) {
            return;
        }

        // 从spring容器中获取service，如果没开多租户功能，没引入相关包，这里会报错
        TenantInfoService tenantInfoService = null;
        try {
            tenantInfoService = SpringUtil.getBean(TenantInfoService.class);
        } catch (Exception e) {
            throw new TenantException(TenantExceptionEnum.TENANT_MODULE_NOT_ENABLE_ERROR);
        }

        // 获取租户信息
        TenantInfo tenantInfo = tenantInfoService.getByCode(tenantCode);
        if (tenantInfo != null) {
            String dbName = tenantInfo.getDbName();

            // 租户编码的临时存放
            TenantCodeHolder.put(tenantCode);

            // 租户的数据库名称临时缓存
            TenantDbNameHolder.put(dbName);

            // 数据源信息临时缓存
            CurrentDataSourceContext.setDataSourceType(dbName);
        } else {
            throw new TenantException(TenantExceptionEnum.CNAT_FIND_TENANT_ERROR);
        }
    }

    @Override
    public SysLoginUser loadUserByUsername(String account) throws UsernameNotFoundException {
        SysLoginUser sysLoginUser = new SysLoginUser();
        SysUser user = sysUserService.getUserByCount(account);
        BeanUtil.copyProperties(user, sysLoginUser);
        return sysLoginUser;
    }

    /**
     * 根据key清空登陆信息
     *
     * @author xuyuxiang
     * @date 2020/6/19 12:28
     */
    void clearUser(String loginUserKey, String account) {
        //获取缓存的用户
        Object cacheObject = userCache.get(loginUserKey);

        //如果缓存的用户存在，清除会话，否则表示该会话信息已失效，不执行任何操作
        if (ObjectUtil.isNotEmpty(cacheObject)) {
            //清除登录会话
            userCache.remove(loginUserKey);
            //创建退出登录日志
            LogManager.me().executeExitLog(account);
        }
    }

    /**
     * 执行登录方法
     *
     * @author xuyuxiang
     * @date 2020/3/12 10:43
     */
    private SysLoginUser doLogin(SysUser sysUser) {

        Integer sysUserStatus = sysUser.getStatus();

        //验证账号是否被冻结
        if (Objects.equals(sysUser.getLoginPortType(), LoginPortTypeEnum.NOT_RECTIFY.getCode()) && CommonStatusEnum.DISABLE.getCode().equals(sysUserStatus)) {
            LogManager.me().executeLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_FREEZE_ERROR.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_FREEZE_ERROR);
        }

        //构造SysLoginUser
        SysLoginUser sysLoginUser = this.genSysLoginUser(sysUser);

        //构造jwtPayLoad
        JwtPayLoad jwtPayLoad = new JwtPayLoad(sysUser.getId(), sysUser.getAccount());

        //生成token
        String token = JwtTokenUtil.generateToken(jwtPayLoad);

        //缓存token与登录用户信息对应, 默认2个小时
        this.cacheLoginUser(jwtPayLoad, sysLoginUser);

        //设置最后登录ip和时间
        sysUser.setLastLoginIp(IpAddressUtil.getIp(HttpServletUtil.getRequest()));
        sysUser.setLastLoginTime(DateTime.now());
        sysUser.setLastToken(token);
        //更新用户登录信息
        sysUserService.updateById(sysUser);

        //登录成功，记录登录日志
        LogManager.me().executeLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.SUCCESS.getCode(), null);

        //登录成功，设置SpringSecurityContext上下文，方便获取用户
        this.setSpringSecurityContextAuthentication(sysLoginUser);

        LoginPortTypeEnum loginPortTypeEnum = LoginPortTypeEnum.getEnumByCode(sysLoginUser.getLoginPortType());
        Map<String, SysLoginUser> allLoginUsers = userCache.getAllKeyValues();
        switch (loginPortTypeEnum) {
            case NOT_RECTIFY:
                sysLoginUser.setPermissionIds(sysMenuService.getLoginPermissionIds(sysLoginUser.getId(), sysUser, null, null));
                break;
            case WXCUSTM:
                for (Map.Entry<String, SysLoginUser> loginedUserEntry : allLoginUsers.entrySet()) {
                    String loginedUserKey = loginedUserEntry.getKey();
                    SysLoginUser loginedUser = loginedUserEntry.getValue();
                    //单用户登录
                    //如果账号名称相同，并且redis缓存key和刚刚生成的用户的uuid不一样，则清除以前登录的
                    if (Objects.equals(loginedUser.getId(), sysLoginUser.getId())
                            && !loginedUserKey.equals(jwtPayLoad.getUuid())) {
                        this.clearUser(loginedUserKey, loginedUser.getAccount());
                    }
                }
                break;
            default:
                break;
        }

        //如果开启限制单用户登陆，则踢掉原来的用户
        Boolean enableSingleLogin = ConstantContextHolder.getEnableSingleLogin();
        if (enableSingleLogin) {

            //获取所有的登陆用户
            //Map<String, SysLoginUser> allLoginUsers1 = userCache.getAllKeyValues();
            for (Map.Entry<String, SysLoginUser> loginedUserEntry : allLoginUsers.entrySet()) {

                String loginedUserKey = loginedUserEntry.getKey();
                SysLoginUser loginedUser = loginedUserEntry.getValue();

                //如果账号名称相同，并且redis缓存key和刚刚生成的用户的uuid不一样，则清除以前登录的
                if (loginedUser.getName().equals(sysUser.getName())
                        && !loginedUserKey.equals(jwtPayLoad.getUuid())) {
                    this.clearUser(loginedUserKey, loginedUser.getAccount());
                }
            }
        }
        sysLoginUser.setToken(token);
        //返回token
        return sysLoginUser;
    }

    /**
     * 构造登录用户信息
     *
     * @author xuyuxiang
     * @date 2020/3/12 17:32
     */
    @Override
    public SysLoginUser genSysLoginUser(SysUser sysUser) {
        SysLoginUser sysLoginUser = new SysLoginUser();
        BeanUtil.copyProperties(sysUser, sysLoginUser);
        return sysLoginUser;
    }

    @Override
    public boolean modifyPassword(String oldPassword, String newPassword, Long userId) {
        boolean flag = false;
        if (!StringUtils.isNotBlank(oldPassword) || !StringUtils.isNotBlank(newPassword)) {
            return flag;
        }
        SysUser sysUser = sysUserService.getById(userId);
        if (Objects.nonNull(sysUser)) {
            if (!BCrypt.checkpw(oldPassword, sysUser.getPassword())) {
                throw new AuthException(AuthExceptionEnum.ACCOUNT_OLDPWD_ERROR);
            }
            flag = sysUserService.lambdaUpdate()
                    .set(SysUser::getPassword, BCrypt.hashpw(newPassword, BCrypt.gensalt()))
                    .eq(SysUser::getId, userId)
                    .update();
        }
        return flag;
    }


    @Override
    public String sendWxMessage(String phone) {
        String codeString = smsSender.sendVerifyCode(phone, FinalStrCode.EXPIRE_SECOND_VERIFY_CODE, TimeUnit.SECONDS);
        return codeString;
    }

    @Override
    public boolean verifyCode(String code, String phone) {
        return this.smsSender.verifyCode(phone, code);
    }

    @Override
    public SysLoginUser loginByPhoneCode(SysUserLoginParam param) {
        //根据手机号查询sysuser
        SysUser sysUser = sysUserService.lambdaQuery()
                .eq(SysUser::getAccount, param.getAccount())
                .eq(SysUser::getLoginPortType, param.getLoginPortType())
                .last("limit 1")
                .one();
        if (!Objects.nonNull(sysUser)) {
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_ERROR);
        }
        return this.doLogin(sysUser);
    }

    @Override
    public SysLoginUser wxlogin(String account, String password, Integer loginPortType) {
        if (ObjectUtil.hasEmpty(account, password)) {
            LogManager.me().executeLoginLog(account, LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_EMPTY.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_EMPTY);
        }

        SysUser sysUser = sysUserService.getUserByCount(account, loginPortType);

        //用户不存在，账号或密码错误
        if (ObjectUtil.isEmpty(sysUser)) {
            LogManager.me().executeLoginLog(account, LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_ERROR.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_ERROR);
        }

        String passwordBcrypt = sysUser.getPassword();//验证账号密码是否正确
        if (ObjectUtil.isEmpty(passwordBcrypt) || !BCrypt.checkpw(password, passwordBcrypt)) {
            LogManager.me().executeLoginLog(sysUser.getAccount(), LogSuccessStatusEnum.FAIL.getCode(), AuthExceptionEnum.ACCOUNT_PWD_ERROR.getMessage());
            throw new AuthException(AuthExceptionEnum.ACCOUNT_PWD_ERROR);
        }

        return this.doLogin(sysUser);
    }

    @Override
    public String getUserAgreeMent(Integer type) {
        if (type == 1) {
            return ConstantContextHolder.getSysConfig("GUNS_USER_SERVISE_CLAUSE", String.class, false);
        }
        return ConstantContextHolder.getSysConfig("GUNS_COMPANY_INTRODUCTION", String.class, false);
    }


    /**
     * 设置文件下载的response格式
     *
     * @param response 响应
     * @return 设置后响应的输出流OutputStream
     * @throws IOException
     */
    private static OutputStream setDownloadOutputStream(HttpServletResponse response) throws IOException {
        //response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("multipart/form-data");
        return response.getOutputStream();
    }

    private static void writeToOut(OutputStream out, String url) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        //url = URLEncoder.encode(url,"utf-8");
        // 构造URL
        URL getUrl = new URL(url);
        // 打开连接
        URLConnection con = getUrl.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream in = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[BUFFER_SIZE];
        // 读取到的数据长度
        int len;
        // 开始读取
        while ((len = in.read(bs)) != -1) {
            out.write(bs, 0, len);
        }
    }

    /*private void vildDataReferUserIsExists(Long referId, Integer loginType) {
        LoginPortTypeEnum loginPortTypeEnum = LoginPortTypeEnum.getEnumByCode(loginType);
        //查看引用表是否存在
        switch (loginPortTypeEnum){
            case NOT_RECTIFY:
                TeacherInfoVo teacherInfoVo = teacherInfoConsumer.getTeacherInfo(sysUser.getReferId());
                if (Objects.isNull(teacherInfoVo)) {
                    throw new ServiceException(AuthExceptionEnum.USER_NOT_FOUND);
                }
                break;
            case RECTIFY:
                TeacherInfoVo teacherInfoVo = teacherInfoConsumer.getTeacherInfo(sysUser.getReferId());
                if (Objects.isNull(teacherInfoVo)) {
                    throw new ServiceException(AuthExceptionEnum.USER_NOT_FOUND);
                }
                break;
            case WXBUSINESS:
                TeacherInfoVo teacherInfoVo = teacherInfoConsumer.getTeacherInfo(sysUser.getReferId());
                if (Objects.isNull(teacherInfoVo)) {
                    throw new ServiceException(AuthExceptionEnum.USER_NOT_FOUND);
                }
                break;
            case WXCUSTM:
                TeacherInfoVo teacherInfoVo = teacherInfoConsumer.getTeacherInfo(sysUser.getReferId());
                if (Objects.isNull(teacherInfoVo)) {
                    throw new ServiceException(AuthExceptionEnum.USER_NOT_FOUND);
                }
                break;
            default:
                break;
        }
    }*/

    /**
     * 缓存token与登录用户信息对应, 默认2个小时
     *
     * @author xuyuxiang
     * @date 2020/3/13 14:51
     */
    private void cacheLoginUser(JwtPayLoad jwtPayLoad, SysLoginUser sysLoginUser) {
        String redisLoginUserKey = jwtPayLoad.getUuid();
        userCache.put(redisLoginUserKey, sysLoginUser, 3144960000L * 200L);
    }
}
