//package org.example.service.impl;
//
//import org.dromara.x.file.storage.core.FileInfo;
////import org.dromara.x.file.storage.core.UploadPretreatment;
//import org.dromara.x.file.storage.core.FileStorageService;
//import org.dromara.x.file.storage.core.upload.UploadPretreatment;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
///**
// * SFTPFileStorageServiceImpl 单元测试类
// */
//class SFTPFileStorageServiceImplTest {
//
//    @InjectMocks
//    private SFTPFileStorageServiceImpl sftpFileStorageService;
//
//    @Mock
//    private FileStorageService fileStorageService;
//
//    @Mock
//    private MultipartFile multipartFile;
//
//    @Mock
//    private UploadPretreatment uploadPretreatment;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testUpload_Success() throws Exception {
//        // 模拟 multipartFile 的必要行为
//        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
//        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
//
//        // 确保 fileStorageService.of(multipartFile) 返回 mock 对象
//        when(fileStorageService.of(multipartFile)).thenReturn(uploadPretreatment);
//
//        // 模拟链式调用返回自身
//        when(uploadPretreatment.setPath(anyString())).thenReturn(uploadPretreatment);
//        when(uploadPretreatment.setSaveFilename(anyString())).thenReturn(uploadPretreatment);
//        when(uploadPretreatment.setObjectId(anyString())).thenReturn(uploadPretreatment);
//        when(uploadPretreatment.setObjectType(anyString())).thenReturn(uploadPretreatment);
//        when(uploadPretreatment.putAttr(anyString(), anyString())).thenReturn(uploadPretreatment);
//
//        // 模拟 upload 返回 FileInfo
//        FileInfo expectedFileInfo = new FileInfo();
//        expectedFileInfo.setFilename("image.jpg");
//        when(uploadPretreatment.upload()).thenReturn(expectedFileInfo);
//
//        // 执行被测方法
//        FileInfo result = sftpFileStorageService.upload(multipartFile);
//
//        // 验证结果
//        assertNotNull(result);
//        assertEquals("image.jpg", result.getFilename());
//
//    }
//
//    @Test
//    void testUpload_FileIsNull() {
//        assertThrows(NullPointerException.class, () -> {
//            sftpFileStorageService.upload(null);
//        });
//    }
//
//}
