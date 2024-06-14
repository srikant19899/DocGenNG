package com.DocGenNG.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsErrorResponse {
    public List<Errors> errors;
    public String ticket;
}