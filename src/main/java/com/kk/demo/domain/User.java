package com.kk.demo.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * @author :Mr.kk
 * @date: 2020/3/7 10:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
    private Integer id;
    private String username;
    private Integer age;


}