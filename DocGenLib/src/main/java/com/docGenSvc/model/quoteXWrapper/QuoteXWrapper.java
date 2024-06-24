package com.docGenSvc.model.quoteXWrapper;

import lombok.Data;

import java.util.Map;

@Data
public class QuoteXWrapper {
    private Map<String, Fields> fields;
    private Map<String, Rows.Row> rows;
}
