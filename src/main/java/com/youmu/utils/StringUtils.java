package com.youmu.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

/**
 * Created by wyoumuw on 2017/3/28.
 */
public abstract class StringUtils {
    /**
     * 获得field的get方法名字
     * @param field
     * @return 如果field是空则返回field否则返回get方法名
     */
    public static String toGetterMethodName(String field){
        if(isEmpty(field)){
            return field;
        }
        return new StringBuilder(3+field.length()).append("get").append(capitalize(field)).toString();
    }

    /**
     * 获得field的set方法名字
     * @param field
     * @return 如果field是空则返回field否则返回set方法名
     */
    public static String toSetterMethodName(String field){
        if(isEmpty(field)){
            return field;
        }
        return new StringBuilder(3+field.length()).append("set").append(capitalize(field)).toString();
    }

    /**
     * 判空null或者""
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str){
        return str==null||str.length()==0;
    }
    /**
     * 引用于spring-core的函数
     * @param str
     * @return
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }
        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    /**
     *
     * @param strings
     * @param splitor default(,)
     * @return
     */
    private static final CharSequence defaultSplitor=",";

    public static String join(String[] strings,CharSequence splitor ){
        if(ArrayUtils.isEmpty(strings)){
            return "";
        }
        if(StringUtils.isEmpty(splitor)){
            splitor=defaultSplitor;
        }
        StringBuilder stringBuilder=new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string).append(splitor);
        }
        return stringBuilder.toString().substring(0,stringBuilder.length()-splitor.length());
    }

    private static final char[] base = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPQqRrSsTtUuVvWwXYxyZz0123456789".toCharArray();

    /**
     * generate nonce string
     * @param len
     * @return
     */
    public static String generateNonceStr(int len){
        byte[] bytes=new byte[len];
        new Random().nextBytes(bytes);
        int baseLen=base.length;
        char[] out=new char[len];
        for (int i=0;i<len;i++){
            out[i]=base[(bytes[i]>>>1)%baseLen];
        }
        return String.valueOf(out);
    }
}
