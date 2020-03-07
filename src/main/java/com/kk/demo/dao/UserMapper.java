package com.kk.demo.dao;

import com.kk.demo.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    int insert(@Param("user") User user);
}