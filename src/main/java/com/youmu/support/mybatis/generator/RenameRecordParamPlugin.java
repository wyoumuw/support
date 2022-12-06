package com.youmu.support.mybatis.generator;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;

public class RenameRecordParamPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        for (Method method : interfaze.getMethods()) {
            List<Parameter> parameters = method.getParameters();
            List<Parameter> parametersNew = new ArrayList<>();
            for (Parameter parameter : parameters) {
                if ("record".equals(parameter.getName())) {
                    Parameter parameterNew = new Parameter(parameter.getType(), JavaBeansUtil.getCamelCaseString(parameter.getType().getShortName(), false), parameter.isVarargs());
                    parameter.getAnnotations().forEach(parameterNew::addAnnotation);
                    parametersNew.add(parameterNew);
                }else {
                    parametersNew.add(parameter);
                }
            }
            method.getParameters().clear();
            method.getParameters().addAll(parametersNew);
        }
        return true;
    }
}
