package com.youmu;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/11/21
 */
public class SerTest {

    static class A implements Serializable {

        private static final long serialVersionUID = 5626183536342511789L;

        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        }

        private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
        }

        private void readObjectNoData() throws ObjectStreamException {
        }
    }
}
