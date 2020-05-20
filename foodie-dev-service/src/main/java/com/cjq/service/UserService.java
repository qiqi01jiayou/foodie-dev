package com.cjq.service;

import com.cjq.pojo.Stu;
import com.cjq.pojo.Users;
import com.cjq.pojo.bo.UsersBO;

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

    /**
     * 创建用户
     *
     * @param usersBO
     * @return
     */
    Users createUser(UsersBO usersBO);

    /**
     * 检索用户名和密码是否匹配
     * @param username
     * @param password
     * @return
     */
    Users queryUserForLogin(String username, String password);
}
