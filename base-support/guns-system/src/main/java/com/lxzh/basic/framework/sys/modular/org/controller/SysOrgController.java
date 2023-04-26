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
package com.lxzh.basic.framework.sys.modular.org.controller;

import com.lxzh.basic.framework.sys.modular.org.service.SysOrgService;
import com.lxzh.basic.framework.core.annotion.BusinessLog;
import com.lxzh.basic.framework.core.annotion.DataScope;
import com.lxzh.basic.framework.core.annotion.Permission;
import com.lxzh.basic.framework.core.enums.LogAnnotionOpTypeEnum;
import com.lxzh.basic.framework.core.pojo.node.AntdBaseTreeNode;
import com.lxzh.basic.framework.core.pojo.response.ResponseData;
import com.lxzh.basic.framework.core.pojo.response.SuccessResponseData;
import com.lxzh.basic.framework.sys.core.response.DCResponse;
import com.lxzh.basic.framework.sys.modular.org.entity.SysOrg;
import com.lxzh.basic.framework.sys.modular.org.param.SysOrgParam;
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
 * 系统组织机构控制器
 *
 * @author xuyuxiang
 * @date 2020/3/20 19:47
 */
@Api(tags = {"系统组织机构接口"})
@RestController
public class SysOrgController {

    @Resource
    private SysOrgService sysOrgService;

    /**
     * 查询系统机构
     *
     * @author xuyuxiang
     * @date 2020/5/11 15:49
     */
    @Permission
    @DataScope
    @GetMapping("/sysOrg/page")
    @BusinessLog(title = "系统机构_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData page(SysOrgParam sysOrgParam) {
        return new SuccessResponseData(sysOrgService.page(sysOrgParam));
    }

    /**
     * 系统组织机构列表
     *
     * @author xuyuxiang
     * @date 2020/3/26 10:20
     */
    @Permission
    @DataScope
    @GetMapping("/sysOrg/list")
    @ApiOperation(value = "系统组织机构列表", notes = "系统组织机构列表")
    @BusinessLog(title = "系统组织机构_列表", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<List<SysOrg>> list(SysOrgParam sysOrgParam) {
        return DCResponse.success(sysOrgService.list(sysOrgParam));
    }

    /**
     * 添加系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/25 14:44
     */
    @Permission
    @DataScope
    @PostMapping("/sysOrg/add")
    @BusinessLog(title = "系统组织机构_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(SysOrgParam.add.class) SysOrgParam sysOrgParam) {
        sysOrgService.add(sysOrgParam);
        return new SuccessResponseData();
    }

    /**
     * 删除系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/25 14:54
     */
    @Permission
    @DataScope
    @PostMapping("/sysOrg/delete")
    @BusinessLog(title = "系统组织机构_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(SysOrgParam.delete.class) SysOrgParam sysOrgParam) {
        sysOrgService.delete(sysOrgParam);
        return new SuccessResponseData();
    }

    /**
     * 编辑系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/25 14:54
     */
    @Permission
    @DataScope
    @PostMapping("/sysOrg/edit")
    @BusinessLog(title = "系统组织机构_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(SysOrgParam.edit.class) SysOrgParam sysOrgParam) {
        sysOrgService.edit(sysOrgParam);
        return new SuccessResponseData();
    }

    /**
     * 查看系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:49
     */
    @Permission
    @GetMapping("/sysOrg/detail")
    @BusinessLog(title = "系统组织机构_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(SysOrgParam.detail.class) SysOrgParam sysOrgParam) {
        return new SuccessResponseData(sysOrgService.detail(sysOrgParam));
    }

    /**
     * 获取组织机构树
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:55
     */
    @Permission
    @DataScope
    @GetMapping("/sysOrg/tree")
    @ApiOperation(value = "系统组织机构_树", notes = "系统组织机构_树")
    @BusinessLog(title = "系统组织机构_树", opType = LogAnnotionOpTypeEnum.TREE)
    public DCResponse<List<AntdBaseTreeNode>> tree(SysOrgParam sysOrgParam) {
        return DCResponse.success(sysOrgService.tree(sysOrgParam));
    }
}
