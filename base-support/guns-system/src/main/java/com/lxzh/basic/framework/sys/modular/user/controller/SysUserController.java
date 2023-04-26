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
package com.lxzh.basic.framework.sys.modular.user.controller;

import cn.hutool.core.lang.Dict;
import com.lxzh.basic.framework.sys.modular.user.param.SysUserParam;
import com.lxzh.basic.framework.core.annotion.BusinessLog;
import com.lxzh.basic.framework.core.annotion.DataScope;
import com.lxzh.basic.framework.core.annotion.Permission;
import com.lxzh.basic.framework.core.enums.LogAnnotionOpTypeEnum;
import com.lxzh.basic.framework.core.pojo.page.PageResult;
import com.lxzh.basic.framework.sys.core.response.DCResponse;
import com.lxzh.basic.framework.sys.modular.user.result.SysUserResult;
import com.lxzh.basic.framework.sys.modular.user.service.SysUserService;
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
 * 系统用户控制器
 *
 * @author xuyuxiang
 * @date 2020/3/19 21:14
 */
@Api(tags = {"系统用户接口"})
@RestController
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 查询系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/20 21:00
     */
    @Permission
    @DataScope
    @GetMapping("/sysUser/page")
    @ApiOperation(value = "系统用户_查询", notes = "系统用户_查询")
    @BusinessLog(title = "系统用户_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<PageResult<SysUserResult>> page(SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.page(sysUserParam));
    }

    /**
     * 增加系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/add")
    @ApiOperation(value = "系统用户_增加", notes = "系统用户_增加")
    @BusinessLog(title = "系统用户_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public DCResponse<Boolean> add(@RequestBody @Validated(SysUserParam.add.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.add(sysUserParam));
    }

    /**
     * 删除系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/delete")
    @ApiOperation(value = "系统用户_删除", notes = "系统用户_删除")
    @BusinessLog(title = "系统用户_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public DCResponse<Boolean> delete(@RequestBody @Validated(SysUserParam.delete.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.delete(sysUserParam));
    }

    /**
     * 编辑系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/edit")
    @ApiOperation(value = "系统用户_编辑", notes = "系统用户_编辑")
    @BusinessLog(title = "系统用户_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public DCResponse<Boolean> edit(@RequestBody @Validated(SysUserParam.edit.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.edit(sysUserParam));
    }

    /**
     * 查看系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @GetMapping("/sysUser/detail")
    @ApiOperation(value = "系统用户_查看", notes = "系统用户_查看")
    @BusinessLog(title = "系统用户_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public DCResponse<SysUserResult> detail(@Validated(SysUserParam.detail.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.detail(sysUserParam));
    }

    /**
     * 修改状态
     *
     * @author xuyuxiang
     * @date 2020/5/25 14:32
     */
    @Permission
    @PostMapping("/sysUser/changeStatus")
    @ApiOperation(value = "系统用户_修改状态", notes = "系统用户_修改状态")
    @BusinessLog(title = "系统用户_修改状态", opType = LogAnnotionOpTypeEnum.CHANGE_STATUS)
    public DCResponse<Boolean> changeStatus(@RequestBody @Validated(SysUserParam.changeStatus.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.changeStatus(sysUserParam));
    }

    /**
     * 授权角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:05
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/grantRole")
    @ApiOperation(value = "系统用户_授权角色", hidden = true)
    @BusinessLog(title = "系统用户_授权角色", opType = LogAnnotionOpTypeEnum.GRANT)
    public DCResponse<Boolean> grantRole(@RequestBody @Validated(SysUserParam.grantRole.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.grantRole(sysUserParam));
    }

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:05
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/grantData")
    @ApiOperation(value = "系统用户_授权数据", hidden = true)
    @BusinessLog(title = "系统用户_授权数据", opType = LogAnnotionOpTypeEnum.GRANT)
    public DCResponse<Boolean> grantData(@RequestBody @Validated(SysUserParam.grantData.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.grantData(sysUserParam));
    }

    /**
     * 更新信息
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:27
     */
    @PostMapping("/sysUser/updateInfo")
    @ApiOperation(value = "系统用户_更新信息", notes = "系统用户_更新信息")
    @BusinessLog(title = "系统用户_更新信息", opType = LogAnnotionOpTypeEnum.UPDATE)
    public DCResponse<Boolean> updateInfo(@RequestBody @Validated(SysUserParam.updateInfo.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.updateInfo(sysUserParam));
    }

    /**
     * 修改密码
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:42
     */
    @PostMapping("/sysUser/updatePwd")
    @ApiOperation(value = "系统用户_修改密码", notes = "系统用户_修改密码")
    @BusinessLog(title = "系统用户_修改密码", opType = LogAnnotionOpTypeEnum.UPDATE)
    public DCResponse<Boolean> updatePwd(@RequestBody @Validated(SysUserParam.updatePwd.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.updatePwd(sysUserParam));
    }

    /**
     * 拥有角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysUser/ownRole")
    @ApiOperation(value = "系统用户_拥有角色", notes = "系统用户_拥有角色")
    @BusinessLog(title = "系统用户_拥有角色", opType = LogAnnotionOpTypeEnum.DETAIL)
    public DCResponse<List<Long>> ownRole(@Validated(SysUserParam.detail.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.ownRole(sysUserParam));
    }

    /**
     * 拥有数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysUser/ownData")
    @ApiOperation(value = "系统用户_拥有数据", hidden = true)
    @BusinessLog(title = "系统用户_拥有数据", opType = LogAnnotionOpTypeEnum.DETAIL)
    public DCResponse<List<Long>> ownData(@Validated(SysUserParam.detail.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.ownData(sysUserParam));
    }

    /**
     * 重置密码
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:42
     */
    @Permission
    @PostMapping("/sysUser/resetPwd")
    @ApiOperation(value = "系统用户_重置密码", notes = "系统用户_重置密码")
    @BusinessLog(title = "系统用户_重置密码", opType = LogAnnotionOpTypeEnum.UPDATE)
    public DCResponse<Boolean> resetPwd(@RequestBody @Validated(SysUserParam.resetPwd.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.resetPwd(sysUserParam));
    }

    /**
     * 修改头像
     *
     * @author xuyuxiang
     * @date 2020/6/28 15:19
     */
    @PostMapping("/sysUser/updateAvatar")
    @ApiOperation(value = "系统用户_修改头像", notes = "系统用户_修改头像")
    @BusinessLog(title = "系统用户_修改头像", opType = LogAnnotionOpTypeEnum.UPDATE)
    public DCResponse<Boolean> updateAvatar(@RequestBody @Validated(SysUserParam.updateAvatar.class) SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.updateAvatar(sysUserParam));
    }

//    /**
//     * 导出系统用户
//     *
//     * @author xuyuxiang
//     * @date 2020/6/30 16:07
//     */
//    @Permission
//    @GetMapping("/sysUser/export")
//    @ApiOperation(value = "系统用户_导出", notes = "系统用户_导出")
//    @BusinessLog(title = "系统用户_导出", opType = LogAnnotionOpTypeEnum.EXPORT)
//    public void export(SysUserParam sysUserParam) {
//        sysUserService.export(sysUserParam);
//    }


    /**
     * 用户选择器
     *
     * @author xuyuxiang
     * @date 2020/7/3 13:17
     */
    @Permission
    @GetMapping("/sysUser/selector")
    @ApiOperation(value = "系统用户_选择器", notes = "系统用户_选择器")
    @BusinessLog(title = "系统用户_选择器", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<List<Dict>> selector(SysUserParam sysUserParam) {
        return DCResponse.success(sysUserService.selector(sysUserParam));
    }
}
