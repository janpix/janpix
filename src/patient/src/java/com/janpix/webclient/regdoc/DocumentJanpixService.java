package com.janpix.webclient.regdoc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.6.2
 * 2013-12-16T17:05:57.532-03:00
 * Generated source version: 2.6.2
 * 
 */
@WebService(targetNamespace = "http://services.regdoc.janpix.com/", name = "DocumentJanpixService")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface DocumentJanpixService {

    @WebResult(name = "queryDocumentResponse", targetNamespace = "http://services.regdoc.janpix.com/", partName = "queryDocumentResponse")
    @WebMethod
    public AckStoredQueryMessage queryDocument(
        @WebParam(partName = "queryDocumentRequestMessage", name = "queryDocumentRequestMessage", targetNamespace = "http://services.regdoc.janpix.com/")
        QueryDocumentRequest queryDocumentRequestMessage
    );

    @WebResult(name = "registerDocumentResponse", targetNamespace = "http://services.regdoc.janpix.com/", partName = "registerDocumentResponse")
    @WebMethod
    public AckMessage registerDocument(
        @WebParam(partName = "registerDocumentRequestMessage", name = "registerDocumentRequestMessage", targetNamespace = "http://services.regdoc.janpix.com/")
        RegisterDocumentRequest registerDocumentRequestMessage
    );

    @WebResult(name = "updateStateDocumentResponse", targetNamespace = "http://services.regdoc.janpix.com/", partName = "updateStateDocumentResponse")
    @WebMethod
    public AckMessage updateStateDocument(
        @WebParam(partName = "updateStateDocumentRequestMessage", name = "updateStateDocumentRequestMessage", targetNamespace = "http://services.regdoc.janpix.com/")
        UpdateStateDocumentRequest updateStateDocumentRequestMessage
    );
}
