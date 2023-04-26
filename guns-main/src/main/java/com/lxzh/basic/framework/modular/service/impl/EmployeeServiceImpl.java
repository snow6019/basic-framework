package com.lxzh.basic.framework.modular.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxzh.basic.framework.core.context.constant.ConstantContextHolder;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.modular.dto.EmployeeDTO;
import com.lxzh.basic.framework.modular.enums.ServiceExceptionEnum;
import com.lxzh.basic.framework.modular.service.IEmployeeService;
import com.lxzh.basic.framework.modular.utils.FinalStrCode;
import com.lxzh.basic.framework.modular.vo.EmployeeVO;
import com.lxzh.basic.framework.sys.core.enums.AdminTypeEnum;
import com.lxzh.basic.framework.sys.modular.auth.eunms.LoginPortTypeEnum;
import com.lxzh.basic.framework.sys.modular.role.entity.SysRole;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleService;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUserRole;
import com.lxzh.basic.framework.sys.modular.user.param.SysUserParam;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserRoleService;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工信息(用户)信息表 服务实现类
 * </p>
 *
 * @author baiwandong
 * @since 2022-03-02
 */
@Transactional(rollbackFor = RuntimeException.class)
@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public PageResult<EmployeeVO> employeePage(Page<SysUser> page, EmployeeDTO employeeDTO) {
        page = sysUserService.lambdaQuery()
                .eq(employeeDTO.getStatus() != null, SysUser::getStatus, employeeDTO.getStatus())
                .eq(employeeDTO.getRoleId() != null, SysUser::getRoleId, employeeDTO.getRoleId())
                .like(StringUtils.isNotBlank(employeeDTO.getName()), SysUser::getName, employeeDTO.getName())
                .like(StringUtils.isNotBlank(employeeDTO.getAccount()), SysUser::getAccount, employeeDTO.getAccount())
                .eq(SysUser::getLoginPortType, LoginPortTypeEnum.NOT_RECTIFY.getCode())
                .orderByDesc(SysUser::getCreateTime)
                .page(page);
        List<SysUser> sysUserList = page.getRecords();
        Map<Long, SysRole> roleMaps = sysUserRoleService.getRoleMapsByUserIds(sysUserList.stream().map(e -> {
            return e.getId();
        }).collect(Collectors.toSet()));
        IPage<EmployeeVO> modelIPage = page.convert(e -> {
            EmployeeVO employeeVO = new EmployeeVO();
            employeeVO.setId(e.getId());
            employeeVO.setName(e.getName());
            employeeVO.setAccount(e.getAccount());
            employeeVO.setIsSuperAdmin((e.getAdminType() != null && e.getAdminType() == 1) ? true : false);
            employeeVO.setStatus(e.getStatus());
            employeeVO.setCreateTime(e.getCreateTime());
            employeeVO.setAdminType(e.getAdminType());
            //设置校色信息
            SysRole sysRole = roleMaps.get(e.getId());
            if (Objects.nonNull(sysRole)) {
                employeeVO.setRoleId(sysRole.getId());
                employeeVO.setRoleStr(sysRole.getName());
            }
            return employeeVO;
        });
        return new PageResult<>((Page) modelIPage);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addEmployee(EmployeeVO employeeVO) {
        //校验员工账号是否存在
        vilddataAccount(employeeVO.getId(), employeeVO.getAccount());
        Integer sex = null;
        boolean flag = false;
        //先插入sysUser表
        SysUser sysUser = saveSysUser(employeeVO);
        return flag;
    }

    private void vilddataAccount(Long id, String account) {
        int count = 0;
        if (Objects.nonNull(id)) {
            count = sysUserService.lambdaQuery()
                    .eq(SysUser::getAccount, account)
                    .ne(SysUser::getId, id)
                    .count();
        } else {
            count = sysUserService.lambdaQuery()
                    .eq(SysUser::getAccount, account)
                    .count();
        }

        if (count > 0) {
            throw new ServiceException(ServiceExceptionEnum.EMPLOYEE_ACCOUNT_EXISTS);
        }
    }

    /**
     * 插入sysUser
     *
     * @param employeeVO
     * @return
     */
    private SysUser saveSysUser(EmployeeVO employeeVO) {
        SysRole sysRole = sysRoleService.getById(employeeVO.getRoleId());
        //插入sysuser
        SysUser sysUser = new SysUser();
        if (Objects.nonNull(employeeVO.getId())) {
            SysUser oldSysUser = sysUserService.getById(employeeVO.getId());
            if (Objects.nonNull(oldSysUser)) {
                sysUser = oldSysUser;
            }
        }
        sysUser.setAccount(employeeVO.getAccount());
        sysUser.setEmail("");
        sysUser.setName(employeeVO.getName());
        sysUser.setNickName(employeeVO.getName());
        sysUser.setRoleId(employeeVO.getRoleId());
        if (!StringUtils.isNotBlank(sysUser.getAvatar())) {
            sysUser.setAvatar(FinalStrCode.SYSHEADIMGURL);
        }
        sysUser.setAdminType(AdminTypeEnum.NONE.getCode());
        sysUser.setPhone(employeeVO.getPhone());
        sysUser.setTel("");
        if (!StringUtils.isNotBlank(sysUser.getPassword())) {//如果密码为空，默认123456
            sysUser.setPassword(BCrypt.hashpw(FinalStrCode.SYSPASSWORD, BCrypt.gensalt()));
        }
        if (Objects.nonNull(sysRole)) {
            sysUser.setLoginPortType(sysRole.getLoginPortType());
        }
        if (Objects.nonNull(employeeVO.getId())) {
            sysUserService.updateById(sysUser);
            //删除关联角色
            sysUserRoleService.lambdaUpdate()
                    .eq(SysUserRole::getUserId, sysUser.getId())
                    .remove();
        } else {
            sysUserService.save(sysUser);
        }
        //关联角色
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getId());
        sysUserRole.setRoleId(employeeVO.getRoleId());
        sysUserRoleService.save(sysUserRole);
        return sysUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editEmployee(EmployeeVO employeeVO) {
        boolean flag = false;
        SysUser sysUser1 = sysUserService.getById(employeeVO.getId());
        //校验员工账号是否存在
        vilddataAccount(employeeVO.getId(), employeeVO.getAccount());
        if (Objects.nonNull(sysUser1)) {
            employeeVO.setId(sysUser1.getId());
            //先插入sysUser表
            SysUser sysUser = saveSysUser(employeeVO);
        }
        return flag;
    }

    @Override
    public boolean startOrStopUse(Long id, Integer status) {
        boolean flag = false;
        SysUser sysUser = sysUserService.getById(id);
        if (Objects.nonNull(sysUser)) {
            SysUserParam sysUserParam = new SysUserParam();
            sysUserParam.setId(id);
            sysUserParam.setStatus(status);
            flag = sysUserService.changeStatus(sysUserParam);
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEmployee(Long id) {
        boolean flag = false;
        SysUser sysUser = sysUserService.getById(id);
        if (Objects.nonNull(sysUser)) {
            SysUserParam sysUserParam = new SysUserParam();
            sysUserParam.setId(id);
            sysUserService.delete(sysUserParam);
        }
        return flag;
    }

    @Override
    public EmployeeVO showInfoVO(Long id) {
        EmployeeVO employeeVO = new EmployeeVO();
        SysUser sysUser = sysUserService.getById(id);
        if (Objects.nonNull(sysUser)) {
            SysRole sysRole = sysUserRoleService.getRoleByUserId(id);
            if (Objects.nonNull(sysRole)) {
                employeeVO.setRoleId(sysRole.getId());
                employeeVO.setRoleStr(sysRole.getName());
            }
            employeeVO.setId(sysUser.getId());
            employeeVO.setName(sysUser.getName());
            employeeVO.setPhone(sysUser.getPhone());
            employeeVO.setAccount(sysUser.getAccount());
            employeeVO.setAdminType(sysUser.getAdminType());
            employeeVO.setIsSuperAdmin((sysUser.getAdminType() != null && sysUser.getAdminType() == 1) ? true : false);
            employeeVO.setStatus(sysUser.getStatus());
        }
        return employeeVO;
    }

    @Override
    public boolean resetPassword(Long id) {
        boolean flag = false;
        SysUser sysUser = sysUserService.getById(id);
        if (Objects.nonNull(sysUser)) {
            flag = sysUserService.lambdaUpdate()
                    .set(SysUser::getPassword, BCrypt.hashpw(FinalStrCode.SYSPASSWORD, BCrypt.gensalt()))
                    .eq(SysUser::getId, sysUser.getId())
                    .update();
        }
        return flag;
    }

    @Override
    public String contactUs() {
        String configStr = ConstantContextHolder.getSysConfig("GUNS_CONTACT_US", String.class, false);
        return configStr;
    }
}
