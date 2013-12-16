package ar.com.janpix.patient

import com.janpix.exceptions.ErrorOnApproveDocumentJanpixException
import com.janpix.exceptions.JanpixConnectionException
import com.janpix.webclient.regdoc.AckMessage
import com.janpix.webclient.regdoc.AckStoredQueryMessage
import com.janpix.webclient.regdoc.QueryDocumentRequest
import ar.com.janpix.patient.utils.JanpixAssembler

class JanpixService {
	
	static transactional = false
	
	def grailsApplication
	def janpixRegdocServiceClient
	
    List<StudyCommand> queryAllStudies(PatientCommand patient) {
		List<StudyCommand> studies = []
		AckStoredQueryMessage ack
		String cuis = patient.cuis
		
		try {
			log.info("Consultando por todos los estudios del paciente "+patient)
			
			log.info("Armando Request")
			QueryDocumentRequest request = new QueryDocumentRequest()
			request.healthEntityFinder = JanpixAssembler.toHealthEntity(grailsApplication.config.patients)
			request.patientId = cuis
			
			log.info("Enviando request al WS")
			ack = janpixRegdocServiceClient.queryDocument(request)
		}
		catch(Exception ex){
			String message ="Error de conexión contra el Registro de Documentos: "+ex.message
			log.error(message, ex)
			throw new JanpixConnectionException(message);
		}
		
		// TODO validar el tipo del ACK
		
		log.info("Se recibieron "+ack.clinicalDocuments.clinicalDocument.size()+" estudios")
		
		// Transformo todos los estudios
		log.info("Transformando estudios...")
		ack.clinicalDocuments.clinicalDocument.each {com.janpix.webclient.regdoc.ClinicalDocumentDTO document->
			def study = JanpixAssembler.fromDocument(document)
			studies.add(study)
		}
		log.info("Estudios transformados correctamente")
		
		return studies
		
		/*AuthorCommand author = new AuthorCommand()
		HealthEntityCommand healthEntity = new HealthEntityCommand()
		healthEntity.oid = "HE_OID_0001"
		healthEntity.name = "HE_NAME_MOCK"
		author.healthEntity = healthEntity
		
		StudyCommand study1 = new StudyCommand()
		study1.date = new Date()
		study1.name = "Study Mock"
		study1.state = "Pendiente Mock"
		study1.comments = "Es un Mock"
		study1.uniqueId = "Mock Unique Id"
		study1.author = author
		
		studies.add(study1)
		studies.add(study1)
		
		return studies;*/
    }
	
	def approveStudy(StudyCommand study){
		if(!study || !study.uniqueId)
			throw new ErrorOnApproveDocumentJanpixException("No se envio ningun estudio para ser aprobado")
			
		AckMessage ack
		try{
			
		}catch(Exception ex){
			String message ="Error de conexión contra el Registro de Documentos: "+ex.message
			log.error(message, ex)
			throw new JanpixConnectionException(message);
		}
		
		// TODO validar ack message
	}
}
