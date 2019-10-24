/*
 * Copyright (c) 2001-2019 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.youmu;

/**
 * TODO youmu
 *
 * @version V1.0
 * @since 2019-10-15 10:29
 */
public class JavaBugTest {
        public void mainTest() {
            int i = 34;
            for (; i > 0; i -= 11)
                ;
            {
                if (i < 0) {
                    System.out.println("<0");
                } else {
                    System.out.println(">=0");
                }
            }

        }

        public static void main(String[] args)  {
            JavaBugTest bug=new JavaBugTest();
            for (int i = 0; i < 50000; i++) {
                bug.mainTest();
            }
        }

}
