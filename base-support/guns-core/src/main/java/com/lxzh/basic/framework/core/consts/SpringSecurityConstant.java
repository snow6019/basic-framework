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
package com.lxzh.basic.framework.core.consts;

/**
 * SpringSecurity相关常量
 *
 * @author xuyuxiang
 * @date 2020/3/18 17:49
 */
public interface SpringSecurityConstant {

    /**
     * 放开权限校验的接口
     */
    String[] NONE_SECURITY_URL_PATTERNS = {

            //前端的
            "/favicon.ico",

            //swagger相关的
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v2/api-docs-ext",
            "/configuration/ui",
            "/configuration/security",

            //upload相关的
            CommonConstant.FILE_ACCESS_URL_PREFIX + "/**",

            //后端的
            "/",
            "/login",
            "/logout",

            "/wx/auth/**",
            "/wx/area/**",
            "/v1/wxpay/**",
            "/getKey",
            "/generate",
//	临时放行
            "/login/**",
            "/stonebaivip4.vipgz4.91tunnel.com/MP_verify_Qb2nRrMQ1UUExapH.txt",

            //文件的
            "/sysFileInfo/upload",
            "/sysFileInfo/download",
            "/sysFileInfo/preview",

            //"/v1/electric-relay/wateringOpening",
            "/sysFileInfo/preview",
            //druid的
            "/v1/sensor/**",
            //test
            "/**/test",

            //后台登录
            "/v1/ht/login",
            "/v1/ht/getKey",
            "/v1/ht/generate",

            //wx首页轮播图片
            "/wx/carousel/list",
            "/wx/vip-card/getVipCardByStatus",

            //联系我们
            "/wx/contact-us/contactUs",

            "/menu/importMenuData",
            "/wx/time-plan/appointmentTime",
            "/cos/upload",
            "/wx/prompt/getContent",
    };

}
