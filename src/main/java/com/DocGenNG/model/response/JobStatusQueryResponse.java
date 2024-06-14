package com.DocGenNG.model.response;

import lombok.Data;

import java.util.List;

@Data
public class JobStatusQueryResponse {
    private Status status;
    private List<Errors> errors;
    private String routing;
}
