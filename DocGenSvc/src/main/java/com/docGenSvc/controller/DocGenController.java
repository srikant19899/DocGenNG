package com.docGenSvc.controller;
import com.docGenSvc.exception.InvalidInputException;
import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.model.response.DocumentsResponse;
import com.docGenSvc.model.response.Errors;
import com.docGenSvc.model.response.JobSubmitResponse;
import com.docGenSvc.service.DocGenNgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Arrays;



@RestController
@Tag(name = "DocGenController")
public class DocGenController {
    @Autowired
    private  DocGenNgService docGenNgService;
    private static final Logger logger = LoggerFactory.getLogger(DocGenController.class);



       /*
        step 1: change this service to asych
        step 2: once the request comes in - the service should return the ticket , another thread to process the request
        Step 3: return the ticket from service class even if there is any exception
        step 4: in service class write a method to call QuoteX service ( for now hardcode it as - it should return a template details )
        step 5: in resource folder add the master template and use that template and raw data excel file to create a new template ( sales / other )
        step 6: in service write a method that will hold the business logic to map the raw data to excel file using the master template
        NOTE: Business logic --> Create a copy of master template, copy thr values from raw excel to the newly created excel file - refer poc code
        Step 7: once the raw data is mapped in the newly created excel then --> you will make a call to a new method that will call the db and store the ticket number and is_ready flag as true
        Step 8: the created excel sheet to be moved to server path --. write a method for this and once we move it then we need to store the path in redis/db for the retrieve service to use the path
        Step 9: Exception handling to be solid across the application.
        schema => requestId, is_ready, tkt_no
         */

    @Operation(summary = "Initiates the process of generating a document", description = "This operation validates the data in the request and starts the generation of the document defined by the arguments")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote matching is submitted; the returned ticket can be used to check status",
                    content = @Content(schema = @Schema(implementation = DocumentsResponse.class), mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content(schema = @Schema(implementation = JobSubmitResponse.class),mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(schema = @Schema(implementation = JobSubmitResponse.class), mediaType = "application/json"))
    })
    @PostMapping("/documents")
    public ResponseEntity<Object> submit(  @RequestBody  DocGenData request,
                                          @RequestHeader(name = "requestId") String requestId,
                                          @RequestHeader(name = "trace", required = false) String trace ) throws Exception {

        try {
            String fileId = docGenNgService.processFile(requestId, trace, request);
            return ResponseEntity.status(HttpStatus.OK).body(new DocumentsResponse(fileId));
        }  catch (InvalidInputException | IOException | InterruptedException e) {
            logger.error("InvalidInputException occurred: {}", e.getMessage());
            throw new InvalidInputException( e.getMessage());
        }catch (Exception e) {
            logger.error("IOException occurred: {}", e.getMessage());
            throw new Exception(e.getMessage());
           }

    }

    @GetMapping("/documents/jobs/{jobId}")
    public ResponseEntity<Boolean> isReady(@RequestParam("jobId") String jobId) {
        boolean isReady = docGenNgService.isFileReady(jobId);
        return ResponseEntity.ok(isReady);
    }


    @GetMapping("/templates/")
    public ResponseEntity<byte[]> retrieve(@RequestParam("Id") String Id) {
        try {
            byte[] fileData = docGenNgService.getFile(Id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + Id + ".xlsx")
                    .body(fileData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);

        }

    }
}
