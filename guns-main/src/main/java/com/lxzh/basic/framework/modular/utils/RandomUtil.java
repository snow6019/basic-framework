package com.lxzh.basic.framework.modular.utils;


import java.security.SecureRandom;
import java.util.Random;

/**
 * @author : shenjue
 * @Description: TODO
 * @date : 2023/2/1 16:26
 **/
public class RandomUtil {
    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyz"; // 数字和26个字母组成
    private static final Random RANDOM = new SecureRandom();
    public static void main(String[] args) {
        System.out.println(getRandomNumber());
    }

    /**
     * 获取长度为 6 的随机字母+数字
     * @return 随机数字
     */
    public static String getRandomNumber() {
        char[] nonceChars = new char[6];  //指定长度为6位/自己可以要求设置

        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }
}
