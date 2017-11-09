package com.youmu.cache;

import org.aopalliance.intercept.MethodInvocation;

import com.youmu.cache.annotation.Expireable;


/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/08
 */
public interface CacheAnnotationHandler {
    HandleResult handle(Expireable expireable, MethodInvocation method);

    class HandleResult {

        public final static HandleResult CONTINUE = new HandleResult(true, null);

        private boolean doChain;
        private Object rtnVal;

        private HandleResult(boolean doChain, Object rtnVal) {
            this.doChain = doChain;
            this.rtnVal = rtnVal;
        }

        public boolean getDoChain() {
            return doChain;
        }

        public Object getRtnVal() {
            return rtnVal;
        }

        public static HandleResult of(boolean doChain, Object rtnVal) {
            return new HandleResult(doChain, rtnVal);
        }
    }
}
