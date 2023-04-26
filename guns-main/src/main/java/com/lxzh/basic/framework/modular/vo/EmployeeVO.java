package com.lxzh.basic.framework.modular.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 员工vo
 * </p>
 *
 * @author baiwandong
 * @since 2022-03-02
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel
public class EmployeeVO implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("登录名")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("角色id")
    private Long roleId;

    @ApiModelProperty("角色名称")
    private String roleStr;

    @ApiModelProperty("状态(0-正常,1-已禁用")
    private Integer status;

    @ApiModelProperty("1-超级管理员 2-普通用户")
    private Integer adminType;

    @ApiModelProperty("true-是,false-否")
    private Boolean isSuperAdmin;

    @ApiModelProperty("创建日期")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
