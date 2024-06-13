package com.DocGenNG.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsErrorResponse {
    public Errors errors;
    public String ticket;
}
