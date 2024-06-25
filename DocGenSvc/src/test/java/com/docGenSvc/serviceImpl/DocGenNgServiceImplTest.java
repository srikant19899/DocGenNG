package com.docGenSvc.serviceImpl;




import com.docGenSvc.model.quoteXWrapper.QuoteXWrapper;
import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.properties.DocGenProperties;
import com.docGenSvc.utility.DocGenUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocGenNgServiceImplTest {

    @Mock
    private DocGenUtility docGenUtility;

    @Mock
    private DocGenProperties docGenProperties;

    @InjectMocks
    private DocGenNgServiceImpl docGenNgServiceImpl;

    private DocGenData request;

    @BeforeEach
    void setUp() {
        request = new DocGenData();
        request.setQuoteId("testQuoteId");
        request.setClientId("PROS");
        request.setDocType("testDocType");
    }

    @Test
    void testProcessFile() {
        when(docGenUtility.ticketGenerator(anyString())).thenReturn("ticket123");
        doNothing().when(docGenUtility).validateRequest(any());
        doNothing().when(docGenUtility).addDocumentStatus(anyString(), anyString());

        String ticketNumber = docGenNgServiceImpl.processFile("request123", "trace123", request);

        assertEquals("ticket123", ticketNumber);
        verify(docGenUtility).validateRequest(any());
        verify(docGenUtility).addDocumentStatus(anyString(), anyString());
        verify(docGenUtility).ticketGenerator(anyString());
    }

    @Test
    void testGenerateFile() throws Exception {
        QuoteXWrapper quoteXWrapper = new QuoteXWrapper();
        when(docGenUtility.getQuoteXData(any())).thenReturn(quoteXWrapper);
        when(docGenUtility.copyTemplate(anyString())).thenReturn(new File("testTemplate.xlsx"));
        when(docGenProperties.getTemplateFile()).thenReturn("template.xlsx");
        when(docGenProperties.getDocGenLag()).thenReturn(1000);

        CompletableFuture<Void> future = docGenNgServiceImpl.generateFile("file123", "request123", request);

        future.get();
        verify(docGenUtility).getQuoteXData(any());
        verify(docGenUtility).copyTemplate(anyString());
        verify(docGenUtility).generateDocGenReport(any(), any(), any());
        verify(docGenUtility).deleteSelectedSheet(any(), any());
    }


}
