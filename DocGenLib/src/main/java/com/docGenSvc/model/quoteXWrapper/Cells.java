package com.docGenSvc.model.quoteXWrapper;

import lombok.Data;

import java.util.Map;

@Data
public class Cells {
    private Map<String, CellValue> cells;
}
