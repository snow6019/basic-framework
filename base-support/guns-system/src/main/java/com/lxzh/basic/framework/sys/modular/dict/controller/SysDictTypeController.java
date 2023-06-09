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
package com.lxzh.basic.framework.sys.modular.dict.controller;

import cn.hutool.core.lang.Dict;
import com.lxzh.basic.framework.sys.modular.dict.param.SysDictTypeParam;
import com.lxzh.basic.framework.sys.modular.dict.service.SysDictTypeService;
import com.lxzh.basic.framework.core.annotion.BusinessLog;
import com.lxzh.basic.framework.core.annotion.Permission;
import com.lxzh.basic.framework.core.enums.LogAnnotionOpTypeEnum;
import com.lxzh.basic.framework.core.pojo.response.ResponseData;
import com.lxzh.basic.framework.core.pojo.response.SuccessResponseData;
import com.lxzh.basic.framework.sys.core.response.DCResponse;
import com.lxzh.basic.framework.core.pojo.base.param.BaseParam;
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
 * 系统字典类型控制器
 *
 * @author xuyuxiang，fengshuonan
 * @date 2020/3/31 20:49
 */
@Api(tags = {"系统字典类型接口"})
@RestController
public class SysDictTypeController {

    @Resource
    private SysDictTypeService sysDictTypeService;

    /**
     * 分页查询系统字典类型
     *
     * @author xuyuxiang，fengshuonan
     * @date 2020/3/31 20:30
     */
    @Permission
    @GetMapping("/sysDictType/page")
    @BusinessLog(title = "系统字典类型_分页查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData page(SysDictTypeParam sysDictTypeParam) {
        return new SuccessResponseData(sysDictTypeService.page(sysDictTypeParam));
    }

    /**
     * 获取字典类型列表
     *
     * @author xuyuxiang，fengshuonan
     * @date 2020/3/31 21:03
     */
    @Permission
    @GetMapping("/sysDictType/list")
    @BusinessLog(title = "系统字典类型_列表", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData list(SysDictTypeParam sysDictTypeParam) {
        return new SuccessResponseData(sysDictTypeService.list(sysDictTypeParam));
    }

    /**
     * 获取字典类型下所有字典，举例，返回格式为：[{code:"M",value:"男"},{code:"F",value:"女"}]
     *
     * @author xuyuxiang，fengshuonan yubaoshan
     * @date 2020/3/31 21:18
     */
    @GetMapping("/sysDictType/dropDown")
    @ApiOperation(value = "系统字典类型_下拉", notes = "系统字典类型_下拉")
    @BusinessLog(title = "系统字典类型_下拉", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<List<Dict>> dropDown(@Validated(BaseParam.dropDown.class) SysDictTypeParam sysDictTypeParam) {
        return DCResponse.success(sysDictTypeService.dropDown(sysDictTypeParam));
    }

    /**
     * 查看系统字典类型
     *
     * @author xuyuxiang，fengshuonan
     * @date 2020/3/31 20:31
     */
    @Permission
    @GetMapping("/sysDictType/detail")
    @BusinessLog(title = "系统字典类型_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(BaseParam.detail.class) SysDictTypeParam sysDictTypeParam) {
        return new SuccessResponseData(sysDictTypeService.detail(sysDictTypeParam));
    }

    /**
     * 添加系统字典类型
     *
     * @author xuyuxiang，fengshuonan
     * @date 2020/3/31 20:30
     */
    @Permission
    @PostMapping("/sysDictType/add")
    @BusinessLog(title = "系统字典类型_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(BaseParam.add.class) SysDictTypeParam sysDictTypeParam) {
        sysDictTypeService.add(sysDictTypeParam);
        return new SuccessResponseData();
    }

    /**
     * 删除系统字典类型
     *
     * @author xuyuxiang，fengshuonan
     * @date 2020/3/31 20:30
     */
    @Permission
    @PostMapping("/sysDictType/delete")
    @BusinessLog(title = "系统字典类型_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(BaseParam.delete.class) SysDictTypeParam sysDictTypeParam) {
        sysDictTypeService.delete(sysDictTypeParam);
        return new SuccessResponseData();
    }

    /**
     * 编辑系统字典类型
     *
     * @author xuyuxiang，fengshuonan
     * @date 2020/3/31 20:31
     */
    @Permission
    @PostMapping("/sysDictType/edit")
    @BusinessLog(title = "系统字典类型_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(BaseParam.edit.class) SysDictTypeParam sysDictTypeParam) {
        sysDictTypeService.edit(sysDictTypeParam);
        return new SuccessResponseData();
    }

    /**
     * 修改状态
     *
     * @author stylefeng
     * @date 2020/4/30 22:20
     */
    @Permission
    @PostMapping("/sysDictType/changeStatus")
    @BusinessLog(title = "系统字典类型_修改状态", opType = LogAnnotionOpTypeEnum.CHANGE_STATUS)
    public ResponseData changeStatus(@RequestBody @Validated(BaseParam.changeStatus.class) SysDictTypeParam sysDictTypeParam) {
        sysDictTypeService.changeStatus(sysDictTypeParam);
        return new SuccessResponseData();
    }

    /**
     * 系统字典类型与字典值构造的树
     *
     * @author stylefeng
     * @date 2020/4/30 22:20
     */
    @GetMapping("/sysDictType/tree")
    @BusinessLog(title = "系统字典类型_树", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData tree() {
        return new SuccessResponseData(sysDictTypeService.tree());
    }
}
