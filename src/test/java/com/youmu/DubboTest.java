package com.youmu;

import com.youmu.support.TestService;
import com.youmu.support.TestService2;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.builders.ApplicationBuilder;
import org.apache.dubbo.config.bootstrap.builders.ProviderBuilder;
import org.apache.dubbo.config.bootstrap.builders.ReferenceBuilder;
import org.apache.dubbo.config.bootstrap.builders.ServiceBuilder;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.FrameworkModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

public class DubboTest {

    @Test
    public void Test() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setPort(2181);
        registryConfig.setAddress("127.0.0.1");
        ApplicationModel applicationModel = FrameworkModel.defaultModel().newApplication();
        ApplicationConfig applicationConfig = new ApplicationBuilder().name("youmu-provider").qosPort(44444).build();
        applicationModel.getApplicationConfigManager().addConfig(applicationConfig);
        ServiceConfig<Object> build = ServiceBuilder.newBuilder().interfaceClass(TestService.class).ref(new TestServiceImpl()).application(applicationConfig).addRegistry(registryConfig).build();
        build.setScopeModel(applicationModel.getDefaultModule());
        build.export();
        ServiceConfig<Object> build2 = ServiceBuilder.newBuilder().interfaceClass(TestService2.class).ref(new TestService2Impl()).application(applicationConfig).addRegistry(registryConfig).build();
        build2.setScopeModel(applicationModel.getDefaultModule());
        build2.export();


        new CyclicBarrier(2).await();
    }

    @Test
    public void provider2() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setPort(2181);
        registryConfig.setAddress("127.0.0.1");
        ApplicationModel applicationModel = FrameworkModel.defaultModel().newApplication();
        ApplicationConfig applicationConfig = new ApplicationBuilder().name("youmu-provider").qosPort(55555).build();
        applicationModel.getApplicationConfigManager().addConfig(applicationConfig);
        applicationModel.getDefaultModule().getConfigManager().addProvider(new ProviderBuilder().port(28090).build());
        ServiceConfig<Object> build = ServiceBuilder.newBuilder().interfaceClass(TestService.class).ref(new TestServiceImpl()).application(applicationConfig).addRegistry(registryConfig).build();
        build.setScopeModel(applicationModel.getDefaultModule());
        build.export();
        ServiceConfig<Object> build2 = ServiceBuilder.newBuilder().interfaceClass(TestService2.class).ref(new TestService2Impl()).application(applicationConfig).addRegistry(registryConfig).build();
        build2.setScopeModel(applicationModel.getDefaultModule());
        build2.export();


        new CyclicBarrier(2).await();
    }

    @Test
    public void refer() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setPort(2181);
        registryConfig.setAddress("127.0.0.1");
        ApplicationModel applicationModel = FrameworkModel.defaultModel().newApplication();
        ApplicationConfig applicationConfig = new ApplicationBuilder().name("youmu-consumer").build();
        applicationModel.getApplicationConfigManager().addConfig(applicationConfig);
        ReferenceConfig<TestService> build = ReferenceBuilder.<TestService>newBuilder().interfaceClass(TestService.class).addRegistry(registryConfig).loadbalance("roundrobin").build();
        build.setScopeModel(applicationModel.getDefaultModule());
        TestService testService = build.get();
        System.out.println(testService.say("what"));
        System.out.println(testService.say("what"));
        System.out.println(testService.say("what"));
        new CyclicBarrier(2).await();
    }

    @Test
    public void Test33() throws Exception {
        String[] keys = new String[]{"1", "2", "3", "4", "5"};
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));
        System.out.println(select(keys));

    }

    @Test
    public void Testdd() throws  Exception{
    }

    static Map<String, Holder> map = new HashMap<>();

    private String select(String[] keys) {
        int maxC = Integer.MIN_VALUE, total = 0;
        Holder selected = null;
        for (String key : keys) {
            Holder h = map.computeIfAbsent(key, k -> {
                Holder holder = new Holder();
                holder.key = k;
                holder.current = 0;
                holder.weight = Integer.parseInt(key);
                return holder;
            });
            h.current += h.weight;
            if (h.current > maxC) {
                maxC = h.current;
                selected = h;
            }
            total += h.weight;
        }
        if (selected != null) {
            selected.current -= total;
            return selected.key;
        }
        return keys[0];
    }

    static class Holder {
        private String key;
        private int weight;
        private int current;
    }

    public static class TestServiceImpl implements TestService {


        @Override
        public String say(String what) {
            return "cnmd " + what;
        }
    }

    public static class TestService2Impl implements TestService2 {


        @Override
        public String say(String what) {
            return "cnmd2 " + what;
        }
    }
}
