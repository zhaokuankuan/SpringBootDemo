package com.kk.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :Mr.kk
 * @date: 2020/3/2 15:44
 */
@RestController
public class TestController {


    @GetMapping("/testSpringBootDemo")
    public String testSpringBootDemo(){
        return "I am a test";
    }
}
