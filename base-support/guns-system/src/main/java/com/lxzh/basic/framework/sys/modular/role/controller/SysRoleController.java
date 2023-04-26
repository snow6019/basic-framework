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
package com.lxzh.basic.framework.sys.modular.role.controller;

import cn.hutool.core.lang.Dict;
import com.lxzh.basic.framework.sys.modular.role.entity.SysRole;
import com.lxzh.basic.framework.sys.modular.role.param.SysRoleParam;
import com.lxzh.basic.framework.core.annotion.BusinessLog;
import com.lxzh.basic.framework.core.annotion.DataScope;
import com.lxzh.basic.framework.core.annotion.Permission;
import com.lxzh.basic.framework.core.enums.LogAnnotionOpTypeEnum;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.sys.core.response.DCResponse;
import com.lxzh.basic.framework.sys.modular.role.service.SysRoleService;
import com.lxzh.basic.framework.sys.modular.role.result.SysRoleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统角色控制器
 *
 * @author xuyuxiang
 * @date 2020/3/20 19:42
 */
@Api(tags = {"系统角色接口"})
@RestController
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 查询系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:45
     */
    @Permission
    @GetMapping("/sysRole/page")
    @ApiOperation(value = "系统角色_查询", notes = "系统角色_查询")
    @BusinessLog(title = "系统角色_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<PageResult<SysRole>> page(SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.page(sysRoleParam));
    }

    /**
     * 系统角色下拉（用于授权角色时选择）
     *
     * @author xuyuxiang
     * @date 2020/4/5 16:45
     */
    @Permission
    @GetMapping("/sysRole/dropDown")
    @ApiOperation(value = "系统角色_下拉", notes = "系统角色_下拉")
    @BusinessLog(title = "系统角色_下拉", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<List<Dict>> dropDown() {
        return DCResponse.success(sysRoleService.dropDown());
    }

    /**
     * 添加系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:45
     */
    @Permission
    @PostMapping("/sysRole/add")
    @ApiOperation(value = "系统角色_增加", notes = "系统角色_增加")
    @BusinessLog(title = "系统角色_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public DCResponse<Boolean> add(@RequestBody @Validated(SysRoleParam.add.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.add(sysRoleParam));
    }

    /**
     * 删除系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:45
     */
    @Permission
    @PostMapping("/sysRole/delete")
    @ApiOperation(value = "系统角色_删除", notes = "系统角色_删除")
    @BusinessLog(title = "系统角色_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public DCResponse<Boolean> delete(@RequestBody @Validated(SysRoleParam.delete.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.delete(sysRoleParam));
    }

    /**
     * 编辑系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @PostMapping("/sysRole/edit")
    @ApiOperation(value = "系统角色_编辑", notes = "系统角色_编辑")
    @BusinessLog(title = "系统角色_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public DCResponse<Boolean> edit(@RequestBody @Validated(SysRoleParam.edit.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.edit(sysRoleParam));
    }

    /**
     * 查看系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysRole/detail")
    @ApiOperation(value = "系统角色_查看", notes = "系统角色_查看")
    @BusinessLog(title = "系统角色_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public DCResponse<SysRoleResult> detail(@Validated(SysRoleParam.detail.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.detail(sysRoleParam));
    }

    /**
     * 授权菜单
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:05
     */
    @Permission
    @PostMapping("/sysRole/grantMenu")
    @ApiOperation(value = "系统角色_授权菜单", hidden = true)
    @BusinessLog(title = "系统角色_授权菜单", opType = LogAnnotionOpTypeEnum.GRANT)
    public DCResponse<Boolean> grantMenu(@RequestBody @Validated(SysRoleParam.grantMenu.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.grantMenu(sysRoleParam));
    }

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:05
     */
    @Permission
    @DataScope
    @PostMapping("/sysRole/grantData")
    @ApiOperation(value = "系统角色_授权数据", hidden = true)
    @BusinessLog(title = "系统角色_授权数据", opType = LogAnnotionOpTypeEnum.GRANT)
    public DCResponse<Boolean> grantData(@RequestBody @Validated(SysRoleParam.grantData.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.grantData(sysRoleParam));
    }

    /**
     * 拥有菜单
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysRole/ownMenu")
    @ApiOperation(value = "系统角色_拥有菜单", hidden = true)
    @BusinessLog(title = "系统角色_拥有菜单", opType = LogAnnotionOpTypeEnum.DETAIL)
    public DCResponse<List<Long>> ownMenu(@Validated(SysRoleParam.detail.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.ownMenu(sysRoleParam));
    }

    /**
     * 拥有数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysRole/ownData")
    @ApiOperation(value = "系统角色_拥有数据", hidden = true)
    @BusinessLog(title = "系统角色_拥有数据", opType = LogAnnotionOpTypeEnum.DETAIL)
    public DCResponse<List<Long>> ownData(@Validated(SysRoleParam.detail.class) SysRoleParam sysRoleParam) {
        return DCResponse.success(sysRoleService.ownData(sysRoleParam));
    }

}
