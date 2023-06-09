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
package com.lxzh.basic.framework.sys.modular.user.result;

import com.lxzh.basic.framework.sys.modular.emp.result.SysEmpInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 系统用户结果
 *
 * @author xuyuxiang
 * @date 2020/4/2 9:19
 */
@Data
@ApiModel
public class SysUserResult {

    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String account;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String name;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private Long avatar;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别(字典 1男 2女 3未知)
     */
    private Integer sex;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    @ApiModelProperty("手机")
    private String phone;

    /**
     * 角色
     */
    @ApiModelProperty("角色")
    private String roleNames;

    /**
     * 角色id集合
     */
    @ApiModelProperty("角色id集合")
    private List<Long> roleIdList;

    /**
     * 电话
     */
    private String tel;

    /**
     * 用户员工信息
     */
    @ApiModelProperty("用户员工信息")
    private SysEmpInfo sysEmpInfo;

    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @ApiModelProperty("状态（字典 0正常 1停用 2删除）")
    private Integer status;
}
