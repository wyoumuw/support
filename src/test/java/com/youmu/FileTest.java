package com.youmu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.youmu.poi.word.MicroWord;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipFile;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/01/06
 */
public class FileTest {

    @Test
    public void readZipFile()
            throws IOException, TemplateException, InvalidFormatException, ClassNotFoundException {
        Class.forName("com.youmu.poi.word.MicroWord");
        MicroWord microWord = new MicroWord("E:\\word\\aapp.zip", "aapp.zip");
        try (XWPFDocument xwpfDocument = (XWPFDocument) microWord.process(
                ImmutableMap.of("based", ImmutableMap.of("scoreItems", Lists.newArrayList()),
                        "textItems", Lists.newArrayList()));
                OutputStream outputStream = new FileOutputStream("E://klajdlkad.docx");) {
            xwpfDocument.write(outputStream);
        }
    }
}
