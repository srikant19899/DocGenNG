package com.DocGenNG.model.request;

public class DocumentsRequest {

    private String quoteId;
    private String docType;
    private String template;

    public String getQuoteId() {
        return quoteId;
    }

    public DocumentsRequest setQuoteId(String quoteId) {
        this.quoteId = quoteId;
        return this;
    }

    public String getDocType() {
        return docType;
    }

    public DocumentsRequest setDocType(String docType) {
        this.docType = docType;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public DocumentsRequest setTemplate(String template) {
        this.template = template;
        return this;
    }

    @Override
    public String toString() {
        return "DocumentsRequest{" +
                "quoteId='" + quoteId + '\'' +
                ", docType='" + docType + '\'' +
                ", template='" + template + '\'' +
                '}';
    }
}
