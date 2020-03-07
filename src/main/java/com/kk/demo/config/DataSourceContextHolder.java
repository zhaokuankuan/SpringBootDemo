package com.kk.demo.config;

/**
 * @author :Mr.kk
 * @date: 2020/3/7 9:57
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    // 设置数据源名
    public static void setDataSourceKey(String dbName) {
        contextHolder.set(dbName);
    }

    // 获取数据源名
    public static String getDataSourceKey() {
        return (contextHolder.get());
    }

    // 清除数据源名
    public static void clearDataSourceKey() {
        contextHolder.remove();
    }
}