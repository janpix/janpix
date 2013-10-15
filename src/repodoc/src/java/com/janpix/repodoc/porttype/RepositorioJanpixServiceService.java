package com.janpix.repodoc.porttype;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.2
 * 2013-10-14T22:29:46.058-03:00
 * Generated source version: 2.7.2
 * 
 */
@WebServiceClient(name = "RepositorioJanpixServiceService", 
                  wsdlLocation = "/tmp/tempdir7963309908591967213.tmp/repositorioJanpix_1.wsdl",
                  targetNamespace = "http://repodoc.janpix.com/") 
public class RepositorioJanpixServiceService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://repodoc.janpix.com/", "RepositorioJanpixServiceService");
    public final static QName RepositorioJanpixServicePort = new QName("http://repodoc.janpix.com/", "RepositorioJanpixServicePort");
    static {
        URL url = RepositorioJanpixServiceService.class.getResource("/tmp/tempdir7963309908591967213.tmp/repositorioJanpix_1.wsdl");
        if (url == null) {
            java.util.logging.Logger.getLogger(RepositorioJanpixServiceService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "/tmp/tempdir7963309908591967213.tmp/repositorioJanpix_1.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public RepositorioJanpixServiceService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RepositorioJanpixServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RepositorioJanpixServiceService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public RepositorioJanpixServiceService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public RepositorioJanpixServiceService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public RepositorioJanpixServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns RepositorioJanpixService
     */
    @WebEndpoint(name = "RepositorioJanpixServicePort")
    public RepositorioJanpixService getRepositorioJanpixServicePort() {
        return super.getPort(RepositorioJanpixServicePort, RepositorioJanpixService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RepositorioJanpixService
     */
    @WebEndpoint(name = "RepositorioJanpixServicePort")
    public RepositorioJanpixService getRepositorioJanpixServicePort(WebServiceFeature... features) {
        return super.getPort(RepositorioJanpixServicePort, RepositorioJanpixService.class, features);
    }

}
