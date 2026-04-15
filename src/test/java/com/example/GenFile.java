package com.example;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenFile {
    private static final int[] FILE_SIZES_MB = {3, 10, 20, 50, 100, 200, 300, 500};
    // 定义生成文件的目录
    private static final String OUTPUT_DIRECTORY = "./upload_test_files";

    @Test
    void genFile() {


        // 创建输出目录
        File directory = new File(OUTPUT_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("创建目录成功: " + directory.getAbsolutePath());
            } else {
                System.err.println("创建目录失败: " + directory.getAbsolutePath());
                return;
            }
        }

        // 生成测试数据块(1KB)，使用重复的字符填充
        String dataBlock = generateDataBlock(1024);

        // 为每个指定大小生成文件
        for (int sizeMb : FILE_SIZES_MB) {
            generateFile(sizeMb, dataBlock);
        }

        System.out.println("所有文件生成完成!");
    }

    /**
     * 生成指定大小的文件
     *
     * @param sizeMb    文件大小(MB)
     * @param dataBlock 数据块(1KB)
     */
    private static void generateFile(int sizeMb, String dataBlock) {
        long startTime = System.currentTimeMillis();
        String fileName = String.format("%s/test_file_%dMB_%s.txt",
                OUTPUT_DIRECTORY,
                sizeMb,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // 计算需要写入的数据块数量 (1MB = 1024KB)
            long blocksCount = (long) sizeMb * 1024;

            System.out.printf("开始生成 %dMB 文件: %s%n", sizeMb, fileName);

            // 写入数据块
            for (long i = 0; i < blocksCount; i++) {
                writer.write(dataBlock);

                // 打印进度
                if (i % 1024 == 0 && i > 0) {
                    double progress = (double) i / blocksCount * 100;
                    System.out.printf("进度: %.2f%%%n", progress);
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.printf("文件生成完成，耗时: %.2f秒%n", (endTime - startTime) / 1000.0);

        } catch (IOException e) {
            System.err.printf("生成 %dMB 文件时出错: %s%n", sizeMb, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成指定大小的数据块
     *
     * @param sizeBytes 数据块大小(字节)
     * @return 数据块字符串
     */
    private static String generateDataBlock(int sizeBytes) {
        // 使用随机字符填充数据块，增加文件内容的随机性
        StringBuilder sb = new StringBuilder(sizeBytes);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

        for (int i = 0; i < sizeBytes; i++) {
            // 循环使用字符集，确保数据块大小准确
            sb.append(chars.charAt(i % chars.length()));
        }

        return sb.toString();
    }
}
