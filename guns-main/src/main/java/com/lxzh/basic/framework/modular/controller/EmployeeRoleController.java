package com.lxzh.basic.framework.modular.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxzh.basic.framework.core.annotion.Permission;
import com.lxzh.basic.framework.core.factory.PageFactory;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.modular.dto.EmployeeDTO;
import com.lxzh.basic.framework.modular.service.IEmployeeService;
import com.lxzh.basic.framework.modular.utils.DCResponse;
import com.lxzh.basic.framework.modular.utils.UserKit;
import com.lxzh.basic.framework.modular.vo.EmployeeVO;
import com.lxzh.basic.framework.sys.modular.auth.service.AuthService;
import com.lxzh.basic.framework.sys.modular.role.entity.SysRole;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleService;
import com.lxzh.basic.framework.sys.modular.user.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 员工信息(用户)信息以及角色信息
 * </p>
 *
 * @author baiwandong
 * @since 2022-03-02
 */
@Api(tags = {"员工与角色管理"})
@RestController
@RequestMapping("/v1/employee-role")
public class EmployeeRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private IEmployeeService employeeService;
    @Resource
    private AuthService authService;

    @ApiOperation(value = "获取员工列表", notes = "获取员工列表")
    @GetMapping("/employeePage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(paramType = "状态", name = "status", value = "状态", dataType = "Integer")
    })
    public DCResponse<PageResult<EmployeeVO>> employeePage(EmployeeDTO employeeDTO) {
        Page<SysUser> page = PageFactory.defaultPage();
        return DCResponse.success(employeeService.employeePage(page, employeeDTO));
    }

    /**
     * 新增员工
     *
     * @return
     */
    @Permission
    @PostMapping("/addEmployee")
    @ApiOperation(value = "新增员工", notes = "新增员工")
    public DCResponse addEmployee(@RequestBody EmployeeVO employeeVO) {
        if (employeeService.addEmployee(employeeVO)) {
            return DCResponse.success("操作成功!");
        }
        return DCResponse.success("操作异常!");
    }

    /**
     * 编辑员工
     *
     * @return
     */
    @Permission
    @PostMapping("/editEmployee")
    @ApiOperation(value = "编辑员工", notes = "编辑员工")
    public DCResponse editEmployee(@RequestBody EmployeeVO employeeVO) {
        if (employeeService.editEmployee(employeeVO)) {
            return DCResponse.success("操作成功!");
        }
        return DCResponse.success("操作异常!");
    }

    /**
     * 启用/停用员工账号
     *
     * @param id
     * @return
     */
    @Permission
    @ApiOperation(value = "启用/停用员工账号", notes = "启用/停用员工账号")
    @GetMapping("/startOrStopUse")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "员工id", name = "id", value = "员工id", dataType = "Long"),
            @ApiImplicitParam(paramType = "status(状态0-正常,1-停用)", name = "status", value = "status(状态0-正常,1-停用)", dataType = "Integer")
    })
    public DCResponse startOrStopUse(@RequestParam(value = "id") Long id, @RequestParam(value = "status") Integer status) {
        if (employeeService.startOrStopUse(id, status)) {
            return DCResponse.success("操作成功");
        }
        return DCResponse.error(500, "操作失败！");
    }

    @Permission
    @ApiOperation(value = "员工明细")
    @ResponseBody
    @GetMapping("/info/{id}")
    public DCResponse<EmployeeVO> info(@PathVariable("id") Long id) {
        return DCResponse.success(employeeService.showInfoVO(id));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @Permission
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @DeleteMapping("/deleteOne/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "员工id", name = "id", value = "员工id", dataType = "Long")
    })
    public DCResponse deleteOne(@PathVariable("id") Long id) {
        if (employeeService.deleteEmployee(id)) {
            return DCResponse.success("删除成功");
        }
        return DCResponse.error(500, "删除失败！");
    }

    @ApiOperation(value = "获取角色列表", notes = "获取角色列表")
    @GetMapping("/getRoleList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "角色名称", name = "name", value = "角色名称", dataType = "String")
    })
    public DCResponse<List<SysRole>> getRoleList(@RequestParam(value = "name") String name) {
        return DCResponse.success(sysRoleService.getRoleList(name));
    }

    /**
     * 重置密码
     *
     * @param id
     * @return
     */
    @Permission
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @GetMapping("/resetPassword")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "员工id", name = "id", value = "员工id", dataType = "Long")
    })
    public DCResponse resetPassword(@RequestParam(value = "id") Long id) {
        if (employeeService.resetPassword(id)) {
            return DCResponse.success("操作成功");
        }
        return DCResponse.error(500, "操作失败！");
    }

    /**
     * 修改密码
     *
     * @return
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @GetMapping("/modifyPassword")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "旧密码", name = "oldPassword", value = "旧密码", dataType = "Long"),
            @ApiImplicitParam(paramType = "新密码", name = "newPassword", value = "新密码", dataType = "String")
    })
    public DCResponse modifyPassword(@RequestParam(value = "oldPassword") String oldPassword, @RequestParam(value = "newPassword") String newPassword) {
        Long userId = UserKit.getLoginUser().getId();
        if (authService.modifyPassword(oldPassword, newPassword, userId)) {
            return DCResponse.success("操作成功");
        }
        return DCResponse.error(500, "操作失败！");
    }
}
