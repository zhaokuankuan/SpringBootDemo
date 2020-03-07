package com.kk.demo;

import com.kk.demo.common.RedisUtils;
import com.kk.demo.domain.User;
import com.kk.demo.service.TestJtaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

/**
 * @author :Mr.kk
 * @date: 2020/3/2 15:44
 */
@RestController
public class TestController {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TestJtaService testJtaService;


    @GetMapping("/testSpringBootDemo")
    public String testSpringBootDemo(){
        return "I am a test";
    }


    @GetMapping("/insertRedis")
    public void insertRedis(String key,String value){
        redisUtils.insertStrng(key,value);
    }


    @GetMapping("/getRedis")
    public Object getRedis(String key){
        return redisUtils.getString(key);
    }

    @PostMapping("/insertOne")
    public Object insertOne(@RequestBody User user){
        return testJtaService.testInsertUser(user);
    }

    @PostMapping("/insertTwo")
    @Transactional
    public Object insertTwo(@RequestBody User user){
        Object res = testJtaService.testInsertUser2(user);
        return res;
    }

    @PostMapping("/testRollBack")
    public Object testRollBack(@RequestBody User user){
        Object res = testJtaService.testInsertUser2(user);
        Object res1 = testJtaService.testInsertUser(user);
        return res;
    }



}
