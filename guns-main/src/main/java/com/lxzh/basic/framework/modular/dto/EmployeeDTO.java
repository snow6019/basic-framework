package com.lxzh.basic.framework.modular.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 会员卡DTO
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
public class EmployeeDTO implements Serializable {

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("登录账号")
    private String account;

    @ApiModelProperty("角色id")
    private Long roleId;

    @ApiModelProperty("状态(0-正常,1-已禁用")
    private Integer status;
}
