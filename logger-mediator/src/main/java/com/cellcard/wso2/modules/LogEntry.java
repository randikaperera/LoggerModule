package com.cellcard.wso2.modules;

public class LogEntry {

    private String platform;
    private String loggerVersion;
    private String correlationId;
    private String capability;
    private String integrationReference;
    private String integrationVersion;
    private String messageType;
    private String clientMessageId;
    private String businessId;
    private String origin;
    private String sequenceName;
    private String httpMethod;
    private String resource;
    private String metaTransportInUrl;
    private String contentType;
    private String context;
    private Integer statusCode;
    private String message;
    private String registryPath;
    private String errorReason;
    private String requestBody;
    private Object body;
    private String originatingErrorMessage;
    private String originatingErrorDetail;
    private String uniVerseInfo;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLoggerVersion() {
        return loggerVersion;
    }

    public void setLoggerVersion(String loggerVersion) {
        this.loggerVersion = loggerVersion;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getIntegrationReference() {
        return integrationReference;
    }

    public void setIntegrationReference(String integrationReference) {
        this.integrationReference = integrationReference;
    }

    public String getIntegrationVersion() {
        return integrationVersion;
    }

    public void setIntegrationVersion(String integrationVersion) {
        this.integrationVersion = integrationVersion;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getMetaTransportInUrl() {
        return metaTransportInUrl;
    }

    public void setMetaTransportInUrl(String metaTransportInUrl) {
        this.metaTransportInUrl = metaTransportInUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public void setRegistryPath(String registryPath) {
        this.registryPath = registryPath;
    }
    
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Object getBody() {
        return this.body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public void setOriginatingErrorMessage(String originatingErrorMessage) {
        this.originatingErrorMessage = originatingErrorMessage;
    }

    public void setOriginatingErrorDetail(String originatingErrorDetail) {
        this.originatingErrorDetail = originatingErrorDetail;
    }

    public void setUniVerseInfo(String uniVerseInfo) {
        this.uniVerseInfo = uniVerseInfo;
    }

}