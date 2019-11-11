package com.cellcard.wso2.modules;

import static org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS;

import java.io.ByteArrayOutputStream;
/*import java.io.IOException;
import java.io.StringReader;*/
import java.util.Map;

/*import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;*/
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
//import org.w3c.dom.Document;
import org.w3c.dom.Node;
/*import org.xml.sax.InputSource;
import org.xml.sax.SAXException;*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class LoggerMediator extends AbstractMediator {
   // private static final String METADATA = "MetaData";
    private static final String VERSION = "1.0";
    private static final Logger LOGGER = Logger.getLogger("LOGGER_V2");
    private static final Logger CARBON_LOGGER = Logger.getLogger(LoggerMediator.class);

    @Override
    public boolean mediate(MessageContext context) {
        LOGGER.log(getLogLevel(context), buildLog(context));
        return true;
    }

    private Level getLogLevel(MessageContext context) {
        String logLevel = getSynapseValue("logLevel", context);

        if (null != logLevel && logLevel.trim().length() > 0)
            return Level.toLevel(logLevel);
        else
            return Level.INFO;
    }

    private String buildLog(MessageContext context) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.disableHtmlEscaping();
        Gson gson = gsonbuilder.create();

        String businessId = null;
        String capability = null;
        String correlationId = null;
        String clientMessageId = null;
        String messageType = null;
        String integrationReference = null;
        String integrationVersion = null;

        org.apache.axis2.context.MessageContext axis2Context = getAxis2Context(context);

       // Document document;
        //Node node = null;

      //  try {
        //    document = getTransportValueAsXml(METADATA, axis2Context);
          //  node = document.getFirstChild();
        //} catch (IOException | NullPointerException | SAXException e) {
           // String sequence = (null != context) ? getSynapseValue("sequenceName", context) : "Unknown sequence";
         //   CARBON_LOGGER.warn(sequence + ": MetaData header was not found");
       // }

        //if (null != node) {
            correlationId = "Default";
            clientMessageId = "Default";
            businessId = "Default";
            capability = "Default";
            messageType = "Default";
            integrationReference = "Default";
            integrationVersion = "Default";
       // }

        LogEntry logEntry = new LogEntry();

        logEntry.setPlatform("WSO2");
        logEntry.setLoggerVersion(VERSION);
        logEntry.setCorrelationId(correlationId);
        logEntry.setCapability(capability);

        if (integrationReference == null || "".equals(integrationReference)) {
            logEntry.setIntegrationReference(getSynapseValue("integrationReference", context));
        } else {
            logEntry.setIntegrationReference(integrationReference);
        }

        if (integrationVersion == null || "".equals(integrationVersion)) {
            logEntry.setIntegrationVersion(getSynapseValue("integrationVersion", context));
        } else {
            logEntry.setIntegrationVersion(integrationVersion);
        }

        logEntry.setMessageType(messageType);
        logEntry.setClientMessageId(clientMessageId);
        logEntry.setBusinessId(businessId);
        logEntry.setOrigin(getTransportValue("X-Forwarded-For", axis2Context));
        logEntry.setSequenceName(getSynapseValue("sequenceName", context));
        logEntry.setHttpMethod(getAxisValue("HTTP_METHOD", axis2Context));
        logEntry.setResource(getAxisValue("REST_URL_POSTFIX", axis2Context));
        logEntry.setMetaTransportInUrl(getAxisValue("TransportInURL", axis2Context));
        logEntry.setContentType(getAxisValue("messageType", axis2Context));
        logEntry.setContext(getSynapseValue("REST_API_CONTEXT", context));
        logEntry.setStatusCode(getAxisValueAsNumber("HTTP_SC", axis2Context));
        logEntry.setMessage(getSynapseValue("logMessage", context));
        logEntry.setUniVerseInfo(getSynapseValue("uniVerseInfo", context));
        logEntry.setRegistryPath(getSynapseValue("registryPath", context));

        if (includeMessageBody(context) | includeErrorBody(context)) {
            logEntry.setBody(getBody(context, axis2Context));
        }

        if (includeErrorBody(context)) {
            logEntry.setErrorReason(getSynapseValue("errorReason", context));
            logEntry.setRequestBody(getSynapseValue("requestBody", context));

            if (isUnhandledError(context)) {
                logEntry.setOriginatingErrorMessage(getSynapseValue("ERROR_MESSAGE", context));
                logEntry.setOriginatingErrorDetail(getSynapseValue("ERROR_DETAIL", context));
            }
        }

        return gson.toJson(logEntry);
    }

    private Object getBody(MessageContext context, org.apache.axis2.context.MessageContext axis2Context) {
        if (isMessageType(getAxisValue("messageType", axis2Context), "application/json")) {
            return getJsonBody(context);
        } 
        if (isMessageType(getAxisValue("messageType", axis2Context), "application/xml")) {
            return getSoapBody(context);
        } 
        return null;
    }

    private boolean isMessageType(String messageType, String comparator) {
        return null != messageType && messageType.toLowerCase().contains(comparator);
    }
    
    private Object getSoapBody(MessageContext context) {
        try {
            SOAPBody body = context.getEnvelope().getBody();
            return body.getFirstElement().toString();
        } catch (RuntimeException e) { // NOSONAR
            String sequence = (null != context) ? getSynapseValue("sequenceName", context) : "unknown sequence";
            CARBON_LOGGER.warn("getSoapBody: Request made to log message body from " + sequence + " when no message body exists, returning null instead");
            return null;
        }
    }

    private String getJsonBody(MessageContext context) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {        
            JsonUtil.writeAsJson(context.getEnvelope().getBody().getFirstElement(), outputStream);            
        } catch (AxisFault | OMException e) { // NOSONAR
            String sequence = (null != context) ? getSynapseValue("sequenceName", context) : "unknown sequence";
            CARBON_LOGGER.warn("getJsonBody: Request made to log message body from " + sequence + " when no message body exists, returning null instead");
            return null;
        }
        
        JsonElement jsonElement = new Gson().fromJson(outputStream.toString(), JsonElement.class);
        if (jsonElement.isJsonNull()) {
            return null;
        } else {
            return new Gson().toJson(jsonElement);
        }
    }
    
    private boolean includeMessageBody(MessageContext context) {
        Level level = getLogLevel(context);
        return ((level.equals(Level.INFO) || level.equals(Level.TRACE) || level.equals(Level.WARN)) & isTrue(getSynapseValue("includeMessageBody", context)));
    }

    private boolean isUnhandledError(MessageContext context) {
        return isTrue(getSynapseValue("unhandledError", context));
    }

    private boolean includeErrorBody(MessageContext context) {
        Level level = getLogLevel(context);
        return ((level.equals(Level.DEBUG) || level.equals(Level.ERROR) || level.equals(Level.FATAL)) & isTrueOrEmpty(getSynapseValue("includeMessageBody", context)));
    }

    private boolean isTrue(String value) {
        return null != value && "true".equalsIgnoreCase(value);
    }

    private boolean isTrueOrEmpty(String value) {
        return null == value || value.isEmpty() || "true".equalsIgnoreCase(value);
    }

    private String getSynapseValue(String key, MessageContext context) {
        return (String) context.getProperty(key);
    }

    private String getAxisValue(String key, org.apache.axis2.context.MessageContext axis2Context) {
        return (String) axis2Context.getLocalProperty(key);
    }

    private Integer getAxisValueAsNumber(String key, org.apache.axis2.context.MessageContext axis2Context) {
        Object prop = axis2Context.getLocalProperty(key);
        try {
            if (prop instanceof String)
                return Integer.valueOf((String) prop);

            return (Integer) prop;

        } catch (NumberFormatException e) {
            CARBON_LOGGER.warn("getAxisValueAsNumber: Caught exception when attempting to parse HTTP_SC. Non-numeric value supplied.\n {}", e);
            return null;
        }
    }

   // private Document getTransportValueAsXml(String key, org.apache.axis2.context.MessageContext axis2Context) throws SAXException, IOException {
   //     return createDocumentBuilder().parse(toInputSource(getTransportValue(key, axis2Context)));
    //}

    private String getTransportValue(String key, org.apache.axis2.context.MessageContext axis2Context) {
        
    	Object transportValue = getTransportProperties(axis2Context).get(key);
    	if (transportValue instanceof OMElement) {
    	   return ((OMElement) transportValue).toString();
    	}
        return (String) transportValue;
    }

    /*private static DocumentBuilder createDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputSource toInputSource(String xml) {
        return new InputSource(new StringReader(xml));
    }*/

    public static XPath createXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath();
    }

    public static String query(String xPath, Node node) {
        try {
            return createXPath().compile(xPath).evaluate(node);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    private org.apache.axis2.context.MessageContext getAxis2Context(MessageContext context) {
        return ((Axis2MessageContext) context).getAxis2MessageContext();
    }

    @SuppressWarnings("rawtypes")
    private Map getTransportProperties(org.apache.axis2.context.MessageContext axis2Context) {
        return (Map) axis2Context.getProperty(TRANSPORT_HEADERS);
    }
}

