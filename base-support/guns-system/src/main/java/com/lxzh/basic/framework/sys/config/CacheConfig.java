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
package com.lxzh.basic.framework.sys.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.lxzh.basic.framework.core.context.constant.ConstantContextHolder;
import com.lxzh.basic.framework.core.pojo.login.SysLoginUser;
import com.lxzh.basic.framework.sys.core.cache.KaptchaCache;
import com.lxzh.basic.framework.sys.core.cache.MappingCache;
import com.lxzh.basic.framework.sys.core.cache.ResourceCache;
import com.lxzh.basic.framework.sys.core.cache.UserCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 缓存的配置，默认使用基于内存的缓存，如果分布式部署请更换为redis
 *
 * @author xuyuxiang
 * @date 2020/7/9 11:43
 */
@Configuration
public class CacheConfig {

    /**
     * url资源的缓存，默认不过期
     *
     * @author stylefeng
     * @date 2020/7/9 11:44
     */
    @Bean
    public ResourceCache resourceCache() {
        return new ResourceCache();
    }

    /**
     * 登录用户的缓存，默认过期时间，根据系统sys_config中的常量决定
     *
     * @author stylefeng
     * @date 2020/7/9 11:44
     */
    @Bean
    public UserCache userCache() {
        TimedCache<String, SysLoginUser> timedCache =
                CacheUtil.newTimedCache(ConstantContextHolder.getSessionTokenExpireSec() * 1000);

        //定时清理缓存，间隔1秒
        timedCache.schedulePrune(1000);

        return new UserCache(timedCache);
    }

    /**
     * mapping映射缓存
     *
     * @author fengshuonan
     * @date 2020/7/24 13:55
     */
    @Bean
    public MappingCache mappingCache() {
        TimedCache<String, Map<String, Object>> timedCache =
                CacheUtil.newTimedCache(2 * 60 * 1000);

        // 定时清理缓存，间隔1秒
        timedCache.schedulePrune(1000);

        return new MappingCache(timedCache);
    }

    /**
     * mapping映射缓存
     *
     * @author fengshuonan
     * @date 2020/7/24 13:55
     */
    @Bean
    public KaptchaCache kaptchaCache() {
        TimedCache<String, String> timedCache =
                CacheUtil.newTimedCache(2 * 60 * 1000);

        // 定时清理缓存，间隔1秒
        timedCache.schedulePrune(1000);

        return new KaptchaCache(timedCache);
    }

}
