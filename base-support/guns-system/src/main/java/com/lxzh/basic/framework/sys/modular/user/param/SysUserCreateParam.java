package com.lxzh.basic.framework.sys.modular.user.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 系统用户参数
 *
 * @author xuyuxiang
 * @date 2020/3/23 9:21
 */
@Data
@ApiModel
public class SysUserCreateParam {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账号")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("员工姓名")
    @NotBlank(message = "员工姓名不能为空")
    private String name;

    @ApiModelProperty("手机号码")
    @Size(min = 11, max = 11, message = "手机号码格式错误")
    private String phone;

    @ApiModelProperty("角色id")
    @NotNull(message = "角色不能为空")
    private Long roleId;

    @ApiModelProperty("部门id")
    @NotNull(message = "部门不能为空")
    private Long orgId;
}
