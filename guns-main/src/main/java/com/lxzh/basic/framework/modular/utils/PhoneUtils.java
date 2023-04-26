package com.lxzh.basic.framework.modular.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wsn
 */
public class PhoneUtils {
    public static final String REGEX_MOBILE = "(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}";

    public static String regexMobile(String content) {
        Pattern p = Pattern.compile(REGEX_MOBILE);
        Matcher m = p.matcher(content);
        String paramStr = new String(content);
        while (m.find()) { //一定需要先查找再调用group获取电话号码
            String group = m.group();
            paramStr = paramStr.replaceAll(group, group.substring(0, 3) + "****" + group.substring(7, 11));
        }

        return paramStr;
    }
}
