package com.youmu.support.spring.serviceinvoker;

import java.net.SocketTimeoutException;

import com.youmu.common.Loggable;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;


/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/09/07
 */
public class DefaultHttpClientFactory implements HttpClientFactory, Loggable, DisposableBean {

    public static final String UTF8 = "UTF-8";
    public volatile boolean isClosed = false;

    public static final int MAX_TOTAL_POOL = 150;
    public static final int MAX_TIMEOUT = 10000;
    public static final int REQUEST_TIMEOUT = 10000;

    private HttpClientBuilder httpClientBuilder;
    private PoolingHttpClientConnectionManager poolConnManager;

    public DefaultHttpClientFactory() {
        // 设置连接池
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(MAX_TOTAL_POOL);// 设置连接池大小
        poolConnManager.setDefaultMaxPerRoute(MAX_TOTAL_POOL);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(REQUEST_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        // configBuilder.setStaleConnectionCheckEnabled(true);
        RequestConfig requestConfig = configBuilder.build();
        //
        httpClientBuilder = HttpClients.custom().setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig).setRetryHandler((e, count, context) -> {
                    if (count > 3) {
                        getLog().warn("Maximum tries reached for client http pool ");
                        return false;
                    }

                    if (e instanceof NoHttpResponseException // NoHttpResponseException 重试
                            || e instanceof ConnectTimeoutException // 连接超时重试
                            || e instanceof SocketTimeoutException // 响应超时不重试，避免造成业务数据不一致
                    ) {
                        getLog().warn("NoHttpResponseException on " + count + " call");
                        return true;
                    }
                    return false;
                });
    }

    public DefaultHttpClientFactory(RequestConfig requestConfig) {
        poolConnManager = new PoolingHttpClientConnectionManager();
        poolConnManager.setMaxTotal(MAX_TOTAL_POOL);// 设置连接池大小
        poolConnManager.setDefaultMaxPerRoute(MAX_TOTAL_POOL);
        httpClientBuilder = HttpClients.custom().setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig).setRetryHandler((e, count, context) -> {
                    if (count > 3) {
                        getLog().warn("Maximum tries reached for client http pool ");
                        return false;
                    }

                    if (e instanceof NoHttpResponseException // NoHttpResponseException 重试
                            || e instanceof ConnectTimeoutException // 连接超时重试
                            || e instanceof SocketTimeoutException // 响应超时不重试，避免造成业务数据不一致
                    ) {
                        getLog().warn("NoHttpResponseException on " + count + " call");
                        return true;
                    }
                    return false;
                });
    }

    public DefaultHttpClientFactory(RequestConfig requestConfig,
            PoolingHttpClientConnectionManager poolConnManager) {
        this(HttpClients.custom().setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig));
    }

    public DefaultHttpClientFactory(HttpClientBuilder httpClientBuilder) {
        this.httpClientBuilder = httpClientBuilder;
    }

    @Override
    public CloseableHttpClient get() {
        CloseableHttpClient httpClient = httpClientBuilder.build();
        if (null == httpClient) {
            getLog().info("---------HttpClients.createDefault()---------");
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    @Override
    public void destroy() throws Exception {
        if (!isClosed) {
            isClosed = true;
            poolConnManager.close();
        }
    }
}
