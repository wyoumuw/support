package com.youmu;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author laidh
 * @version V1.0
 * @since 2019-08-27 17:14
 */
public class MybatisUtilsTest {

    Pattern pattern = Pattern.compile("`(\\w+)` (\\w+)\\(*[0-9]*\\)* [\\w ']+ '(.*)',");
    Pattern tablePattern = Pattern.compile("CREATE TABLE `(\\w+)`(.*\\s+)+COMMENT='(.*)';");
    String table = "";
    private Class clazz = null;

    @Test
    public void generateClass() {
        Matcher matcher = pattern.matcher(table);
        StringBuilder sb = new StringBuilder(1024);
        Matcher tableMatcher = tablePattern.matcher(table);
        tableMatcher.find();
        sb.append(
            "/**\n" + " * " + tableMatcher.group(3) + "po\n" + " *\n" + " * @author YOUMU\n" + " * @version V1.0\n"
                + " * @since " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + "\n" + " */\n");
        sb.append("@Setter\n" + "@Getter\n" + "@ToString\npublic class ")
            .append(underlineToCamel(tableMatcher.group(1))).append("PO {\n");
        while (matcher.find()) {
            String name = StringUtils.uncapitalize(underlineToCamel(matcher.group(1)));
            String type = matcher.group(2);
            String comment = matcher.group(3);
            type = typeChange(type);
            sb.append("    /**\n     * cnm\n     */\n    private $2 $1;\n\n".replace("cnm", comment).replace("$2", type)
                .replace("$1", name));
        }
        sb.append("}\n");
        System.out.println(sb);
    }

    private String typeChange(String type) {
        if ("bigint".equalsIgnoreCase(type)) {
            return "Long";
        }
        if ("varchar".equalsIgnoreCase(type)) {
            return "String";
        }
        if ("datetime".equalsIgnoreCase(type)) {
            return "Date";
        }
        if ("timestamp".equalsIgnoreCase(type)) {
            return "Date";
        }
        if ("char".equalsIgnoreCase(type)) {
            return "String";
        }
        if ("tinyint".equalsIgnoreCase(type)) {
            return "Integer";
        }
        if ("int".equalsIgnoreCase(type)) {
            return "Integer";
        }
        return type;
    }

    @Test
    public void generateResultMap() {
        StringBuilder sb = new StringBuilder(256);
        ReflectionUtils.doWithFields(clazz, f -> {
            if (f.getName().equalsIgnoreCase("id")) {
                sb.append("<id column=\"id\" property=\"id\" />\n");
            } else {
                String[] names = StringUtils.splitByCharacterTypeCamelCase(f.getName());
                for (int i = 0; i < names.length; i++) {
                    names[i] = names[i].toLowerCase();
                }
                sb.append("<result column=\"").append(StringUtils.join(names, "_")).append("\" property=\"")
                    .append(f.getName()).append("\" />\n");
            }
        });
        System.out.println(sb);
    }

    @Test
    public void allColumn() {
        List<String> columns = new ArrayList<>(128);
        ReflectionUtils.doWithFields(clazz, f -> {
            String[] names = StringUtils.splitByCharacterTypeCamelCase(f.getName());
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].toLowerCase();
            }
            columns.add("`" + StringUtils.join(names, "_") + "`");
        });
        System.out.println(StringUtils.join(columns, ","));
    }

    @Test
    public void allQueryCriteria() {
        String prefix = "queryPO.";
        StringBuilder sb = new StringBuilder(1024);
        sb.append("<trim prefix=\"where\" prefixOverrides=\"AND | OR\">\n");
        ReflectionUtils.doWithFields(clazz, f -> {
            String[] names = StringUtils.splitByCharacterTypeCamelCase(f.getName());
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].toLowerCase();
            }
            String name = StringUtils.join(names, "_");
            sb.append("<if test=\"null != ").append(prefix).append(f.getName()).append("\">\n").append("and ")
                .append(name).append(" = #{").append(prefix).append(f.getName()).append("}\n").append("</if>\n");
        });
        sb.append("</trim>\n");
        System.out.println(sb);
    }

    @Test
    public void insertSelective() {
        String prefix = "";
        StringBuilder sb = new StringBuilder(1024);
        StringBuilder sb2 = new StringBuilder(1024);
        String table = camelToUnderline(clazz.getSimpleName());
        sb.append("insert into ").append(table).append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >\n");
        sb2.append("\n<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" >\n");
        ReflectionUtils.doWithFields(clazz, f -> {
            String name = camelToUnderline(f.getName());
            sb.append("<if test=\"null != ").append(prefix).append(f.getName()).append("\">\n").append(name)
                .append(",\n").append("</if>\n");
            sb2.append("<if test=\"null != ").append(prefix).append(f.getName()).append("\">\n").append("#{")
                .append(prefix).append(f.getName()).append("}").append(",\n").append("</if>\n");
        });
        sb.append("</trim>\n");
        sb2.append("</trim>\n");
        System.out.println(sb.append(sb2));
    }

    @Test
    public void updateByPropSelective() {
        String prop = "id";
        StringBuilder sb = new StringBuilder(1024);
        String table = camelToUnderline(clazz.getSimpleName());
        sb.append("update ").append(table).append(" set \n");
        sb.append("<trim suffixOverrides=\",\" >\n");
        ReflectionUtils.doWithFields(clazz, f -> {
            String name = camelToUnderline(f.getName());
            if (f.getName().equals(prop)) {
                return;
            }
            sb.append("<if test=\"null != ").append(f.getName()).append("\">\n").append(name).append(" = #{")
                .append(f.getName()).append("}").append(",\n").append("</if>\n");
        });
        sb.append("</trim>\n");
        sb.append(" where ").append(camelToUnderline(prop)).append(" = #{").append(prop).append("}");
        System.out.println(sb);
    }

    static String camelToUnderline(String camel) {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(camel);
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].toLowerCase();
        }
        return StringUtils.join(names, "_");
    }

    static String underlineToCamel(String underline) {
        String[] names = StringUtils.split(underline, '_');
        for (int i = 0; i < names.length; i++) {
            names[i] = StringUtils.capitalize(names[i]);
        }
        return StringUtils.join(names);
    }

    public static void main(String[] args) {
        String str = "";
        str = str.replaceAll("`", "");
        String[] strs = StringUtils.split(str, ",");
        for (String s : strs) {
            System.out.println("#{" + StringUtils.uncapitalize(underlineToCamel(StringUtils.trim(s))) + "},");
        }
    }

    @Test
    public void fillPrepareStatement() {
        String preSql = "";
        String param = "";
        String[] paramArr = param.split(",");
        String[] sqlArr = preSql.split("\\?");
        List<String> sqls = Lists.newArrayList();
        int sqli = 0, parami = 0, i = 0;
        while (i < sqlArr.length) {

            sqls.add(sqlArr[i]);
            if (i < paramArr.length) {
                String s = paramArr[i];
                if (s.endsWith("(String)") || s.endsWith("(Timestamp)")) {
                    sqls.add("\"" + s.replaceAll("\\(\\w+\\)","").trim() + "\"");
                }else {
                    sqls.add(s.replaceAll("\\(\\w+\\)","").trim());
                }
            }
            i++;
        }
        System.out.println(StringUtils.join(sqls, ""));
    }

    @Test
    public void setAll() {
        String prefix = "";
        ReflectionUtils.doWithMethods(clazz, m -> {
            System.out.println(prefix + "." + m.getName() + "(null);");
        }, m -> m.getName().startsWith("set"));
    }

    public static void generateTest() {
        //        Class

    }

    @Test
    public void dynamicSqlTest() throws  Exception{
        Configuration configuration =new Configuration();
        //<script>里的东西替换成具体sql
        String script = "<script>select id from user where <if test='filter'>id=#{id}</if></script>";
        XPathParser parser = new XPathParser(script, false, configuration.getVariables(), new XMLMapperEntityResolver());
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, parser.evalNode("/script"), org.apache.ibatis.binding.MapperMethod.ParamMap.class);
        //构造参数，如果是对象那么param也换成对象
        Map<String,Object> param = new HashMap<>();
        param.put("id", 1L);
        param.put("filter", true);
        BoundSql boundSql = builder.parseScriptNode().getBoundSql(param);
        String sql = boundSql.getSql();
        System.out.println(sql);
        System.out.println("参数为");
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (ParameterMapping parameterMapping : parameterMappings) {
            System.out.println(parameterMapping.toString());
        }
    }
}
