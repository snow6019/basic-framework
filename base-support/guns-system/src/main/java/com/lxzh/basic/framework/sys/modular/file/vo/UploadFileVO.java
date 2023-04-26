package com.lxzh.basic.framework.sys.modular.file.vo;

import com.lxzh.basic.framework.sys.modular.file.entity.SysFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * <p>
 * 上传文件响应对象
 * </p>
 *
 * @author wr
 * @since 2021-12-31
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel
public class UploadFileVO {

    @ApiModelProperty(value = "文件id")
    private Long id;

    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    public UploadFileVO(SysFileInfo sysFileInfo) {
        this.id = sysFileInfo.getId();
        this.fileName = sysFileInfo.getFileOriginName();
        this.fileUrl = sysFileInfo.getFilePath();
    }
}
