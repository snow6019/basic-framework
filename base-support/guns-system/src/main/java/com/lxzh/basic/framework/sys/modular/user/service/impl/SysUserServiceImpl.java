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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;
import com.lxzh.basic.framework.sys.modular.user.enums.SysUserExceptionEnum;
import com.lxzh.basic.framework.sys.modular.user.mapper.SysUserMapper;
import com.lxzh.basic.framework.sys.modular.user.param.SysUserParam;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.consts.SymbolConstant;
import com.lxzh.basic.framework.core.context.constant.ConstantContextHolder;
import com.lxzh.basic.framework.core.enums.CommonStatusEnum;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.exception.enums.AuthExceptionEnum;
import com.lxzh.basic.framework.core.exception.enums.StatusExceptionEnum;
import com.lxzh.basic.framework.core.factory.PageFactory;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.core.util.PoiUtil;
import com.lxzh.basic.framework.sys.core.enums.AdminTypeEnum;
import com.lxzh.basic.framework.sys.modular.emp.param.SysEmpParam;
import com.lxzh.basic.framework.sys.modular.emp.result.SysEmpInfo;
import com.lxzh.basic.framework.sys.modular.emp.service.SysEmpService;
import com.lxzh.basic.framework.sys.modular.file.entity.SysFileInfo;
import com.lxzh.basic.framework.sys.modular.file.param.SysFileInfoParam;
import com.lxzh.basic.framework.sys.modular.file.service.SysFileInfoService;
import com.lxzh.basic.framework.sys.modular.user.factory.SysUserFactory;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserDataScopeService;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserRoleService;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserService;
import com.lxzh.basic.framework.sys.modular.user.result.SysUserResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 系统用户service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/11 17:49
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysEmpService sysEmpService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysUserDataScopeService sysUserDataScopeService;

    @Resource
    private SysFileInfoService sysFileInfoService;

    @Override
    public SysUser getUserByCount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode());
        return this.getOne(queryWrapper);
    }

    @Override
    public SysUser getUserByCount(String account, Integer referType) {
        List<SysUser> sysUserList = this.lambdaQuery()
            .eq(SysUser::getAccount, account)
            .ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode())
            .eq(SysUser::getLoginPortType, referType).list();
        if(!CollectionUtils.isNotEmpty(sysUserList)){
            return null;
        }
        if(sysUserList.size()!=1){
            throw new ServiceException(AuthExceptionEnum.ACCOUNT_EXCIPTION);
        }
        return sysUserList.get(0);
    }

    @Override
    public PageResult<SysUserResult> page(SysUserParam sysUserParam) {
        QueryWrapper<SysUserResult> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(sysUserParam)) {
            //根据关键字模糊查询（姓名，账号，手机号）
            if (ObjectUtil.isNotEmpty(sysUserParam.getSearchValue())) {
                queryWrapper.and(q -> q.like("sys_user.account", sysUserParam.getSearchValue())
                        .or().like("sys_user.name", sysUserParam.getSearchValue())
                        .or().like("sys_user.phone", sysUserParam.getSearchValue()));
            }
            //根据员工所属机构查询
            if (ObjectUtil.isNotEmpty(sysUserParam.getSysEmpParam())) {
                SysEmpParam sysEmpParam = sysUserParam.getSysEmpParam();
                if (ObjectUtil.isNotEmpty(sysEmpParam.getOrgId())) {
                    //查询属于该机构的，或该机构下级所有的人员
                    queryWrapper.and(q -> q.eq("sys_emp.org_id", sysEmpParam.getOrgId())
                            .or().like("sys_org.pids", sysEmpParam.getOrgId()));
                }
            }
            //根据状态查询（0正常 1停用），删除的不会展示在列表
            if (ObjectUtil.isNotEmpty(sysUserParam.getSearchStatus())) {
                queryWrapper.eq("sys_user.status", sysUserParam.getSearchStatus());
            }
        }
        //查询非删除状态，排除超级管理员
        queryWrapper.ne("sys_user.status", CommonStatusEnum.DELETED.getCode())
                .ne("sys_user.admin_type", AdminTypeEnum.SUPER_ADMIN.getCode());
        return new PageResult<>(this.baseMapper.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<Dict> list(SysUserParam sysUserParam) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysUserParam)) {
            //根据账号，姓名模糊查询
            if (ObjectUtil.isNotEmpty(sysUserParam.getAccount())) {
                queryWrapper.and(i -> i.like(SysUser::getAccount, sysUserParam.getAccount())
                        .or().like(SysUser::getName, sysUserParam.getAccount()));
            }
        }
        //查询正常状态，排除超级管理员
        queryWrapper.eq(SysUser::getStatus, CommonStatusEnum.ENABLE.getCode())
                .ne(SysUser::getAdminType, AdminTypeEnum.SUPER_ADMIN.getCode());
        this.list(queryWrapper).forEach(sysUser -> {
            Dict dict = Dict.create();
            dict.put("id", sysUser.getId().toString());
            dict.put("firstName", sysUser.getName() + SymbolConstant.LEFT_SQUARE_BRACKETS
                    + sysUser.getAccount() + SymbolConstant.RIGHT_SQUARE_BRACKETS);
            dictList.add(dict);
        });
        return dictList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean add(SysUserParam sysUserParam) {
        checkParam(sysUserParam, false);
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(sysUserParam, sysUser);
        SysUserFactory.fillAddCommonUserInfo(sysUser);
        sysUser.setPassword(BCrypt.hashpw(sysUser.getPassword(), BCrypt.gensalt()));
        boolean flag = this.save(sysUser);
        Long sysUserId = sysUser.getId();
        //增加员工信息
        SysEmpParam sysEmpParam = sysUserParam.getSysEmpParam();
        sysEmpParam.setId(sysUserId);
        sysEmpService.addOrUpdate(sysEmpParam);
        //授权角色
        sysUserParam.setId(sysUserId);
        sysUserRoleService.grantRole(sysUserParam);
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean delete(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        //不能删除超级管理员
        if (AdminTypeEnum.SUPER_ADMIN.getCode().equals(sysUser.getAdminType())) {
            throw new ServiceException(SysUserExceptionEnum.USER_CAN_NOT_DELETE_ADMIN);
        }
        sysUser.setStatus(CommonStatusEnum.DELETED.getCode());
        boolean flag = this.updateById(sysUser);
        Long id = sysUser.getId();
        //删除该用户对应的员工表信息
        sysEmpService.deleteEmpInfoByUserId(id);

        //删除该用户对应的用户-角色表关联信息
        sysUserRoleService.deleteUserRoleListByUserId(id);

        //删除该用户对应的用户-数据范围表关联信息
        sysUserDataScopeService.deleteUserDataScopeListByUserId(id);
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean edit(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        checkParam(sysUserParam, true);

        BeanUtil.copyProperties(sysUserParam, sysUser);
        //不能修改状态，用修改状态接口修改状态
        sysUser.setStatus(null);
        //设置密码
        SysUserFactory.fillBaseUserInfo(sysUser);
        boolean flag = this.updateById(sysUser);
        Long sysUserId = sysUser.getId();
        //编辑员工信息
        SysEmpParam sysEmpParam = sysUserParam.getSysEmpParam();
        BeanUtil.copyProperties(sysUserParam, sysEmpParam);
        sysEmpParam.setId(sysUserId);
        sysEmpService.addOrUpdate(sysEmpParam);
        //授权角色
        sysUserRoleService.grantRole(sysUserParam);
        return flag;
    }

    @Override
    public SysUserResult detail(SysUserParam sysUserParam) {
        SysUserResult sysUserResult = new SysUserResult();
        //获取系统用户
        SysUser sysUser = this.querySysUser(sysUserParam);
        BeanUtil.copyProperties(sysUser, sysUserResult);
        //获取对应员工信息
        SysEmpInfo sysEmpInfo = sysEmpService.getSysEmpInfo(sysUser.getId());
        //设置员工信息
        sysUserResult.setSysEmpInfo(sysEmpInfo);
        List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(sysUser.getId());
        sysUserResult.setRoleIdList(roleIdList);
        return sysUserResult;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean changeStatus(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        //不能修改超级管理员状态
        if (AdminTypeEnum.SUPER_ADMIN.getCode().equals(sysUser.getAdminType())) {
            throw new ServiceException(SysUserExceptionEnum.USER_CAN_NOT_UPDATE_ADMIN);
        }

        Long id = sysUser.getId();

        Integer status = sysUserParam.getStatus();
        //校验状态在不在枚举值里
        CommonStatusEnum.validateStatus(status);

        //更新枚举，更新只能更新未删除状态的
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, id)
                .and(i -> i.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode()))
                .set(SysUser::getStatus, status);
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new ServiceException(StatusExceptionEnum.UPDATE_STATUS_ERROR);
        }
        return update;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean grantRole(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        return sysUserRoleService.grantRole(sysUserParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean grantData(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        return sysUserDataScopeService.grantData(sysUserParam);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean updateInfo(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        BeanUtil.copyProperties(sysUserParam, sysUser);
        return this.updateById(sysUser);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean updatePwd(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        //新密码与原密码相同
        if (sysUserParam.getNewPassword().equals(sysUserParam.getPassword())) {
            throw new ServiceException(SysUserExceptionEnum.USER_PWD_REPEAT);
        }
        //原密码错误
        if (!BCrypt.checkpw(sysUserParam.getPassword(), sysUser.getPassword())) {
            throw new ServiceException(SysUserExceptionEnum.USER_PWD_ERROR);
        }
        sysUser.setPassword(BCrypt.hashpw(sysUserParam.getNewPassword(), BCrypt.gensalt()));
        return this.updateById(sysUser);
    }

    @Override
    public List<Long> getUserDataScopeIdList(Long userId, Long orgId) {
        Set<Long> userDataScopeIdSet = CollectionUtil.newHashSet();
        if (ObjectUtil.isAllNotEmpty(userId, orgId)) {

            //获取该用户对应的数据范围集合
            List<Long> userDataScopeIdListForUser = sysUserDataScopeService.getUserDataScopeIdList(userId);

            //获取该用户的角色对应的数据范围集合
            List<Long> userDataScopeIdListForRole = sysUserRoleService.getUserRoleDataScopeIdList(userId, orgId);

            userDataScopeIdSet.addAll(userDataScopeIdListForUser);
            userDataScopeIdSet.addAll(userDataScopeIdListForRole);
        }
        return CollectionUtil.newArrayList(userDataScopeIdSet);
    }

    @Override
    public String getNameByUserId(Long userId) {
        SysUser sysUser = this.getById(userId);
        if (ObjectUtil.isNull(sysUser)) {
            throw new ServiceException(SysUserExceptionEnum.USER_NOT_EXIST);
        }
        return sysUser.getName();
    }

    @Override
    public List<Long> ownRole(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        return sysUserRoleService.getUserRoleIdList(sysUser.getId());
    }

    @Override
    public List<Long> ownData(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        return sysUserDataScopeService.getUserDataScopeIdList(sysUser.getId());
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean resetPwd(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        String password = sysUserParam.getPassword();
        if (StrUtil.isBlank(password)) {
            password = ConstantContextHolder.getDefaultPassWord();
        }
        sysUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        return this.updateById(sysUser);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean updateAvatar(SysUserParam sysUserParam) {
        SysUser sysUser = this.querySysUser(sysUserParam);
        Long avatar = sysUserParam.getAvatar();
        sysFileInfoService.assertFile(avatar);
        SysFileInfoParam sysFileInfoParam = new SysFileInfoParam();
        sysFileInfoParam.setId(avatar);
        SysFileInfo sysFileInfo = sysFileInfoService.detail(sysFileInfoParam);
        if(Objects.nonNull(sysFileInfo)){
            sysUser.setAvatar(sysFileInfo.getFilePath());
        }
        return this.updateById(sysUser);
    }

    @Override
    public void export(SysUserParam sysUserParam) {
        List<SysUser> list = this.list();
        PoiUtil.exportExcelWithStream("GunsUsers.xls", SysUser.class, list);
    }

    @Override
    public List<Dict> selector(SysUserParam sysUserParam) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysUserParam)) {
            //根据姓名模糊查询
            if (ObjectUtil.isNotEmpty(sysUserParam.getName())) {
                queryWrapper.like(SysUser::getName, sysUserParam.getName());
            }
        }
        //查询非删除状态，排除超级管理员
        queryWrapper.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode())
                .ne(SysUser::getAdminType, AdminTypeEnum.SUPER_ADMIN.getCode());
       this.list(queryWrapper).forEach(sysUser -> {
            Dict dict  = Dict.create();
            dict.put(CommonConstant.ID, sysUser.getId());
            dict.put(CommonConstant.NAME, sysUser.getName());
            dictList.add(dict);
       });
       return dictList;
    }

    @Override
    public SysUser getUserAvatar(Integer code, Long id) {
        return this.lambdaQuery()
                .eq(SysUser::getLoginPortType,code)
                .eq(SysUser::getReferId,id)
                .last("limit 1")
                .one();
    }

    @Override
    public SysUser getUserByOpenId(String openid, Integer loginType) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getOpenId, openid);
        queryWrapper.eq(SysUser::getLoginPortType, loginType);
        queryWrapper.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode());
        return this.getOne(queryWrapper);
    }

    @Override
    public SysUser getUserByAcount(String account, Integer loginPortType) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.eq(SysUser::getLoginPortType, loginPortType);
        queryWrapper.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode());
        return this.getOne(queryWrapper);
    }

    @Override
    public SysUser getUserByPhone(String phone, Integer loginType) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, phone);
        queryWrapper.eq(SysUser::getLoginPortType, loginType);
        queryWrapper.ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode());
        return this.getOne(queryWrapper);
    }

    /**
     * 校验参数，检查是否存在相同的账号
     *
     * @author xuyuxiang
     * @date 2020/3/27 16:04
     */
    private void checkParam(SysUserParam sysUserParam, boolean isExcludeSelf) {
        Long id = sysUserParam.getId();
        String account = sysUserParam.getAccount();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account)
                .ne(SysUser::getStatus, CommonStatusEnum.DELETED.getCode());
        //是否排除自己，如果是则查询条件排除自己id
        if (isExcludeSelf) {
            queryWrapper.ne(SysUser::getId, id);
        }
        int countByAccount = this.count(queryWrapper);
        //大于等于1个则表示重复
        if (countByAccount >= 1) {
            throw new ServiceException(SysUserExceptionEnum.USER_ACCOUNT_REPEAT);
        }
    }

    /**
     * 获取系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:54
     */
    private SysUser querySysUser(SysUserParam sysUserParam) {
        SysUser sysUser = this.getById(sysUserParam.getId());
        if (ObjectUtil.isNull(sysUser)) {
            throw new ServiceException(SysUserExceptionEnum.USER_NOT_EXIST);
        }
        return sysUser;
    }
}
