
reids是一个高性能的缓存中间件,redis可以用作缓存来提高系统的系统，由于redis的本身的一些机制，redis也有很多的高级用法，具体的redis的搭建和使用场景上一篇文章已经讲述,
今天就记录下springboot在整合redis的时候的一些操作和客户端的选择。


>概念：

　　Jedis：是Redis的Java实现客户端，提供了比较全面的Redis命令的支持，

　　Redisson：实现了分布式和可扩展的Java数据结构。

　　Lettuce：高级Redis客户端，用于线程安全同步，异步和响应使用，支持集群，Sentinel，管道和编码器。

优点：

　　Jedis：比较全面的提供了Redis的操作特性

　　Redisson：促使使用者对Redis的关注分离，提供很多分布式相关操作服务，例如，分布式锁，分布式集合，可通过Redis支持延迟队列

　　Lettuce：主要在一些分布式缓存框架上使用比较多

可伸缩：

Jedis：使用阻塞的I/O，且其方法调用都是同步的，程序流需要等到sockets处理完I/O才能执行，不支持异步。Jedis客户端实例不是线程安全的，所以需要通过连接池来使用Jedis。

Redisson：基于Netty框架的事件驱动的通信层，其方法调用是异步的。Redisson的API是线程安全的，所以可以操作单个Redisson连接来完成各种操作

Lettuce：基于Netty框架的事件驱动的通信层，其方法调用是异步的。Lettuce的API是线程安全的，所以可以操作单个Lettuce连接来完成各种操作

在spring-boot-starter-data-redis中我们可以看到，在springboot2.0以后默认的将链接redis的客户端修改为了Luttuce，那么今天我们就记录下用Luttuce来进行redis的操作，还是在上一节已经创建好的工程的基础上进行搭建。
1.首先我们需要引入spring-boot-starter-data-redis的包用来集成redis，我们可以看到spring-boot-starter-data-redis的pom中引入了如下的包：
   <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
      <version>2.0.4.RELEASE</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.data</groupId>
      <artifactId>spring-data-redis</artifactId>
      <version>2.0.9.RELEASE</version>
      <scope>compile</scope>
      <exclusions>
        <exclusion>
          <artifactId>jcl-over-slf4j</artifactId>
          <groupId>org.slf4j</groupId>
        </exclusion>
      </exclusions>
    </dependency>
    <dependency>
      <groupId>io.lettuce</groupId>
      <artifactId>lettuce-core</artifactId>
      <version>5.0.4.RELEASE</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
  可以看到，里面引入了Lettuce包和spring-data-redis的包。
  2.接下来我们就需要修改application.yml文件，新增上我们的redis的配置信息：
  server:
  port: 8080
  servlet:
    context-path: /spring-boot-demo
logging:
  file: /home/kk/spring-boot-demo.log
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: adminadmin
    timeout: 3000ms
    database: 0
#    lettuce:
#      pool:
#        max-active: 10
#        max-idle: 8
#        max-wait: 3000ms
#        min-idle: 0
我这里注释掉了，链接池的配置因为在Lettuce中的pool已经给给出了默认值，大家可以根据需要自行配置
 public static class Pool {
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxActive = 8;
        private Duration maxWait = Duration.ofMillis(-1L);

        public Pool() {
        }
}

接下来我们放上一个工具类，这个是我之前一直用的一个，所以直接拷贝来，所有的地方都可以用：RedisUtils.java
package com.kk.demo.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author :Mr.kk
 * @date: 2018/12/10 14:24
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 存入一个字符串
     * @param key
     * @param value
     * @return
     */
    public boolean insertStrng(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 存入一个带失效时间的字符串
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public boolean setStringTime(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {

        }
        return result;
    }

    /**  查询一个key是否存在
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /** 根据key获取一个string字符串
     * @param key
     * @return
     */
    public String getString(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result==null?"":result.toString();
    }

    /** 删除一个string字符串
     * @param key
     */
    public void removeString(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /** 根据keys批量删除
     * batch delete
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     *  新增一个hash
     * @param key
     * @param hashKey
     * @param value
     */
    public void insertHash(String key, Object hashKey, Object value,long expireTime){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     *  删除一个hash中的key对应的数值
     */
    public void deleteHash(String key, Object hashKey){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.delete(key,hashKey);
    }

    /**
     *  新增一个hash不带失效时间
     * @param key
     * @param hashKey
     * @param value
     */
    public void insertHashNotime(String key, Object hashKey, Object value){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }

    /**
     * 查询一个hash
     * @param key
     * @param hashKey
     * @return
     */
    public Object getHash(String key, Object hashKey){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }

    /**
     *  设置一个list
     * @param k
     * @param v
     */
    public void insertList(String k,Object v){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }

    /**
     *  查询list，并且截取l到l1
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> getListByIndex(String k, long l, long l1){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }

    /**
     * 设置一个set
     * @param key
     * @param value
     */
    public void insertSet(String key,Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }

    /**
     * 获取set
     * @param key
     * @return
     */
    public Set<Object> getSetAll(String key){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 新增一个zset
     * @param key
     * @param value
     * @param scoure
     */
    public void insertZset(String key,Object value,double scoure){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key,value,scoure);
    }

    /**
     * 获取zset的数据
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> getZSetByIndex(String key,double scoure,double scoure1){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }
}

接下来我们需要配置redis调用时候的key和value的序列化方式，从源码中我们可以看出，默认的序列化方式为JdkSerializationRedisSerializer
会出现如下的问题，通过上线的代码我们知道了，是因为他的序列化方式的问题，因此我们需要修改redis的序列化方式为stringSerializer
RedisConfiguration.java
package com.kk.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author :Mr.kk
 * @date: 2018/12/10 14:23
 */
@Configuration
public class RedisConfiguration{

    /**
     * RedisTemplate配置
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        //定义key序列化方式
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();//Long类型会出现异常信息;需要我们上面的自定义key生成策略，一般没必要
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);
        template.setHashKeySerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }


}


至此，所有的配置工作就全部做完了，现在我们需要写几个接口进行测试了：

  @GetMapping("/insertRedis")
    public void insertRedis(String key,String value){
        redisUtils.insertStrng(key,value);
    }


    @GetMapping("/getRedis")
    public Object getRedis(String key){
        return redisUtils.getString(key);
    }
    
    
    
    然后启动工程
    先在浏览器中输入 http://127.0.0.1:8080/spring-boot-demo/insertRedis?key=aa&value=bb
    然后再输入 http://127.0.0.1:8080/spring-boot-demo/getRedis?key=aa 会看到返回的数据为bb  这就说明我们的存储和查询已经成功了。 

