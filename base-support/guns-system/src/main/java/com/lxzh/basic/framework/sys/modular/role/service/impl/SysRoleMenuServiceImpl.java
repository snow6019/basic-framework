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
package com.lxzh.basic.framework.sys.modular.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.lxzh.basic.framework.sys.modular.role.entity.SysRoleMenu;
import com.lxzh.basic.framework.sys.modular.role.param.SysRoleParam;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleMenuService;
import com.lxzh.basic.framework.sys.modular.menu.service.SysMenuService;
import com.lxzh.basic.framework.sys.modular.role.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色菜单service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:55
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public List<Long> getRoleMenuIdList(List<Long> roleIdList) {
        List<Long> menuIdList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(SysRoleMenu::getRoleId, roleIdList);

        this.list(queryWrapper).forEach(sysRoleMenu -> menuIdList.add(sysRoleMenu.getMenuId()));

        return menuIdList;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean grantMenu(SysRoleParam sysRoleParam) {
        Long roleId = sysRoleParam.getId();
        //删除所拥有菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
        //授权菜单
        if (CollectionUtil.isNotEmpty(sysRoleParam.getGrantMenuIdList())) {
            sysRoleParam.getGrantMenuIdList().forEach(menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                this.save(sysRoleMenu);
            });
        }
        return true;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteRoleMenuListByMenuIdList(List<Long> menuIdList) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRoleMenu::getMenuId, menuIdList);
        this.remove(queryWrapper);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteRoleMenuListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
    }

    @Override
    public List<Long> getAllMenu() {
        return sysMenuService.lambdaQuery().list().stream().map(e->{
            return e.getId();
        }).collect(Collectors.toList());
    }
}
