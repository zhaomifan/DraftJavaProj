//package com.example;
//
//import org.dromara.x.file.storage.core.FileInfo;
//import org.dromara.x.file.storage.core.FileStorageService;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//
//public class UploadSftpTest {
//
//    @Test
//    void testSftpUpload() {
////测试文件上传到sftp
//        // mock 文件
//        byte[] bytes = "hello world".getBytes();
//        String filename = "hello.txt";
//        FileStorageService fileStorageService = new FileStorageService();
//
//        fileStorageService.of(bytes, filename)
//                .setPath("upload/") //保存到相对路径下，为了方便管理，不需要可以不写
//                .setSaveFilename("image.jpg") //设置保存的文件名，不需要可以不写，会随机生成
//                .setObjectId("0")   //关联对象id，为了方便管理，不需要可以不写
//                .setObjectType("0") //关联对象类型，为了方便管理，不需要可以不写
//                .putAttr("role", "admin").upload(); //保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取
//    }
//
//    @Test
//    void download() {
//        String basePath = "upload/";
//        FileStorageService fileStorageService = new FileStorageService();
//        String path = "bs/business_support/";
//        String fileName = "59678358459498754.jpg";
//        String platform = "sftp-1";
//        FileInfo fileInfo = new FileInfo(basePath, path, fileName);
//        fileInfo.setPlatform(platform);
//
//        fileStorageService.download(fileInfo).file("C:\\a.jpg");
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        fileStorageService.download(fileInfo).outputStream(out);
//
//    }
//
//}
