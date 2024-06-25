package com.docGenSvc.utility;


import com.docGenSvc.exception.InvalidInputException;
import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.properties.DocGenProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DocGenUtilityTest {

    @Mock
    private DocGenProperties docGenProperties;

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

}
