package com.youmu.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/08/09
 */
public interface Loggable {
    public default Logger getLog() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
