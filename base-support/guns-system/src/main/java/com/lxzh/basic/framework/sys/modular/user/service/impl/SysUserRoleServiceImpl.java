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
package com.lxzh.basic.framework.sys.modular.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUserRole;
import com.lxzh.basic.framework.sys.modular.user.mapper.SysUserRoleMapper;
import com.lxzh.basic.framework.sys.modular.user.param.SysUserParam;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserRoleService;
import com.lxzh.basic.framework.sys.modular.role.entity.SysRole;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统用户角色service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:48
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public List<Long> getUserRoleIdList(Long userId) {
        List<Long> roleIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.list(queryWrapper).forEach(sysUserRole -> roleIdList.add(sysUserRole.getRoleId()));
        return roleIdList;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean grantRole(SysUserParam sysUserParam) {
        Long userId = sysUserParam.getId();
        //删除所拥有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);
        //授权角色
        if (CollectionUtil.isNotEmpty(sysUserParam.getGrantRoleIdList())) {
            sysUserParam.getGrantRoleIdList().forEach(roleId -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                this.save(sysUserRole);
            });
        }
        return true;
    }

    @Override
    public List<Long> getUserRoleDataScopeIdList(Long userId, Long orgId) {
        List<Long> roleIdList = CollectionUtil.newArrayList();

        // 获取用户所有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.list(queryWrapper).forEach(sysUserRole -> roleIdList.add(sysUserRole.getRoleId()));

        // 获取这些角色对应的数据范围
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            return sysRoleService.getUserDataScopeIdList(roleIdList, orgId);
        }
        return CollectionUtil.newArrayList();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteUserRoleListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getRoleId, roleId);
        this.remove(queryWrapper);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteUserRoleListByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);
    }

    @Override
    public SysRole getRoleByUserId(Long userId) {
        List<SysUserRole> sysUserRoles = this.lambdaQuery()
                .eq(SysUserRole::getUserId,userId)
                .list();
        if(CollectionUtils.isNotEmpty(sysUserRoles)){
            SysUserRole sysUserRole = sysUserRoles.get(0);
            if(Objects.nonNull(sysUserRole)){
                SysRole sysRole = sysRoleService.getById(sysUserRole.getRoleId());
                if(Objects.nonNull(sysRole)){
                    return sysRole;
                }
            }
        }
        return null;
    }

    @Override
    public Map<Long, SysRole> getRoleMapsByUserIds(Set<Long> collect) {
        Map<Long, SysRole> maps = new HashMap<>();
        if(CollectionUtils.isEmpty(collect)){
            return maps;
        }
        List<SysUserRole> sysUserRoles = this.lambdaQuery()
                .in(SysUserRole::getUserId,collect)
                .list();
        if(CollectionUtils.isNotEmpty(sysUserRoles)){
            Set<Long> roleIds = sysUserRoles.stream().map(e->{return e.getRoleId();}).collect(Collectors.toSet());
            if(CollectionUtils.isNotEmpty(roleIds)){
                Map<Long,SysRole> roleMaps = sysRoleService.lambdaQuery().in(SysRole::getId,roleIds).list().stream().collect(Collectors.toMap(SysRole::getId, Function.identity()));;
                if(Objects.nonNull(roleMaps)){
                    for (SysUserRole sysUserRole : sysUserRoles) {
                        SysRole sysRole = roleMaps.get(sysUserRole.getRoleId());
                        if(Objects.nonNull(sysRole)){
                            maps.put(sysUserRole.getUserId(),sysRole);
                        }
                    }
                }
            }
        }
        return maps;
    }
}
