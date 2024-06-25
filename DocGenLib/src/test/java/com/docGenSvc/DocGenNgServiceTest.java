//package com.docGenSvc;
//
//import com.docGenSvc.repository.DocGenNgRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.*;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class DocGenNgServiceTest {
//
//    @Autowired
//    private DocGenNgRepository docGenNgRepository;
//
//    @Autowired
//    private ResourceLoader resourceLoader;
//
//    private static final String TEST_FILE_NAME = "docGenNg.csv";
//    private static final String SERVER_PATH = "/serverPath";
//
//    private Resource mockResource;
//
//    @Before
//    public void setup() throws IOException {
//        resourceLoader = mock(ResourceLoader.class);
//        mockResource = mock(Resource.class);
//
//        when(resourceLoader.getResource("classpath:" + TEST_FILE_NAME)).thenReturn(mockResource);
//        when(mockResource.exists()).thenReturn(true);
//
//        InputStream inputStream = new ByteArrayInputStream("Test content".getBytes());
//        when(mockResource.getInputStream()).thenReturn(inputStream);
//    }
//
//    @Test
//    public void testMoveFileToPath() throws IOException {
//        docGenNgRepository.moveFiletoPath(TEST_FILE_NAME, SERVER_PATH);
//        File targetFile = new File(SERVER_PATH + File.separator + TEST_FILE_NAME);
//        assertTrue(targetFile.exists());
//    }
//
//    @Test(expected = FileNotFoundException.class)
//    public void testMoveFileToPath_FileNotFound() throws IOException {
//        when(mockResource.exists()).thenReturn(false);
//
//        docGenNgRepository.moveFiletoPath(TEST_FILE_NAME, SERVER_PATH);
//    }
//}