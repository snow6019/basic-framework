package com.lxzh.basic.framework.modular.utils;


public class EnumUtil {

    /**
     * 根据数字找对应枚举类的释义
     *
     * @param enumClass
     * @param param
     * @return
     */
    public static String getName(Class enumClass, Object param) {
        String enumName = enumClass.getSimpleName();
        if (!enumClass.isEnum() || null == param) return null;
        Integer value = null;
        if (param instanceof Integer) {
            value = (Integer) param;
        } else if (param instanceof Byte) {
            value = Integer.valueOf(param.toString());
        } else if (param instanceof String) {
            value = Integer.valueOf(param.toString());
        }
        try {
            for (Object obj : enumClass.getEnumConstants()) {
                if (enumClass.getMethod("getCode").invoke(obj).equals(value)) {
                    return enumClass.getMethod("getMessage").invoke(obj).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println("EnumUtil.class.getName() = " + EnumUtil.class.getName());
    }
}
