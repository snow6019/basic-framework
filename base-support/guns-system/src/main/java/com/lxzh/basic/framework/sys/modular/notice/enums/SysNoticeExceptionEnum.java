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
package com.lxzh.basic.framework.sys.modular.notice.enums;

import com.lxzh.basic.framework.core.annotion.ExpEnumType;
import com.lxzh.basic.framework.core.exception.enums.abs.AbstractBaseExceptionEnum;
import com.lxzh.basic.framework.core.factory.ExpEnumCodeFactory;
import com.lxzh.basic.framework.sys.core.consts.SysExpEnumConstant;

/**
 * 系统应用相关异常枚举
 *
 * @author xuyuxiang
 * @date 2020/3/26 10:11
 */
@ExpEnumType(module = SysExpEnumConstant.GUNS_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SYS_NOTICE_EXCEPTION_ENUM)
public enum SysNoticeExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 通知公告不存在
     */
    NOTICE_NOT_EXIST(1, "通知公告不存在"),

    /**
     * 编辑失败
     */
    NOTICE_CANNOT_EDIT(2, "编辑失败，通知公告非草稿状态时无法编辑"),

    /**
     * 状态格式错误
     */
    NOTICE_STATUS_ERROR(3, "状态格式错误，请检查status参数"),

    /**
     * 删除失败
     */
    NOTICE_CANNOT_DELETE(4, "删除失败，通知公告已发布或已删除");

    private final Integer code;

    private final String message;

    SysNoticeExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
