package com.kk.demo.service;

import com.kk.demo.dao.UserMapper;
import com.kk.demo.domain.DataSource;
import com.kk.demo.domain.DataSourceNames;
import com.kk.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TestJtaServiceImpl implements TestJtaService {

    @Autowired
    UserMapper userMapper;


    public Object testInsertUser(User user){
        int insertNum = userMapper.insert(user);
        System.out.println("插入成功1,条数："+insertNum);
        return insertNum;
    }

    @DataSource(value = DataSourceNames.test)
    public Object testInsertUser2(User user){
        int insertNum = userMapper.insert(user);
        System.out.println("插入成功2,条数："+insertNum);
        return insertNum;
    }

}