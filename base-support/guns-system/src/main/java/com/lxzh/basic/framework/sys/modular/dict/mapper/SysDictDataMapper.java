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
package com.lxzh.basic.framework.sys.modular.dict.mapper;

import com.lxzh.basic.framework.sys.modular.dict.entity.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统字典值mapper接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:12
 */
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 通过字典类型code获取字典编码值列表
     *
     * @param dictTypeCodes 字典类型编码集合
     * @return 字典编码值列表
     * @author fengshuonan
     * @date 2020/8/9 14:27
     */
    List<String> getDictCodesByDictTypeCode(String[] dictTypeCodes);

    /**
     * 通过字典类型code获取字典编码值列表
     *
     * @param dictTypeCodes 字典类型编码集合
     * @return 字典编码值列表
     * @author fengshuonan
     * @date 2020/8/9 14:27
     */
    List<SysDictData> findListByDictTypeCode(@Param("dictTypeCodes") String[] dictTypeCodes);

    /**
     * 通过字典类型code获取字典编码值列表
     *
     * @param dictTypeCode
     * @param dictCode
     * @return
     */
    SysDictData findOneByDictTypeCodeAndDictCode(@Param("dictTypeCode") String dictTypeCode, @Param("dictCode") String dictCode);

}
