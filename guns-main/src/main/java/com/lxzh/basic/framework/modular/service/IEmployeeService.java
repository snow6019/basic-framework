package com.lxzh.basic.framework.modular.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxzh.basic.framework.modular.dto.EmployeeDTO;
import com.lxzh.basic.framework.modular.vo.EmployeeVO;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;


/**
 * <p>
 * 员工信息(用户)信息表 服务类
 * </p>
 *
 * @author baiwandong
 * @since 2022-03-02
 */
public interface IEmployeeService {

    /**
     * 用户管理列表
     * @param page
     * @param employeeDTO
     * @return
     */
    PageResult<EmployeeVO> employeePage(Page<SysUser> page, EmployeeDTO employeeDTO);

    /**
     * 新增用户
     * @param employeeVO
     * @return
     */
    boolean addEmployee(EmployeeVO employeeVO);

    /**
     * 编辑用户
     * @param employeeVO
     * @return
     */
    boolean editEmployee(EmployeeVO employeeVO);

    /**
     * 启用/停用
     * @param id
     * @param status
     * @return
     */
    boolean startOrStopUse(Long id, Integer status);

    /**
     * 删除用户
     * @param id
     * @return
     */
    boolean deleteEmployee(Long id);

    /**
     * 用户详情
     * @param id
     * @return
     */
    EmployeeVO showInfoVO(Long id);

    /**
     * 重置密码
     * @param id
     * @return
     */
    boolean resetPassword(Long id);

    /**
     * 联系我们
     * @return
     */
    String contactUs();
}
