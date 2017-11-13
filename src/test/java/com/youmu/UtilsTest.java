package com.youmu;

import org.junit.Test;

import com.youmu.utils.EnumUtils;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class UtilsTest {
    /******************* Enum test *************************/
    @Test
    public void enumTest() {
        System.out.println("EnumUtils.getByProperty(404):"+EnumUtils.getByProperty(HttpCode.class, 404, HttpCode::getCode));
		System.out.println("EnumUtils.getByProperty(500):"+EnumUtils.getByProperty(HttpCode.class, 500, HttpCode::getCode));
		System.out.println("EnumUtils.get(404):"+EnumUtils.get(HttpCode.class, 404, (e,ele)->e.getCode()==ele.intValue()));
		System.out.println("EnumUtils.get(500):"+EnumUtils.get(HttpCode.class, 500, (e,ele)->e.getCode()==ele.intValue()));
		System.out.println("EnumUtils.contains(404):"+EnumUtils.contains(HttpCode.class, 404, (e,ele)->e.getCode()==ele.intValue()));
		System.out.println("EnumUtils.contains(500):"+EnumUtils.contains(HttpCode.class, 500, (e,ele)->e.getCode()==ele.intValue()));
    }
    enum HttpCode {
        CODE_404(404, "not found page"), CODE_200(200, "success");
        private int code;
        private String desc;
        HttpCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }
    /******************************ReflectUtils test****************************/

}
