package com.youmu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.youmu.poi.word.MicroWord;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipFile;

/**
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

    @Test
    public void htmlPdf() throws Exception {
        File html = new File("C:/Users/ucmed/Desktop/test.html");
        File pdf = new File("C:\\Users\\ucmed\\Desktop\\test.pdf");

        // 第一步，创建一个 iTextSharp.text.Document对象的实例：
        Document document = new Document();
        // 第二步，为该Document创建一个Writer实例：
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
        // 第三步，打开当前Document
        document.open();
        // 第四步，为当前Document添加内容：
        // document.add(new Paragraph("Hello World"));
        Chunk.NEWLINE.append("").insert(0,'\r');
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(html),
                Charset.forName("UTF-8"));
        // 第五步，关闭Document
        document.close();
        System.out.println("OK!");
    }

    public static void main(String[] args) throws Exception {
        new FileTest().htmlPdf();
    }
}
