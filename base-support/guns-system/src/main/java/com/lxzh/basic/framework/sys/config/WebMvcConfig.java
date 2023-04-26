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

import cn.hutool.system.SystemUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.lxzh.basic.framework.sys.core.validator.GunsValidator;
import com.lxzh.basic.framework.core.consts.CommonConstant;
import com.lxzh.basic.framework.core.context.constant.ConstantContextHolder;
import com.lxzh.basic.framework.core.web.GunsRequestResponseBodyMethodProcessor;
import com.lxzh.basic.framework.sys.core.error.GunsErrorAttributes;
import com.lxzh.basic.framework.sys.core.filter.RequestNoFilter;
import com.lxzh.basic.framework.sys.core.filter.xss.XssFilter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * web配置
 *
 * @author stylefeng
 * @date 2020/4/11 10:23
 */
@Configuration
@Import({cn.hutool.extra.spring.SpringUtil.class})
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 错误信息提示重写
     *
     * @author stylefeng
     * @date 2020/4/14 22:27
     */
    @Bean
    public GunsErrorAttributes gunsErrorAttributes() {
        return new GunsErrorAttributes();
    }

    /**
     * 静态资源映射
     *
     * @author stylefeng
     * @date 2020/4/11 10:23
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //swagger增强的静态资源映射
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        if (SystemUtil.getOsInfo().isWindows()) {
            registry.addResourceHandler(CommonConstant.FILE_ACCESS_URL_PREFIX + "/**").addResourceLocations("file:" + ConstantContextHolder.getDefaultFileUploadPathForWindows() + File.separator);
        } else {
            registry.addResourceHandler(CommonConstant.FILE_ACCESS_URL_PREFIX + "/**").addResourceLocations("file:" + ConstantContextHolder.getDefaultFileUploadPathForLinux() + File.separator);
        }
    }

    /**
     * xss过滤器
     *
     * @author stylefeng
     * @date 2020/6/21 10:30
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>(new XssFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * 请求唯一编号生成器
     *
     * @author stylefeng
     * @date 2020/6/21 10:30
     */
    @Bean
    public FilterRegistrationBean<RequestNoFilter> requestNoFilterFilterRegistrationBean() {
        FilterRegistrationBean<RequestNoFilter> registration = new FilterRegistrationBean<>(new RequestNoFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * json自定义序列化工具,long转string
     *
     * @author stylefeng
     * @date 2020/5/28 14:48
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder
                        .serializerByType(Date.class,new DateSerializer(true, DateFormat.getDateInstance()))
                        .serializerByType(LocalDate.class,new LocalDateSerializer())
                        .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer())
                        .serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }

    /**
     * 序列化
     */
    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {

            if (value != null){
                long timestamp = value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    /**
     * 序列化
     */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {

            if (value != null){

                long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    /**
     * 序列化
     */
    /*public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {

            if (value != null){

                long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }*/

    /**
     * 自定义的spring参数校验器，重写主要为了保存一些在自定义validator中读不到的属性
     *
     * @author fengshuonan
     * @date 2020/8/12 20:18
     */
    @Bean
    public GunsValidator gunsValidator() {
        return new GunsValidator();
    }


    /**
     * 自定义的GunsRequestResponseBodyMethodProcessor，放在所有resolvers之前
     *
     * @author fengshuonan
     * @date 2020/8/21 21:09
     */
    @Configuration
    public static class MethodArgumentResolver {

        @Resource
        private RequestMappingHandlerAdapter adapter;

        @PostConstruct
        public void injectSelfMethodArgumentResolver() {
            List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
            argumentResolvers.add(new GunsRequestResponseBodyMethodProcessor(adapter.getMessageConverters()));
            argumentResolvers.addAll(Objects.requireNonNull(adapter.getArgumentResolvers()));
            adapter.setArgumentResolvers(argumentResolvers);
        }
    }

    /**
     * 验证码生成相关
     */
    @Bean
    public DefaultKaptcha kaptcha() {
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.border.color", "105,179,90");
        properties.put("kaptcha.textproducer.font.color", "blue");
        properties.put("kaptcha.image.width", "125");
        properties.put("kaptcha.image.height", "45");
        properties.put("kaptcha.textproducer.font.size", "35");
        properties.put("kaptcha.session.key", "code");
        properties.put("kaptcha.textproducer.char.length", "4");
        properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        properties.put("kaptcha.obscurificator.impl", "com.lxzh.basic.framework.sys.config.NoWaterRipple");
        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}