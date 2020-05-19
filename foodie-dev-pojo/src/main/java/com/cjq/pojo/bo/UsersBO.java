package com.cjq.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 被狗追过的夏天
 * date 2020-05-19
 */
@ApiModel(value = "用户对象BO", description = "从客户端,由用户传入的数据封装到此entity中")
@Data
public class UsersBO {

    @ApiModelProperty(value="用户名",example = "cjq")
    private String username;

    @ApiModelProperty(value="密码",example = "123456")
    private String password;

    @ApiModelProperty(value="确认密码",example = "123456")
    private String confirmPassword;
}
