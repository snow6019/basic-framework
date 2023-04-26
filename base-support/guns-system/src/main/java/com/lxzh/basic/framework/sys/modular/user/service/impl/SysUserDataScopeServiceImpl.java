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
import com.lxzh.basic.framework.sys.modular.user.entity.SysUserDataScope;
import com.lxzh.basic.framework.sys.modular.user.mapper.SysUserDataScopeMapper;
import com.lxzh.basic.framework.sys.modular.user.param.SysUserParam;
import com.lxzh.basic.framework.sys.modular.org.entity.SysOrg;
import com.lxzh.basic.framework.sys.modular.org.service.SysOrgService;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserDataScopeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Objects;

/**
 * 系统用户数据范围service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:49
 */
@Service
public class SysUserDataScopeServiceImpl extends ServiceImpl<SysUserDataScopeMapper, SysUserDataScope>
        implements SysUserDataScopeService {

    @Autowired
    private SysOrgService sysOrgService;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean grantData(SysUserParam sysUserParam) {
        Long userId = sysUserParam.getId();
        //删除所拥有数据
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDataScope::getUserId, userId);
        this.remove(queryWrapper);
        //授权数据
        sysUserParam.getGrantOrgIdList().forEach(orgId -> {
            SysUserDataScope sysUserDataScope = new SysUserDataScope();
            sysUserDataScope.setUserId(userId);
            sysUserDataScope.setOrgId(orgId);
            this.save(sysUserDataScope);
        });
        return true;
    }

    @Override
    public List<Long> getUserDataScopeIdList(Long uerId) {
        List<Long> userDataScopeIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDataScope::getUserId, uerId);
        this.list(queryWrapper).forEach(sysUserDataScope -> userDataScopeIdList.add(sysUserDataScope.getOrgId()));
        return userDataScopeIdList;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteUserDataScopeListByOrgIdList(List<Long> orgIdList) {
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUserDataScope::getOrgId, orgIdList);
        this.remove(queryWrapper);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteUserDataScopeListByUserId(Long userId) {
        LambdaQueryWrapper<SysUserDataScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDataScope::getUserId, userId);
        this.remove(queryWrapper);
    }

    @Override
    public SysOrg getOrgByUserId(Long userId) {
        List<SysUserDataScope> sysUserDataScopes = this.lambdaQuery()
                .eq(SysUserDataScope::getUserId,userId)
                .list();
        if(!CollectionUtils.isEmpty(sysUserDataScopes)){
            SysUserDataScope sysUserDataScope = sysUserDataScopes.get(0);
            if(Objects.nonNull(sysUserDataScope)){
                SysOrg sysOrg = sysOrgService.getById(sysUserDataScope.getOrgId());
                if(Objects.nonNull(sysOrg)){
                    return sysOrg;
                }
            }
        }
        return null;
    }
}
