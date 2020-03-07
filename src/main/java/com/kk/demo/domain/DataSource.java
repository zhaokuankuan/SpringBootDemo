package com.kk.demo.domain;

import java.lang.annotation.*;

/**
 * @author :Mr.kk
 * @date: 2020/3/7 9:54
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String value() default DataSourceNames.iemp;
}