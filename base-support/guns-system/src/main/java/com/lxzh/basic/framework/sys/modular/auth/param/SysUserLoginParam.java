package com.lxzh.basic.framework.sys.modular.auth.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统用户登录请求参数
 *
 * @author xuyuxiang
 * @date 2020/3/23 9:21
 */
@Data
@ApiModel
public class SysUserLoginParam {

    @ApiModelProperty("账号(包括手机号)")
    private String account;

    @ApiModelProperty("登录端口类型1-后台管理,2-客户微信端")
    private Integer loginPortType;

    @ApiModelProperty("多租户编码")
    private String tenantCode;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("openId")
    private String openId;

    @ApiModelProperty("姓名")
    private String nickname;

    @ApiModelProperty("手机验证码")
    private String phoneCode;

    @ApiModelProperty("头像")
    private String headImgUrl;

    @ApiModelProperty("推荐码")
    private String recommendCode;

    @ApiModelProperty("标识")
    private String uuid;

    @ApiModelProperty("验证码")
    private String code;
}
