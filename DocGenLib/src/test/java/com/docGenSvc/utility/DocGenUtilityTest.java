package com.docGenSvc.utility;


import com.docGenSvc.exception.DocGenNGException;
import com.docGenSvc.exception.InvalidInputException;
import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.properties.DocGenProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocGenUtilityTest {

    @Mock
    private DocGenProperties docGenProperties;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DocGenUtility docGenUtility;

    private DocGenData request;

    @BeforeEach
    void setUp() {
        request = new DocGenData();
        request.setClientId("PROS");
        request.setQuoteId("Q12345");
        request.setDocType("Report");
    }
    @Test
    public void testTicketGenerator() {
        String ticket = docGenUtility.ticketGenerator("quoteId");
        assertTrue(ticket.startsWith("quoteId-"));
    }

    @Test
    void testValidateRequest_ValidRequest() {
        assertDoesNotThrow(() -> docGenUtility.validateRequest(request));
    }

    @Test
    void testValidateRequest_EmptyQuoteId() {
        request.setQuoteId("");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> docGenUtility.validateRequest(request));
        assertEquals("quoteId should be present", exception.getMessage());
    }

    @Test
    void testValidateRequest_ShortQuoteId() {
        request.setQuoteId("Q");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> docGenUtility.validateRequest(request));
        assertEquals("quoteId minimum length should be 2", exception.getMessage());
    }

    @Test
    void testValidateRequest_LongQuoteId() {
        request.setQuoteId("Q123456789012345678901234567890123456789012345678901");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> docGenUtility.validateRequest(request));
        assertEquals("quoteId maximum length should be 50", exception.getMessage());
    }

    @Test
    void testValidateRequest_EmptyDocType() {
        request.setDocType("");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> docGenUtility.validateRequest(request));
        assertEquals("docType should be present", exception.getMessage());
    }

    @Test
    void testValidateRequest_ShortDocType() {
        request.setDocType("R");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> docGenUtility.validateRequest(request));
        assertEquals("docType minimum length should be 2", exception.getMessage());
    }

    @Test
    void testValidateRequest_LongDocType() {
        request.setDocType("Report123456789012345678901234567890123456789012345678901");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> docGenUtility.validateRequest(request));
        assertEquals("docType maximum length should be 50", exception.getMessage());
    }

//    @Test
//    public void testCopyTemplate_Success() throws IOException {
//        when(docGenProperties.getTemplateFile()).thenReturn("template.xlsx");
//        ClassPathResource resource = new ClassPathResource("template.xlsx");
//        InputStream fis = resource.getInputStream();
//        when(docGenUtility.copyTemplate("template.xlsx")).thenReturn(new File("copiedTemplate.xlsx"));
//
//        File copiedFile = docGenUtility.copyTemplate("template.xlsx");
//        assertNotNull(copiedFile);
//        assertTrue(copiedFile.exists());
//    }
    @Test
    public void testCopyTemplate_Failure() {
        assertThrows(DocGenNGException.class, () -> {
            docGenUtility.copyTemplate("invalidTemplateFile");
        });
    }


}
