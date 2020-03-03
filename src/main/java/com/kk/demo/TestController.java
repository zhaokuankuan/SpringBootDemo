package com.kk.demo;

import com.kk.demo.common.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :Mr.kk
 * @date: 2020/3/2 15:44
 */
@RestController
public class TestController {

    @Autowired
    RedisUtils redisUtils;


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
}
