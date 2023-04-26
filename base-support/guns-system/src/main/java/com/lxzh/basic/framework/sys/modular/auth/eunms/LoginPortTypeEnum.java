package com.lxzh.basic.framework.sys.modular.auth.eunms;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 *
 * </p>
 *
 * @author wr
 * @since 2021-12-30
 */
@Getter
public enum LoginPortTypeEnum {

    NOT_RECTIFY(1, "后台管理端"),
    WXCUSTM(2,"客户微信端");

    private final Integer code;

    private final String message;

    LoginPortTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static LoginPortTypeEnum getEnumByCode(Integer code) {
        if (Objects.nonNull(code)) {
            for (LoginPortTypeEnum value : values()) {
                if (value.getCode().equals(code)) {
                    return value;
                }
            }
        }
        return null;
    }

    public static Map<Integer, String> getEnumMap() {
        Map<Integer, String> map = Arrays.stream(values()).collect(Collectors.toMap(LoginPortTypeEnum::getCode, LoginPortTypeEnum::getMessage));
        return map;
    }

    public static LoginPortTypeEnum getEnumByMsg(String msg) {
        for (LoginPortTypeEnum value : values()) {
            if (value.getMessage().equals(msg)) {
                return value;
            }
        }
        return null;
    }
}
