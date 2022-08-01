package com.youmu;

public class VolatileTest extends Thread {
    private static boolean flag = false;
    private static boolean flag1 = false;
    private static char padding1 = '0';
    private static long padding2 = '0';
    private static long padding3 = '0';
    private static long padding4 = '0';
    private static long padding5 = '0';
    private static long padding6 = '0';
    private static long padding7 = '0';
    private static volatile long padding8 = '0';
    private Object o = new Object();


    public void run() {
        int i = 0;

        boolean temp = flag;
        for (int j = 0; j < 100000; j++) {
            while (!flag) {
                temp = flag;
//                padding8 = 1;
                padding6 = 1;
                ;
            }
        }
    }
//-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly
    public static void main(String[] args) throws Exception {
        new VolatileTest().start();
        Thread.sleep(100);
        for (int j = 0; j < 100000; j++) {
            VolatileTest.flag = true;//movb   $0x1,0xa2(%r8) 0x76b6bff08 0x76b6bff08
//            padding7 = 1;
//            System.out.println(VolatileTest.flag);
        }
        System.out.println(padding6);

    }
}
