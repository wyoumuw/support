# 常用工具

### com.youmu.utils.EnumUtils
这里是常用的枚举工具

### com.youmu.utils.ReflectUtils
这里提供一些与spring不一样的工具


## spring-cache-support
让spring的cache获得过期属性。(注，spring cache人需要自信配置，此缓存依赖spring cache)

默认实现对spring-data-redis的RedisCacheManager进行过期行为控制。
配置方式

code


    // 其实只需要开启下面的注解并且配置一个redisCacheManager即可
    @Configuration
    //这个是开启默认对RedisCacheManager的配置,不启用则自行配置CacheAnnotationHandler
    @EnableRedisExpireCache
    //开启过期配置,就算被删除了也不会对原有的springcache有影响，只是配置了的过期不生效
    @EnableExpireableCache
    //开启spring的cache
    @EnableCaching
    public class CacheConfig implements CachingConfigurer, Loggable {
    
        @Value("${redis.hostName}")
        private String hostName;
    
        @Value("${redis.port}")
        private int port;
    
        @Value("${redis.ttl}")
        private int ttl;
    
        @Value("${redis.password}")
        private String password;
    
        @Value("${redis.key-prefix}")
        private String keyPrefix;
    
        @Bean
        public RedisStandaloneConfiguration redisStandaloneConfiguration() {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostName,
                    port);
            if (StringUtils.isNotBlank(password)) {
                configuration.setPassword(RedisPassword.of(password));
            }
            return configuration;
        }
    
        @Bean
        public RedisConnectionFactory redisConnectionFactory(
                RedisStandaloneConfiguration redisStandaloneConfiguration) {
            return new JedisConnectionFactory(redisStandaloneConfiguration);
        }
    
        @Bean
        public GenericJackson2JsonRedisSerializer redisValueSerializer() {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            // long输出成string，不然会出现返回值是long的时候反序列化错误
            simpleModule.addSerializer(Long.class, new ToStringSerializer());
            simpleModule.addSerializer(long.class, new ToStringSerializer());
            objectMapper.registerModule(simpleModule);
            // 开启@class
            objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY);
            GenericJackson2JsonRedisSerializer redisValueSerializer = new GenericJackson2JsonRedisSerializer(
                    objectMapper);
            return redisValueSerializer;
        }
    
        @Primary
        @Bean
        public RedisTemplate<String, Object> redisTemplate(
                RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(redisValueSerializer());
            return template;
        }
    
        @Bean
        public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                GenericJackson2JsonRedisSerializer redisValueSerializer) {
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofSeconds(ttl))
                            .computePrefixWith(cacheName -> keyPrefix + ":" + cacheName + ":")
                            .serializeValuesWith(RedisSerializationContext.SerializationPair
                                    .fromSerializer(redisValueSerializer)))
                    .build();
        }
    
        @Override
        public CacheManager cacheManager() {
            return null;
        }
    
        @Override
        public CacheResolver cacheResolver() {
            return null;
        }
    
        @Override
        public KeyGenerator keyGenerator() {
            return new JsonKeyGenerator();
        }
    
        @Override
        public CacheErrorHandler errorHandler() {
            return cacheErrorHandler();
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
        
        
## Service-Invoker
此invoker是模仿springcloud的调用方式进行远程服务开发，例如项目现在是单项目的，并且项目已经很大了，一下子切不到springcloud，需要一个过渡过程则可以使用这个东西。
他的调用方式默认是httpclient，大家都可以自行配置，请将此项目认为是进行了一次http远程调用，开发者可以自行配置其调用。

### 项目架构

...

### 配置


服务端

    //mvc配置 引入MvcSupportConfig配置来让spring mvc支持接口参数注解
    @Import(MvcSuopprtConfig.class)
    public class MvcConfig implements WebMvcConfigurer {}
    ------------------------
    //service接口
    @FeignClient
    @RequestMapping
    public class UserService{
        @RequestMapping("/{id}")
        UserModel getById(@PathVariable("id") String id);
    }
    ------------------------
    //service实现
    @RestController
    public class UserServiceImpl implements UserService {
        
        @Autowired
        private UserMapp           er userMapper;
    
        @Override
        public User getById(String id){
            return userMapper.getById(id);
        }
    }

客户端配置

    //配置接口扫描保证能扫描到UserService这个接口，
    //可以根据需求设置HttpClientFactory，在使用httpclient时并不会对其进行关闭所以劲量使用同一个client，
    //或者自行实现ServiceInvoker和ServiceInvokerFactory来自行扩展 ServiceScannerConfigure#setServiceInvokerFactoryName
    public class WebServiceConfig {
    
        @Bean
        public HttpClientFactory httpClientFactory() {
            return new DefaultHttpClientFactory();
        }
    
        @Bean
        public HttpServiceConfiguration serviceConfiguration(HttpClientFactory httpClientFactory) {
            HttpClientServiceConfiguration httpClientServiceConfiguration = new HttpClientServiceConfiguration();
            httpClientServiceConfiguration.setHttpClientFactory(httpClientFactory);
            httpClientServiceConfiguration.setServer("http://localhost:8081");
            return httpClientServiceConfiguration;
        }
    
        @Bean
        public ServiceScannerConfigure serviceScannerConfigure() {
            ServiceScannerConfigure serviceScannerConfigure = new ServiceScannerConfigure();
            serviceScannerConfigure.setBasePackage("com.youmu.webservice");
            serviceScannerConfigure.setServiceConfigurationName("serviceConfiguration");
            return serviceScannerConfigure;
        }
    }

or

    @Configuration
    @WebServiceScan(serviceConfigurationName = "serviceConfiguration", basePackage = "com.youmu.webservice")
    public class WebserviceConfig {
    
        @Bean
        public HttpClientFactory httpClientFactory() {
            return new DefaultHttpClientFactory();
        }
    
        @Bean
        public HttpServiceConfiguration serviceConfiguration(HttpClientFactory httpClientFactory) {
            HttpClientServiceConfiguration httpClientServiceConfiguration = new HttpClientServiceConfiguration();
            httpClientServiceConfiguration.setHttpClientFactory(httpClientFactory);
            httpClientServiceConfiguration.setServer("http://localhost:8081");
            return httpClientServiceConfiguration;
        }
    }

然后就可以
    
    //确保在spring容器内
    @Service
    public class ClientUserService{
        
        //
        @Autowired
        private UserService userService;
        
        public void sout(){
            System.out.println(userService.getById("123"));
        }
    }
    
目前支持的是:

1. 只允许单复杂对象传输
2. 不支持带有复杂对象或者body对象又带上其他参数，请把其他参数放入对象内
3. 需要在服务接口上标记上注解，目前支持@RequestParam @RequestHeader @ResponseBody,并且需要在@RequestParam和@RequestHeader设置value来指定参数名
4. 服务接口上要标记上@FeignClient来标识属于远程服务或者自定义扫描注解 HttpServiceScannerConfigure.webServiceAnnotations



无body型请求GET,DELETE,OPTIONS发送复杂对象时不允许附带@RequestBody或者自行实现，传输的时候会把复杂对象展开成参数(考虑url可能会过长问题)

非body型需要写consumes在@RequestMapping来指定传输的内容协议

#### 扩展

* HttpClientFactory: 自定义来生成每次请求所使用的httpClient(每次都会调用，如果其实现每次都返回新对象则每次调用方法都是使用新的httpClient
* HttpClientResponseHandler: 用来处理httpclient返回后的内容
* ServiceInvoker: 服务调用器，如果自己实现需要自己实现ServiceInvokerFactory，并且注入指定后才会正常工作，否则还是使用Default的
* ServiceInvokerFactory: 自定义ServiceInvoker时使用，用来生成ServiceInvoker


