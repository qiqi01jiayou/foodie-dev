package com.cjq.service.impl;

import com.cjq.enums.Sex;
import com.cjq.mapper.UsersMapper;
import com.cjq.pojo.Users;
import com.cjq.pojo.bo.UsersBO;
import com.cjq.service.UserService;
import com.cjq.utils.DateUtil;
import com.cjq.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 被狗追过的夏天
 * date 2020-05-17
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNameIsExists(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        Users users = usersMapper.selectOneByExample(userExample);
        return users != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UsersBO usersBO) {
        String userid = sid.nextShort();
        Users users = new Users();
        users.setId(userid);
        String usernmame = usersBO.getUsername();
        users.setUsername(usernmame);
        try {
            users.setPassword(MD5Utils.getMD5Str(usersBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认
        users.setNickname(usernmame);
        // 默认头像
        users.setFace(USER_FACE);
        // 默认生日
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        // 默认性别为 保密
        users.setSex(Sex.secret.type);
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());
        usersMapper.insert(users);
        users = setNullProperty(users);
        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        try {
            criteria.andEqualTo("password", MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Users users = usersMapper.selectOneByExample(userExample);
        if(users!=null){
            users = setNullProperty(users);
        }
        return users;
    }


    //屏蔽数据
    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
