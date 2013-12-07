package com.janpix.webclient.regdoc;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.6.2
 * 2013-12-06T15:34:58.153-03:00
 * Generated source version: 2.6.2
 * 
 */
@WebServiceClient(name = "DocumentJanpixServiceService", 
                  wsdlLocation = "http://localhost:9090/regdoc-0.1/services/documentJanpix?wsdl",
                  targetNamespace = "http://services.regdoc.janpix.com/") 
public class DocumentJanpixServiceService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://services.regdoc.janpix.com/", "DocumentJanpixServiceService");
    public final static QName DocumentJanpixServicePort = new QName("http://services.regdoc.janpix.com/", "DocumentJanpixServicePort");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:9090/regdoc-0.1/services/documentJanpix?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(DocumentJanpixServiceService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://localhost:9090/regdoc-0.1/services/documentJanpix?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public DocumentJanpixServiceService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public DocumentJanpixServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DocumentJanpixServiceService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public DocumentJanpixServiceService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public DocumentJanpixServiceService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public DocumentJanpixServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns DocumentJanpixService
     */
    @WebEndpoint(name = "DocumentJanpixServicePort")
    public DocumentJanpixService getDocumentJanpixServicePort() {
        return super.getPort(DocumentJanpixServicePort, DocumentJanpixService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DocumentJanpixService
     */
    @WebEndpoint(name = "DocumentJanpixServicePort")
    public DocumentJanpixService getDocumentJanpixServicePort(WebServiceFeature... features) {
        return super.getPort(DocumentJanpixServicePort, DocumentJanpixService.class, features);
    }

}
