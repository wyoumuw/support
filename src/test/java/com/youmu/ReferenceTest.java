package com.youmu;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class ReferenceTest {

    @Test
    public void phantomTest() throws Exception {
        ReferenceQueue q = new ReferenceQueue();
        PhantomReference<Object> obj = new PhantomReference<>(new Object(), q);
        new Thread(() -> {
            while (true) {
                Reference poll = q.poll();
                if (poll != null) {

                    System.out.println("回收--" + poll.get());
                }
            }
        }).start();
        while (true) {
            byte[] bytes = new byte[1024 * 1024];
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }


    @Test
    public void Test() throws Exception {
        ReferenceQueue<Connection> referenceQueue = new ReferenceQueue<>();
        Reference<Connection> reference = new WeakReference<>(new Connection(), referenceQueue);
        reference = null;
        while (true) {
            Reference<? extends Connection> poll = referenceQueue.poll();
            if (null != poll) {
                System.out.println(poll.get());
            }
            byte[] bytes = new byte[1024];
        }
    }

    static class Connection implements Closeable {
        @Override
        public void close() throws IOException {
            System.out.println("closed");
        }
    }
}
