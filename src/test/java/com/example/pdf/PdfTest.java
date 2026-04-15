package com.example.pdf;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.docx4j.wml.Tr;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class
PdfTest {

    private final String basePath = "D:/WorkSpace/Idea/CRS/crs-ca-server/crs-ca-rest/src/test/java/com/crcgas/pdf";

    public String getFontPath() {
        String fontFileName = "simhei.ttf";
        return basePath + "/font/" + fontFileName;
    }

    //    @Test
    public void testPdf() throws IOException {
        // 1. 初始化PDF文档
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        // 2. 加载字体文件（替换为你的字体路径）
        String fontPath = getFontPath();
        File fontFile = new File(fontPath);
        // PDType0Font 自动支持子集化（仅嵌入使用的字符）
        PDType0Font customFont = PDType0Font.load(doc, new FileInputStream(fontFile), true);

        // 3. 写入文本
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
            contentStream.beginText();
            // 设置字体和字号
            contentStream.setFont(customFont, 16);
            // 设置文本位置（x,y）
            contentStream.newLineAtOffset(50, 750);
            // 仅写入部分字符，验证子集嵌入
            contentStream.showText("PDFBox 字体子集嵌入测试：Hello PDFBox 你好PDFBox 456 年");
            contentStream.endText();
        }

        // 4. 保存并关闭文档
        String pdfPath = "output_pdfbox.pdf";
        doc.save(pdfPath);
        doc.close();

        System.out.println("PDF生成完成，字体仅嵌入使用的字符！");
    }

    @Test
    public void testprintNotifications11() throws Exception {
//        List<String> idList = List.of(ids.split(","));
//        List<CrsCaPrintNoticeVo> notice = crsCaPayRcvblGasDetailService.printNoticeList(idList);
//        List<Map<String, String>> userList = getMaps(notice);
//        Map<String, String> consAddr = Map.of("consAddr", "北京市海淀区上地十街10号", "consName", "300000036",
//                "amount", "158.60", "rcvblTime", "2025-11-30 06:30:00", "ymd", "2025年12月03日", "amountTwo", "158.60", "rcvblTimeTwo", "2025-11-30 17:00:00");
        Map<String, String> consAddr = Map.of("consAddr", "Ab12z", "consName", "300000036",
                "amount", "158.60", "rcvblTime", "2025-11-30 06:30:00", "ymd", "aaaaaaaaaaaaa", "amountTwo", "158.60", "rcvblTimeTwo", "2025-11-30 17:00:00");
//        Map<String, String> consAddr = Map.of("consAddr", "Ab12faksjd", "consName", "300000036",
//                "amount", "158.60", "rcvblTime", "2025-11-30 06:30:00", "ymd", "aaaaaaaaaaaaa", "amountTwo", "158.60", "rcvblTimeTwo", "2025-11-30 17:00:00");

        List<Map<String, String>> userList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            userList.add(consAddr);
        }

        List<byte[]> pdfBytesList = userList.stream()
                .map(data -> {
                    try {
                        return fillPdfTemplate11(data);
                    } catch (Exception e) {
                        log.error("填充PDF失败，数据：{}", data, e);
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        byte[] mergedPdf = PdfBoxUtils.merge(pdfBytesList);
        ;

        Path path = Paths.get(basePath, LocalDateTimeUtil.format(LocalDateTime.now(), "yyyyMMdd_HHmmss") + "merged.pdf");
        Files.write(path, mergedPdf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    //    public  PDType0Font getTypeFont() throws IOException {
//        try (InputStream fontStream = Files.newInputStream(Paths.get(getFontPath()))) {
//            if (fontStream == null) throw new IllegalStateException("simhei.ttf 未找到");
//            typeFont = PDType0Font.load(document, fontStream, true);
//            res.put(fontName, simheiFont);
//            acroForm.setDefaultAppearance("/SimHei 9 Tf 0 g");
//        }
//    }
    private byte[] fillPdfTemplate11(Map<String, String> data) throws Exception {
        Path path = Paths.get(basePath + "/template.pdf");
        try (InputStream is = Files.newInputStream(path)) {
            if (is == null) throw new IllegalStateException("模板未找到");
            byte[] templateBytes = is.readAllBytes();

            try (PDDocument document = Loader.loadPDF(templateBytes)) {
                PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
                if (acroForm == null) throw new IllegalStateException("模板无表单");

                log.info("【真实字段名】: {}", acroForm.getFields().stream().map(PDField::getFullyQualifiedName).collect(Collectors.toList()));
                PDResources res = acroForm.getDefaultResources();
                if (res == null) {
                    res = new PDResources();
                    acroForm.setDefaultResources(res);
                }
                // 字体名称（自定义，保持唯一）
//                String fontAlias = "CustomChineseFont";
                String fontAlias = "FZSTK--GBK1-0";
                COSName fontName = COSName.getPDFName(fontAlias);
                PDType0Font simheiFont;
                try (InputStream fontStream = Files.newInputStream(Paths.get(getFontPath()))) {
                    if (fontStream == null) throw new IllegalStateException("simhei.ttf 未找到");
                    simheiFont = PDType0Font.load(document, fontStream, true);

                    String fname = res.add(simheiFont).getName();
                    fontAlias = fname;
                    res.put(fontName, simheiFont);
                    acroForm.setDefaultAppearance("/" + fontAlias + " 9 Tf 0 g");
                }

                for (PDField field : acroForm.getFields()) {
                    if (field instanceof PDTextField textField) {
                        textField.setDefaultAppearance("/" + fontAlias + " 9 Tf 0 g");
                    }
                }

                for (Map.Entry<String, String> entry : data.entrySet()) {
                    String fieldName = entry.getKey();
                    String value = entry.getValue() != null ? entry.getValue() : "";
                    PDField field = acroForm.getField(fieldName);
                    if (field != null) {
                        field.setValue(value);
                    } else {
                        log.warn("字段不存在：{}", fieldName);
                    }
                }

                acroForm.refreshAppearances();
//                acroForm.flatten(acroForm.getFields(), false);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                return out.toByteArray();
            }
        }
    }

}
