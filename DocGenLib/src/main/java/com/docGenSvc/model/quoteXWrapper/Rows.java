package com.docGenSvc.model.quoteXWrapper;

import lombok.Data;

import java.util.Map;

@Data
public class Rows {
    private Map<String, Row> rows;

    @Data
    public static class Row {
        private String rowId;
        private Map<String, CellValue> cells;
    }
}
