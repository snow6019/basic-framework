package com.lxzh.basic.framework.sys.core.listener;

import com.lxzh.basic.framework.core.context.param.RequestParamContext;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * 用来清除临时缓存的@RequestBody的请求参数
 *
 * @author fengshuonan
 * @date 2020/8/21 21:14
 */
public class RemoveRequestParamListener implements ApplicationListener<ServletRequestHandledEvent> {

    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        RequestParamContext.clear();
    }

}