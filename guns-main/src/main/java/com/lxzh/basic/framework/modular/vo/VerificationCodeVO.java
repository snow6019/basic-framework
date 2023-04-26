package com.lxzh.basic.framework.modular.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeVO {
    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("base编码")
    private String base64Code;
}
