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
package com.lxzh.basic.framework.core.pojo.login;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.pojo.node.LoginMenuTreeNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 登录用户模型
 *
 * @author xuyuxiang
 * @date 2020/3/11 12:21
 */
@Data
public class SysLoginUser implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String account;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日")
    private Date birthday;

    /**
     * 性别(字典 1男 2女)
     */
    @ApiModelProperty(value = "性别(字典 1男 2女)")
    private Integer sex;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 手机
     */
    @ApiModelProperty(value = "phone")
    private String phone;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String tel;

    /**
     * 管理员类型（0超级管理员 1非管理员）
     */
    private Integer adminType;

    /**
     * 登录端口类型1-后台管理,2-门店终端,3-员工微信端,4-客户微信端
     */
    @ApiModelProperty(value = "登录端口类型1-后台管理,2-客户微信端")
    private Integer loginPortType;

    /**
     * 引用外部id
     */
    private Long referId;

    /**
     * 最后登陆IP
     */
    private String lastLoginIp;

    /**
     * 最后登陆时间
     */
    private String lastLoginTime;

    /**
     * 最后登陆地址
     */
    private String lastLoginAddress;

    /**
     * 最后登陆所用浏览器
     */
    private String lastLoginBrowser;

    /**
     * 最后登陆所用系统
     */
    private String lastLoginOs;

    /**
     * 员工信息
     */
    private LoginEmpInfo loginEmpInfo;

    /**
     * openid
     */
    private String openId;

    /**
     * 角色类型
     */
    @ApiModelProperty(value = "角色类型")
    private Integer roleType;

    /**
     * 具备应用信息
     */
    private List<Dict> apps;

    /**
     * 角色信息
     */
    private List<Dict> roles;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    /**
     * 权限信息
     */
    private List<String> permissions;

    /**
     * 权限ids
     */
    @ApiModelProperty(value = "菜单权限ids")
    private List<Long> permissionIds;

    /**
     * 登录菜单信息，AntDesign版本菜单
     */
    @ApiModelProperty(value = "菜单权限")
    private List<LoginMenuTreeNode> menus;

    /**
     * 数据范围信息
     */
    private List<Long> dataScopes;

    @ApiModelProperty(value = "部门编号")
    private Long orgId;

    /**
     * 租户信息
     */
    private Dict tenants;

    private String token;

    /**
     * 角色名称集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GunsAuthority> grantedAuthorities = CollectionUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(roles)) {
            roles.forEach(dict -> {
                String roleName = dict.getStr(CommonConstant.NAME);
                GunsAuthority gunsAuthority = new GunsAuthority(roleName);
                grantedAuthorities.add(gunsAuthority);
            });
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.account;
    }

    @Override
    public boolean isAccountNonExpired() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }

    @Override
    public boolean isEnabled() {
        //能生成loginUser就是jwt解析成功，没锁定
        return true;
    }
}
