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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxzh.basic.framework.sys.modular.role.entity.SysRole;
import com.lxzh.basic.framework.sys.modular.role.enums.SysRoleExceptionEnum;
import com.lxzh.basic.framework.sys.modular.role.mapper.SysRoleMapper;
import com.lxzh.basic.framework.sys.modular.role.param.SysRoleParam;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleDataScopeService;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUserRole;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserRoleService;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.enums.CommonStatusEnum;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.factory.PageFactory;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.sys.core.enums.DataScopeTypeEnum;
import com.lxzh.basic.framework.sys.modular.org.service.SysOrgService;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleMenuService;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleService;
import com.lxzh.basic.framework.sys.modular.role.result.SysRoleResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 系统角色service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:55
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    @Resource
    private SysRoleDataScopeService sysRoleDataScopeService;

    @Resource
    private SysOrgService sysOrgService;

    @Override
    public List<Dict> getLoginRoles(Long userId) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        //获取用户角色id集合
        List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRole::getId, roleIdList).eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
            //根据角色id集合查询并返回结果
            this.list(queryWrapper).forEach(sysRole -> {
                Dict dict = Dict.create();
                dict.put(CommonConstant.ID, sysRole.getId());
                dict.put(CommonConstant.CODE, sysRole.getCode());
                dict.put(CommonConstant.NAME, sysRole.getName());
                dictList.add(dict);
            });
        }
        return dictList;
    }

    @Override
    public PageResult<SysRole> page(SysRoleParam sysRoleParam) {
//        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
//        if (ObjectUtil.isNotNull(sysRoleParam)) {
//            //根据名称模糊查询
//            if (ObjectUtil.isNotEmpty(sysRoleParam.getName())) {
//                queryWrapper.like(SysRole::getName, sysRoleParam.getName());
//            }
//            //根据编码模糊查询
//            if (ObjectUtil.isNotEmpty(sysRoleParam.getCode())) {
//                queryWrapper.like(SysRole::getCode, sysRoleParam.getCode());
//            }
//        }
//
//        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
//        //根据排序升序排列，序号越小越在前
//        queryWrapper.orderByAsc(SysRole::getSort);
        return new PageResult<>(baseMapper.findPage(PageFactory.defaultPage(), sysRoleParam));
    }

    @Override
    public List<Dict> list(SysRoleParam sysRoleParam) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysRoleParam)) {
            //根据角色名称或编码模糊查询
            if (ObjectUtil.isNotEmpty(sysRoleParam.getName())) {
                queryWrapper.and(i -> i.like(SysRole::getName, sysRoleParam.getName()));
            }
        }
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysRole::getSort);
        this.list(queryWrapper).forEach(sysRole -> {
            Dict dict = Dict.create();
            dict.put(CommonConstant.ID, sysRole.getId());
            dict.put(CommonConstant.NAME, sysRole.getName());
            dictList.add(dict);
        });
        return dictList;
    }

    @Override
    public List<Dict> dropDown() {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        queryWrapper.ne(SysRole::getCode,"customer_role");
        this.list(queryWrapper)
                .forEach(sysRole -> {
                    Dict dict = Dict.create();
                    dict.put(CommonConstant.ID, sysRole.getId());
                    dict.put(CommonConstant.CODE, sysRole.getCode());
                    dict.put(CommonConstant.NAME, sysRole.getName());
                    dict.put(CommonConstant.ISAGENCYNEED, sysRole.getLoginPortType()!=null && sysRole.getLoginPortType()==2 ? true : false);
                    dictList.add(dict);
                });
        return dictList;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean add(SysRoleParam sysRoleParam) {
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, false);
        SysRole sysRole = new SysRole();
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        sysRole.setCode("");
        sysRole.setStatus(CommonStatusEnum.ENABLE.getCode());
        boolean flag = this.save(sysRole);
        //授权菜单
        sysRoleParam.setId(sysRole.getId());
        sysRoleMenuService.grantMenu(sysRoleParam);
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean delete(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        sysRole.setStatus(CommonStatusEnum.DELETED.getCode());
        //校验是否关联了账号,如果关联了账号登录类型就不能修改
        if(!Objects.equals(sysRoleParam.getLoginPortType(),sysRole.getLoginPortType())){
            int count = sysUserRoleService.lambdaQuery()
                    .eq(SysUserRole::getRoleId,sysRole.getId())
                    .count();
            if(count>0){//如果角色下有用户就校验不能修改
                throw new ServiceException(SysRoleExceptionEnum.ROLE_HAS_USER);
            }
        }
        boolean flag = this.updateById(sysRole);
        Long id = sysRole.getId();
        //级联删除该角色对应的角色-数据范围关联信息
        sysRoleDataScopeService.deleteRoleDataScopeListByRoleId(id);

        //级联删除该角色对应的用户-角色表关联信息
        sysUserRoleService.deleteUserRoleListByRoleId(id);

        //级联删除该角色对应的角色-菜单表关联信息
        sysRoleMenuService.deleteRoleMenuListByRoleId(id);
        return flag;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean edit(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        //校验是否关联了账号,如果关联了账号登录类型就不能修改
        if(!Objects.equals(sysRoleParam.getLoginPortType(),sysRole.getLoginPortType())){
            int count = sysUserRoleService.lambdaQuery()
                    .eq(SysUserRole::getRoleId,sysRole.getId())
                    .count();
            if(count>0){//如果角色下有用户就校验不能修改
                throw new ServiceException(SysRoleExceptionEnum.ROLE_HAS_USER);
            }
        }
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, true);
        String code = sysRole.getCode();
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        if(StringUtils.isNotBlank(code)){
            sysRole.setCode(code);
        }else{
            sysRole.setCode("");
        }
        //不能修改状态，用修改状态接口修改状态
        sysRole.setStatus(null);
        boolean flag = this.updateById(sysRole);
        //授权菜单
        sysRoleMenuService.grantMenu(sysRoleParam);
        return flag;
    }

    @Override
    public SysRoleResult detail(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        int count = sysUserRoleService.lambdaQuery()
                .eq(SysUserRole::getRoleId,sysRole.getId())
                .count();
        SysRoleResult sysRoleResult = BeanUtil.toBean(sysRole, SysRoleResult.class);
        sysRoleResult.setUserCount(count);
        List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(CollectionUtil.newArrayList(sysRole.getId()));
        sysRoleResult.setGrantMenuIdList(menuIdList);
        return sysRoleResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean grantMenu(SysRoleParam sysRoleParam) {
        this.querySysRole(sysRoleParam);
        return sysRoleMenuService.grantMenu(sysRoleParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean grantData(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        sysRole.setDataScopeType(sysRoleParam.getDataScopeType());
        boolean flag = this.updateById(sysRole);
        sysRoleDataScopeService.grantDataScope(sysRoleParam);
        return flag;
    }

    @Override
    public List<Long> getUserDataScopeIdList(List<Long> roleIdList, Long orgId) {
        Set<Long> resultList = CollectionUtil.newHashSet();

        //定义角色中最大数据范围的类型，目前系统按最大范围策略来，如果你同时拥有ALL和SELF的权限，系统最后按ALL返回
        Integer strongerDataScopeType = DataScopeTypeEnum.SELF.getCode();

        //获取用户自定义数据范围的角色集合
        List<Long> customDataScopeRoleIdList = CollectionUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            List<SysRole> sysRoleList = this.listByIds(roleIdList);
            for (SysRole sysRole : sysRoleList) {
                if (DataScopeTypeEnum.DEFINE.getCode().equals(sysRole.getDataScopeType())) {
                    customDataScopeRoleIdList.add(sysRole.getId());
                } else {
                    if (sysRole.getDataScopeType() <= strongerDataScopeType) {
                        strongerDataScopeType = sysRole.getDataScopeType();
                    }
                }
            }
        }

        //自定义数据范围的角色对应的数据范围
        List<Long> roleDataScopeIdList = sysRoleDataScopeService.getRoleDataScopeIdList(customDataScopeRoleIdList);

        //角色中拥有最大数据范围类型的数据范围
        List<Long> dataScopeIdList = sysOrgService.getDataScopeListByDataScopeType(strongerDataScopeType, orgId);

        resultList.addAll(dataScopeIdList);
        resultList.addAll(roleDataScopeIdList);
        return CollectionUtil.newArrayList(resultList);
    }

    @Override
    public String getNameByRoleId(Long roleId) {
        SysRole sysRole = this.getById(roleId);
        if (ObjectUtil.isEmpty(sysRole)) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole.getName();
    }

    @Override
    public List<Long> ownMenu(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        return sysRoleMenuService.getRoleMenuIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    @Override
    public List<Long> ownData(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        return sysRoleDataScopeService.getRoleDataScopeIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    @Override
    public List<SysRole> getRoleList(String name) {
        return baseMapper.findList(name);
    }

    /**
     * 校验参数，检查是否存在相同的名称和编码
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    private void checkParam(SysRoleParam sysRoleParam, boolean isExcludeSelf) {
        Long id = sysRoleParam.getId();
        String name = sysRoleParam.getName();
        String code = sysRoleParam.getCode();

        LambdaQueryWrapper<SysRole> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysRole::getName, name)
                .ne(SysRole::getStatus, CommonStatusEnum.DELETED.getCode());

        //是否排除自己，如果排除自己则不查询自己的id
        if (isExcludeSelf) {
            queryWrapperByName.ne(SysRole::getId, id);
        }
        int countByName = this.count(queryWrapperByName);

        if (countByName >= 1) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NAME_REPEAT);
        }
    }

    /**
     * 获取系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    private SysRole querySysRole(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.getById(sysRoleParam.getId());
        if (ObjectUtil.isNull(sysRole)) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole;
    }
}
