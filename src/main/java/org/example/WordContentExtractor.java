package org.example;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class WordContentExtractor {
    private static final List<Path> wordFiles = new ArrayList<>();
    
    public static void main(String[] args) {
        // 要扫描的根目录，可以根据需要修改
        String rootDirectory = "D:/01Document/华润/02工作日志/后端-赵凯帆/2025年8月";
        // 输出的txt文件路径
        String outputFile = "./output/word_contents.txt";
        
        try {
            // 递归扫描所有Word文件
            Files.walkFileTree(Paths.get(rootDirectory), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString().toLowerCase();
                    // 检查是否为Word文件
                    if (fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
                        wordFiles.add(file);
                        System.out.println("找到Word文件: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
            // 提取内容并写入到txt文件
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile), 
                    StandardCharsets.UTF_8)) {
                for (Path wordFile : wordFiles) {
                    writer.write("=== 文件名称: " + wordFile.getFileName() + " ===");
                    writer.newLine();
                    writer.write("=== 文件路径: " + wordFile + " ===");
                    writer.newLine();
                    writer.write("=== 内容开始 ===");
                    writer.newLine();
                    writer.newLine();
                    
                    // 提取文件内容
                    String content = extractWordContent(wordFile.toFile());
                    writer.write(content);
                    
                    writer.newLine();
                    writer.write("=== 内容结束 ===");
                    writer.newLine();
                    writer.newLine();
                    writer.write("==================================================");
                    writer.newLine();
                    writer.newLine();
                }
                System.out.println("所有Word文件内容已提取到: " + outputFile);
            }
            
        } catch (IOException e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 提取Word文档内容
     */
    private static String extractWordContent(File file) throws IOException {
        String content = "";
        String fileName = file.getName().toLowerCase();
        
        try (FileInputStream fis = new FileInputStream(file)) {
            if (fileName.endsWith(".docx")) {
                // 处理docx格式
                XWPFDocument document = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                content = extractor.getText();
                extractor.close();
            } else if (fileName.endsWith(".doc")) {
                // 处理doc格式
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                content = extractor.getText();
                extractor.close();
            }
        }
        
        return content;
    }
}
