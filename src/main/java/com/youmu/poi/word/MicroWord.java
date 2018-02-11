package com.youmu.poi.word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.ReflectionUtils;

import com.google.errorprone.annotations.MustBeClosed;
import com.youmu.common.Loggable;

import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateNameFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/01/05
 */
public class MicroWord implements Loggable {

    private static final String ENTRY_DOC = "word/document.xml";

    private static final long TIME_UPDATE_CACHE = 5000;

    private Template docTemplate;

    private String templateName;

    private String templateFilePath;

    private static final Lock freemarkerTemplateLock = new ReentrantLock();

    private static Configuration configuration = new Configuration(
            Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    private static final Method normalizeNameMethod;
    static {
        configuration.setTemplateLoader(new StringTemplateLoader());
        configuration.setOutputEncoding("UTF-8");
        configuration.setEncoding(configuration.getLocale(), "UTF-8");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateUpdateDelayMilliseconds(TIME_UPDATE_CACHE);
        normalizeNameMethod = ReflectionUtils.findMethod(TemplateNameFormat.class,
                "normalizeAbsoluteName", String.class);
        if (null != normalizeNameMethod && !normalizeNameMethod.isAccessible()) {
            normalizeNameMethod.setAccessible(true);
        }
    }

    public MicroWord(String templateFilePath, String templateName) {
        this.templateName = templateName;
        this.templateFilePath = templateFilePath;
        try {
            initTemplate(templateFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * currently support XWPFDocument only need close on outside
     */
    @SuppressWarnings("non-close-resource")
    @MustBeClosed
    public POIXMLDocument process(Object model)
            throws IOException, TemplateException, InvalidFormatException {
        XWPFDocument xwpfDocument;
        InputStream docInputStream = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
                ZipFile zipFile = new ZipFile(templateFilePath)) {
            ZipEntry document = zipFile.getEntry(ENTRY_DOC);
            // init ZipOutputStream
            zipOutputStream.setComment(zipFile.getComment());
            // pick out doc xml
            docInputStream = zipFile.getInputStream(document);
            // copy entries to new file
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                // deal document body only current version
                if (!ENTRY_DOC.equals(zipEntry.getName())) {
                    zipOutputStream.putNextEntry(new ZipEntry(zipEntry.getName()));
                    getLog().debug(">>>>>>>>>>>>>>>>>zipping file :{}", zipEntry.getName());
                    try (InputStream entryInputStream = zipFile.getInputStream(zipEntry)) {
                        IOUtils.copy(entryInputStream, zipOutputStream);
                    }
                    zipOutputStream.closeEntry();
                }
            }
            // write doc entry to stream
            zipOutputStream.putNextEntry(new ZipEntry(document.getName()));
            try (Writer out = new OutputStreamWriter(zipOutputStream,
                    configuration.getOutputEncoding())) {
                docTemplate.process(model, out);
                getLog().debug(">>>>>>>>>>>>>>>>>zipping file document");
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    byteArrayOutputStream.toByteArray());
            xwpfDocument = new XWPFDocument(OPCPackage.open(byteArrayInputStream));
        } finally {
            if (null != docInputStream) {
                docInputStream.close();
            }
        }
        return xwpfDocument;
    }

    // 暂时不做hotswap template
    private void initTemplate(String templateFilePath) throws IOException {
        InputStream docInputStream = null;
        try (ZipFile zipFile = new ZipFile(templateFilePath);) {
            ZipEntry document = zipFile.getEntry(ENTRY_DOC);
            // pick out doc xml
            docInputStream = zipFile.getInputStream(document);
            // generate doc by freemarker
            String normalizedTemplateName = getTemplateName(this.templateName);
            try {
                this.docTemplate = configuration.getTemplate(templateName,
                        configuration.getEncoding(configuration.getLocale()));
            } catch (FileNotFoundException e) {
                freemarkerTemplateLock.lock();
                try {
                    StringTemplateLoader templateLoader = (StringTemplateLoader) configuration
                            .getTemplateLoader();
                    String t = IOUtils.toString(docInputStream,
                            configuration.getEncoding(configuration.getLocale()));
                    templateLoader.putTemplate(normalizedTemplateName, t);
                    getLog().debug(">>>>>>>>>template>>>>>>>>>>>>>{}", t);
                } finally {
                    freemarkerTemplateLock.unlock();
                }
                configuration.setTemplateUpdateDelayMilliseconds(0);
                this.docTemplate = configuration.getTemplate(templateName,
                        configuration.getEncoding(configuration.getLocale()));
                configuration.setTemplateUpdateDelayMilliseconds(TIME_UPDATE_CACHE);
            }
        } finally {
            if (null != docInputStream) {
                docInputStream.close();
            }
        }
    }

    public static void setEncode(String encode) {
        configuration.setOutputEncoding(encode);
        configuration.setEncoding(configuration.getLocale(), encode);
        configuration.setDefaultEncoding(encode);
        configuration.clearTemplateCache();
    }

    public static String getTemplateName(String templateFile) {
        String templateName;
        try {
            templateName = (String) normalizeNameMethod
                    .invoke(configuration.getTemplateNameFormat(), templateFile);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnknownError("生成模板名字出错");
        }
        return templateName;
    }
}
