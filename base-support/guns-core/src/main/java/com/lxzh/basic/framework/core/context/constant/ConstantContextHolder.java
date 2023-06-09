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
package com.lxzh.basic.framework.core.context.constant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.lxzh.basic.framework.core.exception.ServiceException;
import com.lxzh.basic.framework.core.exception.enums.ServerExceptionEnum;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.consts.SymbolConstant;
import com.lxzh.basic.framework.core.pojo.email.EmailConfigs;
import com.lxzh.basic.framework.core.pojo.sms.AliyunSmsConfigs;
import com.lxzh.basic.framework.core.pojo.sms.TencentSmsConfigs;

import java.util.List;

/**
 * 系统参数配置获取
 *
 * @author xuyuxiang, fengshuonan
 * @date 2020/4/14 15:34
 */
public class ConstantContextHolder {

    private static final Log log = Log.get();

    /**
     * 获取租户功能是否开启
     *
     * @author fengshuonan
     * @date 2020/9/3
     */
    public static Boolean getTenantOpenFlag() {
        return getSysConfigWithDefault("GUNS_TENANT_OPEN", Boolean.class, false);
    }

    /**
     * 获取druid监控的密码
     *
     * @author stylefeng
     * @date 2020/7/8 9:53
     */
    public static String getDruidMonitorPassword() {
        return getSysConfigWithDefault("GUNS_DRUID_PASSWORD", String.class, RandomUtil.randomString(10));
    }

    /**
     * 获取druid的账号
     *
     * @author stylefeng
     * @date 2020/7/8 9:53
     */
    public static String getDruidMonitorUsername() {
        return getSysConfigWithDefault("GUNS_DRUID_USERNAME", String.class, RandomUtil.randomString(10));
    }

    /**
     * 获取放开xss过滤的接口
     *
     * @author stylefeng
     * @date 2020/6/20 22:13
     */
    public static List<String> getUnXssFilterUrl() {
        String gunsUnXssFilterUrl = getSysConfigWithDefault("GUNS_UN_XSS_FILTER_URL", String.class, null);
        if (ObjectUtil.isEmpty(gunsUnXssFilterUrl)) {
            return CollectionUtil.newArrayList();
        } else {
            return CollectionUtil.toList(gunsUnXssFilterUrl.split(SymbolConstant.COMMA));
        }
    }

    /**
     * 获取演示环境开关是否开启，默认为false
     *
     * @author stylefeng
     * @date 2020/6/20 22:13
     */
    public static Boolean getDemoEnvFlag() {
        return getSysConfigWithDefault("GUNS_DEMO_ENV_FLAG", Boolean.class, false);
    }

    /**
     * 邮件的配置
     *
     * @author stylefeng
     * @date 2020/6/19 18:08
     */
    public static EmailConfigs getEmailConfigs() {
        String host = getSysConfig("GUNS_EMAIL_HOST", String.class, true);
        String username = getSysConfig("GUNS_EMAIL_USERNAME", String.class, true);
        String password = getSysConfig("GUNS_EMAIL_PASSWORD", String.class, true);
        Integer port = getSysConfig("GUNS_EMAIL_PORT", Integer.class, true);
        String from = getSysConfig("GUNS_EMAIL_FROM", String.class, true);
        Boolean ssl = getSysConfig("GUNS_EMAIL_SSL", Boolean.class, true);

        EmailConfigs emailConfigs = new EmailConfigs();
        emailConfigs.setHost(host);
        emailConfigs.setUser(username);
        emailConfigs.setPass(password);
        emailConfigs.setPort(port);
        emailConfigs.setFrom(from);
        emailConfigs.setSslEnable(ssl);
        return emailConfigs;
    }

    /**
     * 获取腾讯云短信的配置
     *
     * @author stylefeng
     * @date 2020/6/19 18:08
     */
    public static TencentSmsConfigs getTencentSmsConfigs() {
        String gunsTencentSmsSecretId = getSysConfig("GUNS_TENCENT_SMS_SECRET_ID", String.class, true);
        String gunsTencentSmsSecretKey = getSysConfig("GUNS_TENCENT_SMS_SECRET_KEY", String.class, true);
        String gunsTencentSmsSdkAppId = getSysConfig("GUNS_TENCENT_SMS_SDK_APP_ID", String.class, true);
        String gunsTencentSmsSign = getSysConfig("GUNS_TENCENT_SMS_SIGN", String.class, true);

        TencentSmsConfigs tencentSmsConfigs = new TencentSmsConfigs();
        tencentSmsConfigs.setSecretId(gunsTencentSmsSecretId);
        tencentSmsConfigs.setSecretKey(gunsTencentSmsSecretKey);
        tencentSmsConfigs.setSdkAppId(gunsTencentSmsSdkAppId);
        tencentSmsConfigs.setSign(gunsTencentSmsSign);
        return tencentSmsConfigs;
    }

    /**
     * 获取阿里云短信的配置
     *
     * @author stylefeng
     * @date 2020/6/19 18:08
     */
    public static AliyunSmsConfigs getAliyunSmsConfigs() {
        String gunsSmsAccesskeyId = getSysConfig("GUNS_ALIYUN_SMS_ACCESSKEY_ID", String.class, true);
        String gunsSmsAccesskeySecret = getSysConfig("GUNS_ALIYUN_SMS_ACCESSKEY_SECRET", String.class, true);
        String gunsSmsSignName = getSysConfig("GUNS_ALIYUN_SMS_SIGN_NAME", String.class, true);
        String gunsSmsLoginTemplateCode = getSysConfig("GUNS_ALIYUN_SMS_LOGIN_TEMPLATE_CODE", String.class, true);
        String gunsSmsInvalidateMinutes = getSysConfig("GUNS_ALIYUN_SMS_INVALIDATE_MINUTES", String.class, true);

        AliyunSmsConfigs aliyunSmsProperties = new AliyunSmsConfigs();
        aliyunSmsProperties.setAccessKeyId(gunsSmsAccesskeyId);
        aliyunSmsProperties.setAccessKeySecret(gunsSmsAccesskeySecret);
        aliyunSmsProperties.setSignName(gunsSmsSignName);
        aliyunSmsProperties.setLoginTemplateCode(gunsSmsLoginTemplateCode);
        aliyunSmsProperties.setInvalidateMinutes(Convert.toInt(gunsSmsInvalidateMinutes));
        return aliyunSmsProperties;
    }

    /**
     * 获取jwt密钥，默认是32位随机字符串
     *
     * @author stylefeng
     * @date 2020/6/19 18:08
     */
    public static String getJwtSecret() {
        return getSysConfigWithDefault("GUNS_JWT_SECRET", String.class, RandomUtil.randomString(32));
    }

    /**
     * 获取默认密码
     *
     * @author stylefeng
     * @date 2020/6/19 18:08
     */
    public static String getDefaultPassWord() {
        return getSysConfigWithDefault("GUNS_DEFAULT_PASSWORD", String.class, CommonConstant.DEFAULT_PASSWORD);
    }

    /**
     * 获取会话过期时间，默认2小时
     *
     * @author stylefeng
     * @date 2020/7/9 16:18
     */
    public static Long getSessionTokenExpireSec() {
        return getSysConfigWithDefault("GUNS_SESSION_EXPIRE", Long.class, 2 * 60 * 60L);
    }

    /**
     * 获取token过期时间（单位：秒）
     * <p>
     * 默认时间1天
     *
     * @author xuyuxiang
     * @date 2020/6/19 18:08
     */
    public static Long getTokenExpireSec() {
        return getSysConfigWithDefault("GUNS_TOKEN_EXPIRE", Long.class, 86400L);
    }

    /**
     * 获取微信支付的过期时间（单位：秒）
     * <p>
     * 默认时间1分钟
     *
     * @author wr
     * @date 2023/1/29 15:01
     */
    public static Long getWxOrderPayExpireSec() {
        return getSysConfigWithDefault("WX_ORDER_PAY_EXPIRE", Long.class, 60L);
    }

    /**
     * 获取订单的交易结束时间（单位：秒），这个时间配置要大于上面的 微信支付的过期时间（getWxOrderPayExpireSec 这个方法的）
     * <p>
     * 默认时间2分钟
     *
     * @author wr
     * @date 2023/1/29 15:01
     */
    public static Long getTrxOrderExpireSec() {
        return getSysConfigWithDefault("TRX_ORDER_EXPIRE", Long.class, 120L);
    }

    /**
     * 获取自定义的windows环境本地文件上传路径
     *
     * @author xuyuxiang
     * @date 2020/6/19 18:09
     */
    public static String getDefaultFileUploadPathForWindows() {
        return getSysConfigWithDefault("GUNS_FILE_UPLOAD_PATH_FOR_WINDOWS", String.class, "");
    }

    /**
     * 获取自定义的linux环境本地文件上传路径
     *
     * @author xuyuxiang
     * @date 2020/6/19 18:09
     */
    public static String getDefaultFileUploadPathForLinux() {
        return getSysConfigWithDefault("GUNS_FILE_UPLOAD_PATH_FOR_LINUX", String.class, "");
    }

    /**
     * 获取是否允许单用户登陆的开关， 默认为false
     *
     * @author xuyuxiang
     * @date 2020/6/19 18:09
     */
    public static Boolean getEnableSingleLogin() {
        return getSysConfigWithDefault("GUNS_ENABLE_SINGLE_LOGIN", Boolean.class, false);
    }

    /**
     * 获取阿里云定位接口
     *
     * @author xuyuxiang
     * @date 2020/7/20 9:20
     **/
    public static String getIpGeoApi() {
        return getSysConfig("GUNS_IP_GEO_API", String.class, false);
    }

    /**
     * 获取阿里云定位appCode
     *
     * @author xuyuxiang
     * @date 2020/7/20 10:33
     **/
    public static String getIpGeoAppCode() {
        return getSysConfig("GUNS_IP_GEO_APP_CODE", String.class, false);
    }

    /**
     * 获取config表中的配置，如果为空返回默认值
     *
     * @param configCode   变量名称，对应sys_config表中的code
     * @param clazz        返回变量值的类型
     * @param defaultValue 如果结果为空返回此默认值
     * @author stylefeng
     * @date 2020/6/20 22:03
     */
    public static <T> T getSysConfigWithDefault(String configCode, Class<T> clazz, T defaultValue) {
        String configValue = ConstantContext.me().getStr(configCode);
        if (ObjectUtil.isEmpty(configValue)) {
            // 将默认值加入到缓存常量
            log.warn(">>> 系统配置sys_config表中存在空项，configCode为：{}，系统采用默认值：{}", configCode, defaultValue);
            ConstantContext.me().put(configCode, defaultValue);
            return defaultValue;
        } else {
            try {
                return Convert.convert(clazz, configValue);
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }

    /**
     * 获取config表中的配置，如果为空，是否抛出异常
     *
     * @param configCode   变量名称，对应sys_config表中的code
     * @param clazz        返回变量值的类型
     * @param nullThrowExp 若为空是否抛出异常
     * @author stylefeng
     * @date 2020/6/20 22:03
     */
    public static <T> T getSysConfig(String configCode, Class<T> clazz, Boolean nullThrowExp) {
        String configValue = ConstantContext.me().getStr(configCode);
        if (ObjectUtil.isEmpty(configValue)) {
            if (nullThrowExp) {
                String format = StrUtil.format(">>> 系统配置sys_config表中存在空项，configCode为：{}", configCode);
                log.error(format);
                throw new ServiceException(ServerExceptionEnum.CONSTANT_EMPTY.getCode(), format);
            } else {
                return null;
            }
        } else {
            try {
                return Convert.convert(clazz, configValue);
            } catch (Exception e) {
                if (nullThrowExp) {
                    String format = StrUtil.format(">>> 系统配置sys_config表中存在格式错误的值，configCode={}，configValue={}", configCode, configValue);
                    log.error(format);
                    throw new ServiceException(ServerExceptionEnum.CONSTANT_EMPTY.getCode(), format);
                } else {
                    return null;
                }
            }
        }
    }
}
