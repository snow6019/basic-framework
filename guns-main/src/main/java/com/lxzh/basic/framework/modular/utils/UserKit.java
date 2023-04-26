package com.lxzh.basic.framework.modular.utils;

import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import com.lxzh.basic.framework.sys.modular.auth.context.LoginContextSpringSecurityImpl;

/**
 * @author baiwandong
 * @description: TODO
 * @date 2022/03/01下午2:16
 */

public class UserKit {

    private static final SysLoginUser DUMMY_USER;



    static {
        DUMMY_USER = new SysLoginUser();
    }


    /**
     * 获取登陆用户
     * @return
     */
    public static SysLoginUser getLoginUser(){
        try {
            return LoginContextSpringSecurityImpl.me().getSysLoginUser();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取referId
     * @return
     */
    public static Long getReferId() {
        SysLoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getReferId() : null;
    }

    /**
     * 获取用户Id
     * @return
     */
    public static Long getLoginUserId() {
        SysLoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * 获取用户openId
     * @return
     */
    public static String getLoginUserOpenId() {
        SysLoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getOpenId() : null;
    }

    /**
     * 获取referId
     * @return
     */
    public static Long getUserId() {
        SysLoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }
}
