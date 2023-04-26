package com.lxzh.basic.framework.modular.enums;

import lombok.Getter;

/**
 * @author qun.zheng
 * @description: 基础字典类型
 * @date 2019/4/17下午4:49
 */
@Getter
public enum BasicDictType {
    Sex("sex", "性别"),
    SourceCategory("source_category", "生源类别"),
    Health("health", "健康状况"),
    AccountType("account_type", "户口类型"),
    YesOrNo("yes_or_no", "是否"),
    RiskCategory("risk_category", "风险类别"),
    Classification("classification", "分级"),
    CommonStatus("common_status", "通用状态(0-正常,1-停用,2-删除)"),
    ;

    BasicDictType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    String code;
    String name;
}
