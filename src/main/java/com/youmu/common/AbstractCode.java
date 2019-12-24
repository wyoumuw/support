package com.youmu.common;

import com.youmu.utils.EnumUtils;

/**
 * @Author: YOUMU
 * @Description: 枚举的抽象类，可以配合{@link EnumUtils#get(java.lang.Class, int)}快速使用<br/>
 *               例如有枚举： <code>
 * enum MyEnum implements AbstractCode {
 *      A(1), B(2);
 *      private final int code;
 *      MyEnum(int code) {
 *        this.code = code;
 *      }
 *      public int getCode() {
 *        return code;
 *     }
 *  }
 * </code> <br/>
 *               你可以使用，类似于<br/>
 *               <code>
 *    MyEnum a EnumUtils.get(MyEnum.class,1);
 *    a == A// true
 * </code>
 * @Date: 2017/07/30
 */
public interface AbstractCode {

    /**
     * 自定义枚举的code，避免
     * @return code
     */
    int getCode();
    // 考虑是不是所有枚举都需要
    // String getDesc();
}
