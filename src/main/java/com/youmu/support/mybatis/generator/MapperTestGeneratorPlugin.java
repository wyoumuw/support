package com.youmu.support.mybatis.generator;

import com.youmu.utils.MybatisUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.util.StringBuilderWriter;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MapperTestGeneratorPlugin extends PluginAdapter {
    static {
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return false;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("table", introspectedTable);
        velocityContext.put("superType", new FullyQualifiedJavaType("com.youmu.maven.BaseTest"));
        velocityContext.put("baseType", new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        velocityContext.put("mapperType", mapperType);
        velocityContext.put("MybatisUtils", MybatisUtils.class);
        StringBuilderWriter writer = new StringBuilderWriter();
        Template template = Velocity.getTemplate("templates/mapper-test.ft", "utf-8");
        template.merge(velocityContext, writer);
        GeneratedXmlFile generatedXmlFile = new GeneratedXmlFile(new TextDocument(writer.getBuilder().toString()), mapperType.getShortName() + "Test.java", mapperType.getPackageName(), "src/test/java", false, context.getXmlFormatter());
        return Collections.singletonList(generatedXmlFile);
    }
}
