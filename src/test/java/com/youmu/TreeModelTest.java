package com.youmu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.Test;

import java.util.List;

/**
 * Created by wyoumuw on 2018/7/27.
 */
public class TreeModelTest {

    @Data
   public class Permission {
        private int id;
        private int pid;

        public Permission() {
        }

        public Permission(int id, int pid) {
            this.id = id;
            this.pid = pid;
        }
   }
    @Data
   public static class PermissionModel {
        private int id;
        private List<PermissionModel> perms;


    }

    private List<PermissionModel> treeify(List<Permission> perms, final int pid) {
        List<PermissionModel> list = Lists.newArrayList();
        for (Permission perm : perms) {
            if (pid == perm.pid) {
                PermissionModel parent = new PermissionModel();
                parent.id = perm.id;
                parent.perms = treeify(perms, perm.id);
                list.add(parent);
            }
        }
        return list;
    }

    @Test
    public void treeTest() throws JsonProcessingException {
        List<Permission> list = Lists.newArrayList(new Permission(1, 0), new Permission(2, 1), new Permission(3, 1));
        System.out.println(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(treeify(list, 0)));
    }
}
