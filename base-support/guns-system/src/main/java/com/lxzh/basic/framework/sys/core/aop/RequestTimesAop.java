package com.lxzh.basic.framework.sys.core.aop;

import cn.hutool.core.util.StrUtil;
import com.lxzh.basic.framework.core.annotion.RequestTimes;
import com.lxzh.basic.framework.core.consts.AopSortConstant;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


/**
 * 请求次数注解Aop切面
 *
 * @author wr
 * @date 2022-08-10
 */
@Slf4j
@Aspect
@Component
@Order(AopSortConstant.PERMISSION_AOP-1)
public class RequestTimesAop {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.lxzh.basic.framework.core.annotion.RequestTimes)")
    public void WebPointCut() {
    }

    @Before("WebPointCut() && @annotation(times)")
    public void ifovertimes(final JoinPoint joinPoint, RequestTimes times) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = IPUtils.getIpAddr(request);
        String url = request.getRequestURL().toString();
        String key = "ifovertimes".concat("@").concat(url).concat("@").concat(ip);
        //访问次数加一
        long count = redisTemplate.opsForValue().increment(key, 1);
        //如果是第一次，则设置过期时间
        if (count == 1) {
            redisTemplate.expire(key, times.time(), TimeUnit.MILLISECONDS);
        }
        if (count > times.count()) {
            log.error(">>> ip[{}]接口[{}]请求次数[{}]超出限制次数[{}]", ip, url, count, times.count());
            throw new ServiceException(500, StrUtil.format("ip[{}]接口[{}]请求次数[{}]超出限制次数[{}]", ip, url, count, times.count()));
        }
    }
}