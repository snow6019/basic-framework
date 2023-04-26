package com.lxzh.basic.framework.modular.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.sys.core.cache.KaptchaCache;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * @author : baiwandong
 * @Description: TODO
 * @date : 2022/4/24 13:50
 **/
@UtilityClass
public class KaptchaCodeUtil {
    private KaptchaCache kaptchaCache = SpringUtil.getBean(KaptchaCache.class);

    /**
     * 判断验证码是否正确
     * @param uuid uuid
     * @param code 验证码
     * @return 是否正确
     */
    public boolean checkCode(String uuid , String code){
        String redisValue = Optional.ofNullable(kaptchaCache.get(uuid)).orElse(StrUtil.EMPTY);
        if("".equals(redisValue.trim())){
            throw new ServiceException(500,"验证码已失效！");
        }
        return StrUtil.equals(redisValue,code) ;
    }
}
