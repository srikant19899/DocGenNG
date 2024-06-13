package com.DocGenNG.controller;

import com.DocGenNG.exceptionHandler.InvalidInputException;
import com.DocGenNG.model.request.DocumentsRequest;
import com.DocGenNG.model.response.DocumentsErrorResponse;
import com.DocGenNG.model.response.DocumentsResponse;
import com.DocGenNG.model.response.Errors;
import com.DocGenNG.service.DocGenNgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
public class DocGenController {

    private final DocGenNgService docGenNgService;

    @Autowired
    public DocGenController(DocGenNgService docGenNgService) {
        this.docGenNgService = docGenNgService;
    }

       /*
        step 1: change this service to asych
        step 2: once the request comes in - the service should return the ticket , another thread to process the request
        Step 3: return the ticket from service class even if there is any exception
        step 4: in service class write a method to call QuoteX service ( for now hardcode it as - it should return a template details )
        step 5: in resource folder add the master template and use that template and raw data excel file to create a new template ( sales / other )
        step 6: in service write a method that will hold the business logic to map the raw data to excel file using the mastet template
        NOTE: Business logic --> Create a copy of master template, copy thr values from raw excel to the newly created excel file - refer poc code
        Step 7: once the raw data is mapped in the newly created excel then --> you will make a call to a new method that will call the db and store the ticket number and is_ready flag as true
        Step 8: the created excel sheet to be moved to server path --. write a nethod for this and once we move it then we need to store the path in redis/db for the retrive service to use the path
        Step 9: Exception handling to be solid across the application.
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
                    content = @Content(schema = @Schema(implementation = DocumentsErrorResponse.class),mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(schema = @Schema(implementation = DocumentsErrorResponse.class), mediaType = "application/json"))
    })
    @PostMapping("/documents")
    public ResponseEntity<Object> submit(@RequestBody DocumentsRequest request,
                                         @RequestHeader(name = "requestId", required = true) String requestId,
                                         @RequestHeader(name = "trace", required = false) String trace) {
        try {
            String fileId = docGenNgService.processFile(requestId, trace, request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new DocumentsResponse(fileId));
        }  catch (InvalidInputException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DocumentsErrorResponse(new Errors("400", e.getMessage()), ""));
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DocumentsErrorResponse(new Errors("500", e.getMessage()), ""));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/documents/jobs/")
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
