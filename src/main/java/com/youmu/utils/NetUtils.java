package com.youmu.utils;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.youmu.exception.WebEnvException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @Author: YOUMU
 * @Description: 网络相关的工具
 * @Date: 2018/05/22
 */
public final class NetUtils {
    private NetUtils() {

    }

    /**
     * 从session里获取T类型的对象
     * @param request
     * @param name
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getAttrFromSession(HttpServletRequest request, String name) {
        return Optional.ofNullable((T) request.getSession().getAttribute(name));
    }

    /**
     * 设置值到session里
     * @param request
     * @param name
     * @param obj
     */
    public static void setAttrToSession(HttpServletRequest request, String name, Object obj) {
        request.getSession().setAttribute(name, obj);
    }

    /**
     * 拼接url和参数
     * @param baseUrl
     * @param params
     * @return
     */
    public static String makeUrl(String baseUrl, Map<String, String> params) {
        if (null == baseUrl) {
            throw new RuntimeException("illegal baseUrl");
        }
        if (MapUtils.isEmpty(params)) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    /**
     * Spring web环境下获取请求的方法 <b>需要再springweb环境下使用<b/>
     * @return
     * @throws WebEnvException
     */
    public static HttpServletRequest getRequest() throws WebEnvException {
        return (HttpServletRequest) Optional
                .ofNullable(RequestContextHolder.getRequestAttributes()
                        .resolveReference(RequestAttributes.REFERENCE_REQUEST))
                .orElseThrow(() -> new WebEnvException("not found web environment"));
    }
}
