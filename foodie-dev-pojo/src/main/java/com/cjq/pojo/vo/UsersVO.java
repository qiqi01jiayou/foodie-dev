package com.cjq.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author 被狗追过的夏天
 * date 2020-05-31
 */
@Data
public class UsersVO {


    /**
     * 用户名 用户名
     */
    private String username;


    /**
     * 昵称 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 头像
     */
    private String face;

    /**
     * 性别 性别 1:男  0:女  2:保密
     */
    private Integer sex;


    private String userUniqueToken;

}
