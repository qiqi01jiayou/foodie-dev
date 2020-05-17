package com.cjq.service;

import com.cjq.pojo.Stu;

import java.util.List;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
public interface UserService {
    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    boolean queryUserNameIsExists(String username);
}
