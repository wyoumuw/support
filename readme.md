# 常用工具

### com.youmu.utils.EnumUtils
这里是常用的枚举工具

### com.youmu.utils.ReflectUtils
这里提供一些与spring不一样的工具


## spring-cache-support
让spring的cache获得过期属性。(注，spring cache人需要自信配置，此缓存依赖spring cache)

默认实现对spring-data-redis的RedisCacheManager进行过期行为控制。
配置方式

xml

    <bean class="com.youmu.cache.CustomableCacheAdvisor">
            <property name="cacheAnnotationHandler">
                <!--这里实现自己的过期处理器-->
                <bean class="com.youmu.cache.DefaultRedisCacheAnnotationHandler">
                    <property name="redisCacheManager" ref="redisCacheManager"/>
                </bean>
            </property>
    </bean>
    <!--这里是开启spring的cache,若不开启上面的配置是无效的-->
    <cache:annotation-driven cache-manager="redisCacheManager"/>

code

     @SpringBootApplication
     //这个是开启默认对RedisCacheManager的配置,不启用则自行配置CacheAnnotationHandler
     @EnableRedisExpireCache
     //开启过期配置,就算被删除了也不会对原有的springcache有影响，只是配置了的过期不生效
     @EnableExpireableCache
     //开启spring的cache
     @EnableCaching
     public class Main {
     
     	public static void main(String[] args) {
     		SpringApplication springApplication =new SpringApplication();
     		springApplication.run(Main.class,args);
     	}
     }
     
使用

        @Service
        public class MyService {
        
            //这里使用过期，10秒
        	@Expireable(expire = 10)
        	@Cacheable("user")
        	public User user() throws InterruptedException {
        		System.out.println("check start");
        		Thread.sleep(1000);
        		System.out.println("check end");
        		return new User("youmu","123456");
        	}
        	
        	//这里不使用过期，会使用redisCacheManager的defaultExpire
            @Cacheable("user2")
            public User user2() throws InterruptedException {
                System.out.println("check start");
                Thread.sleep(1000);
                System.out.println("check end");
                return new User("youmu","123456");
            }
        }