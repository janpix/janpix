package com.janpix.repodoc

import javax.jws.WebMethod
import javax.jws.WebParam
import javax.jws.soap.SOAPBinding
import javax.jws.soap.SOAPBinding.ParameterStyle
import javax.xml.ws.soap.MTOM

import org.grails.cxf.utils.EndpointType
import org.grails.cxf.utils.GrailsCxfEndpoint

import com.janpix.servidordocumentos.dto.message.*

@MTOM(enabled = true)
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
@GrailsCxfEndpoint(expose = EndpointType.JAX_WS,soap12=true)

class RepositorioJanpixService {
	
	static transactional = false
	
	def repositorioService
	
    /**
	 * Ingresa un documento en el repositorio y lo registra en el registro
	 * @return
	 */
	@WebMethod
    public ACKMessage provideAndRegisterDocument(
		@WebParam(name = "provideAndRegisterDocumentMessage")
		ProvideAndRegisterDocumentRequest retriveRequest) 
	{
		return repositorioService.provideAndRegisterDocument(retriveRequest.clinicalDocument);
	}
	
	/**
	 * Retorna el documento que se pide por parametro
	 * @param retriveRequest
	 * @return
	 */
	@WebMethod
	public ACKMessage retrieveDocument(
		@WebParam(name = "retrieveDocumentMessage")
		RetrieveDocumentRequest retrieveRequest
		)
	{
		return repositorioService.retrieveDocumentByUUID(retrieveRequest.uuid);
	}
}
