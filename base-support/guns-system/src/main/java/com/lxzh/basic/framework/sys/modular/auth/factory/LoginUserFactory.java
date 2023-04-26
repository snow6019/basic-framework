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
package com.lxzh.basic.framework.sys.modular.auth.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lxzh.basic.framework.sys.modular.menu.service.SysMenuService;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleMenuService;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleService;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserRoleService;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserService;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.context.constant.ConstantContextHolder;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.exception.enums.ServerExceptionEnum;
import com.lxzh.basic.framework.core.pojo.login.LoginEmpInfo;
import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import com.lxzh.basic.framework.core.tenant.consts.TenantConstants;
import com.lxzh.basic.framework.core.tenant.context.TenantCodeHolder;
import com.lxzh.basic.framework.core.tenant.context.TenantDbNameHolder;
import com.lxzh.basic.framework.core.util.HttpServletUtil;
import com.lxzh.basic.framework.core.util.IpAddressUtil;
import com.lxzh.basic.framework.core.util.UaUtil;
import com.lxzh.basic.framework.sys.modular.app.service.SysAppService;
import com.lxzh.basic.framework.sys.modular.emp.service.SysEmpService;
import org.apache.commons.collections4.CollectionUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录用户工厂类
 *
 * @author xuyuxiang
 * @date 2020/3/13 14:58
 */
public class LoginUserFactory {

    private static final SysUserService sysUserService = SpringUtil.getBean(SysUserService.class);

    private static final SysEmpService sysEmpService = SpringUtil.getBean(SysEmpService.class);

    private static final SysAppService sysAppService = SpringUtil.getBean(SysAppService.class);

    private static final SysMenuService sysMenuService = SpringUtil.getBean(SysMenuService.class);

    private static final SysRoleService sysRoleService = SpringUtil.getBean(SysRoleService.class);

    private static final SysUserRoleService sysUserRoleService = SpringUtil.getBean(SysUserRoleService.class);

    private static final SysRoleMenuService sysRoleMenuService = SpringUtil.getBean(SysRoleMenuService.class);


    /**
     * 填充登录用户相关信息
     *
     * @author xuyuxiang yubaoshan
     * @date 2020/3/13 15:01
     */
    public static void fillLoginUserInfo(SysLoginUser sysLoginUser) {
        HttpServletRequest request = HttpServletUtil.getRequest();
        if (ObjectUtil.isNotNull(request)) {
            sysLoginUser.setLastLoginIp(IpAddressUtil.getIp(request));
            sysLoginUser.setLastLoginTime(DateTime.now().toString());
            sysLoginUser.setLastLoginAddress(IpAddressUtil.getAddress(request));
            sysLoginUser.setLastLoginBrowser(UaUtil.getBrowser(request));
            sysLoginUser.setLastLoginOs(UaUtil.getOs(request));
            Long userId = sysLoginUser.getId();

            List<Long> menuIdList = new ArrayList<>();
            List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
            if(CollectionUtils.isNotEmpty(roleIdList)){
                menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
            }

            // 员工信息
            LoginEmpInfo loginEmpInfo = sysEmpService.getLoginEmpInfo(userId);
            sysLoginUser.setLoginEmpInfo(loginEmpInfo);

            // 角色信息
            List<Dict> roles = sysRoleService.getLoginRoles(userId);
            sysLoginUser.setRoles(roles);

            // 权限信息
            List<String> permissions = sysMenuService.getLoginPermissions(userId,roleIdList,menuIdList);
            sysLoginUser.setPermissions(permissions);

            // 数据范围信息
            List<Long> dataScopes = sysUserService.getUserDataScopeIdList(userId, loginEmpInfo.getOrgId());
            sysLoginUser.setDataScopes(dataScopes);

            // 具备应用信息（多系统，默认激活一个，可根据系统切换菜单）,返回的结果中第一个为激活的系统
            List<Dict> apps = sysAppService.getLoginApps(userId,null);
            sysLoginUser.setApps(apps);

            // 如果根本没有应用信息，则没有菜单信息
            if (ObjectUtil.isEmpty(apps)) {
                sysLoginUser.setMenus(CollectionUtil.newArrayList());
            } else {
                //AntDesign菜单信息，根据人获取，用于登录后展示菜单树，默认获取默认激活的系统的菜单
                String defaultActiveAppCode = apps.get(0).getStr(CommonConstant.CODE);
                sysLoginUser.setMenus(sysMenuService.getLoginMenusAntDesign(userId, defaultActiveAppCode,null));
                sysLoginUser.setPermissionIds(sysMenuService.getLoginPermissionIds(userId,null,roleIdList,menuIdList));
            }

            //如果开启了多租户功能，则设置当前登录用户的租户标识
            if (ConstantContextHolder.getTenantOpenFlag()) {
                String tenantCode = TenantCodeHolder.get();
                String dataBaseName = TenantDbNameHolder.get();
                if (StrUtil.isNotBlank(tenantCode) && StrUtil.isNotBlank(dataBaseName)) {
                    Dict tenantInfo = Dict.create();
                    tenantInfo.set(TenantConstants.TENANT_CODE, tenantCode);
                    tenantInfo.set(TenantConstants.TENANT_DB_NAME, dataBaseName);
                    sysLoginUser.setTenants(tenantInfo);
                }
                //注意，这里remove不代表所有情况，在aop remove
                TenantCodeHolder.remove();
                TenantDbNameHolder.remove();
            }

        } else {
            throw new ServiceException(ServerExceptionEnum.REQUEST_EMPTY);
        }
    }
}
