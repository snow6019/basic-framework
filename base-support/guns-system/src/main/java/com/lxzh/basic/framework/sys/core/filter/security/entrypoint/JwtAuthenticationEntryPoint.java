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
package com.lxzh.basic.framework.sys.core.filter.security.entrypoint;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.exception.enums.AuthExceptionEnum;
import com.lxzh.basic.framework.core.exception.enums.PermissionExceptionEnum;
import com.lxzh.basic.framework.core.exception.enums.ServerExceptionEnum;
import com.lxzh.basic.framework.core.util.ResponseUtil;
import com.lxzh.basic.framework.sys.core.cache.ResourceCache;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

/**
 * 未认证用户访问须授权资源端点
 *
 * @author xuyuxiang
 * @date 2020/3/18 11:52
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final Log log = Log.get();

    @Resource
    private ResourceCache resourceCache;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 访问未经授权的接口时执行此方法，未经授权的接口包含系统中存在和不存在的接口，分别处理
     *
     * @author xuyuxiang
     * @date 2020/3/18 19:15
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {

        String requestUri = request.getRequestURI();
        String med = request.getMethod();

        //1.检查redis中RESOURCE缓存是否为空，如果为空，直接抛出系统异常，缓存url作用详见ResourceCollectListener
        Collection<String> urlCollections = resourceCache.getAllResources();
        if (ObjectUtil.isEmpty(urlCollections)) {
            log.error(">>> 获取缓存的Resource Url为空，请检查缓存中是否被误删，requestUri={}", requestUri);
            ResponseUtil.responseExceptionError(response,
                    ServerExceptionEnum.SERVER_ERROR.getCode(),
                    ServerExceptionEnum.SERVER_ERROR.getMessage(),
                    new ServiceException(ServerExceptionEnum.SERVER_ERROR).getStackTrace()[0].toString());
            return;
        }

        //2.判断缓存的url中是否有当前请求的uri，没有的话响应给前端404
        //if (!urlCollections.contains(requestUri) && (med.toUpperCase().equals("GET")) && !urlCollections.contains(requestUri.substring(0,requestUri.lastIndexOf("/")))) {
        /*if (!urlCollections.contains(requestUri)) {
            ResponseUtil.responseExceptionError(response,
                    PermissionExceptionEnum.URL_NOT_EXIST.getCode(),
                    PermissionExceptionEnum.URL_NOT_EXIST.getMessage(),
                    new ServiceException(PermissionExceptionEnum.URL_NOT_EXIST).getStackTrace()[0].toString());
            return;
        }*/
        if(!match(urlCollections,requestUri)){
            ResponseUtil.responseExceptionError(response,
                    PermissionExceptionEnum.URL_NOT_EXIST.getCode(),
                    PermissionExceptionEnum.URL_NOT_EXIST.getMessage(),
                    new ServiceException(PermissionExceptionEnum.URL_NOT_EXIST).getStackTrace()[0].toString());
            return;
        }


        //3.响应给前端无权限访问本接口（没有携带token）
        ResponseUtil.responseExceptionError(response,
                AuthExceptionEnum.REQUEST_TOKEN_EMPTY.getCode(),
                AuthExceptionEnum.REQUEST_TOKEN_EMPTY.getMessage(),
                new ServiceException(AuthExceptionEnum.REQUEST_TOKEN_EMPTY).getStackTrace()[0].toString());
    }

    public boolean match(Collection<String> urlSet, String path) {
        for (String temp : urlSet) {
            boolean flag = antPathMatcher.match(temp, path);
            if (flag) {
                return true;
            }
        }
        return false;
    }
}
