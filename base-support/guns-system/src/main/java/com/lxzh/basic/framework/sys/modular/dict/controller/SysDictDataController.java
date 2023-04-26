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

import com.lxzh.basic.framework.sys.modular.dict.entity.SysDictData;
import com.lxzh.basic.framework.sys.modular.dict.param.SysDictDataParam;
import com.lxzh.basic.framework.sys.modular.dict.result.DictNode;
import com.lxzh.basic.framework.sys.modular.dict.service.SysDictDataService;
import com.lxzh.basic.framework.core.annotion.BusinessLog;
import com.lxzh.basic.framework.core.annotion.Permission;
import com.lxzh.basic.framework.core.enums.LogAnnotionOpTypeEnum;
import com.lxzh.basic.framework.core.pojo.response.ResponseData;
import com.lxzh.basic.framework.core.pojo.response.SuccessResponseData;
import com.lxzh.basic.framework.sys.core.response.DCResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 系统字典值控制器
 *
 * @author xuyuxiang, fengshuonan
 * @date 2020/3/31 20:49
 */
@Api(tags = {"系统字典值接口"})
@RestController
public class SysDictDataController {

    @Resource
    private SysDictDataService sysDictDataService;

    /**
     * 查询系统字典值
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 20:50
     */
    @Permission
    @GetMapping("/sysDictData/page")
    @BusinessLog(title = "系统字典值_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData page(SysDictDataParam sysDictDataParam) {
        return new SuccessResponseData(sysDictDataService.page(sysDictDataParam));
    }

    /**
     * 某个字典类型下所有的字典
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 21:03
     */
    @Permission
    @GetMapping("/sysDictData/list")
    @BusinessLog(title = "系统字典值_列表", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData list(@Validated(SysDictDataParam.list.class) SysDictDataParam sysDictDataParam) {
        return new SuccessResponseData(sysDictDataService.list(sysDictDataParam));
    }

    /**
     * 某个字典类型下所有的字典
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 21:03
     */
    @Permission
    @GetMapping("/sysDictData/getDicts")
    @ApiOperation(value = "查询多个字典项", notes = "查询多个字典项")
    @BusinessLog(title = "查询多个字典项", opType = LogAnnotionOpTypeEnum.QUERY)
    public DCResponse<Map<String, List<SysDictData>>> list(@RequestParam(required = false) String[] dictTypeCodes) {
        return DCResponse.success(sysDictDataService.findSysDictDataMap(dictTypeCodes));
    }

    /**
     * 查看系统字典值
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 20:51
     */
    @Permission
    @GetMapping("/sysDictData/detail")
    @BusinessLog(title = "系统字典值_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(SysDictDataParam.detail.class) SysDictDataParam sysDictDataParam) {
        return new SuccessResponseData(sysDictDataService.detail(sysDictDataParam));
    }

    /**
     * 添加系统字典值
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 20:50
     */
    @Permission
    @PostMapping("/sysDictData/add")
    @BusinessLog(title = "系统字典值_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(SysDictDataParam.add.class) SysDictDataParam sysDictDataParam) {
        sysDictDataService.add(sysDictDataParam);
        return new SuccessResponseData();
    }

    /**
     * 删除系统字典值
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 20:50
     */
    @Permission
    @PostMapping("/sysDictData/delete")
    @BusinessLog(title = "系统字典值_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(SysDictDataParam.delete.class) SysDictDataParam sysDictDataParam) {
        sysDictDataService.delete(sysDictDataParam);
        return new SuccessResponseData();
    }

    /**
     * 编辑系统字典值
     *
     * @author xuyuxiang, fengshuonan
     * @date 2020/3/31 20:51
     */
    @Permission
    @PostMapping("/sysDictData/edit")
    @BusinessLog(title = "系统字典值_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(SysDictDataParam.edit.class) SysDictDataParam sysDictDataParam) {
        sysDictDataService.edit(sysDictDataParam);
        return new SuccessResponseData();
    }

    /**
     * 修改状态
     *
     * @author stylefeng
     * @date 2020/5/1 9:43
     */
    @Permission
    @PostMapping("/sysDictData/changeStatus")
    @BusinessLog(title = "系统字典值_修改状态", opType = LogAnnotionOpTypeEnum.CHANGE_STATUS)
    public ResponseData changeStatus(@RequestBody @Validated(SysDictDataParam.changeStatus.class) SysDictDataParam sysDictDataParam) {
        sysDictDataService.changeStatus(sysDictDataParam);
        return new SuccessResponseData();
    }

    @GetMapping("/getDictNodes/{type}")
    @ApiOperation(value = "获取字典下拉列表", notes = "获取字典下拉列表")
    public DCResponse<List<DictNode>> getDictNodes(@PathVariable("type") String type) {
        return DCResponse.success(sysDictDataService.getDictNodes(type));
    }
}
