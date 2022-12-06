package com.youmu.support.mybatis.generator;

import org.mybatis.generator.api.dom.xml.Document;

public class TextDocument extends Document {

    public String content;

    public TextDocument(String content) {
        this.content = content;
    }

    @Override
    public String getFormattedContent() {
        return content;
    }
}