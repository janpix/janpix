package com.janpix.healthentity

import java.util.List;

import grails.transaction.Transactional
import ar.com.healthentity.ClinicalDocument
import ar.com.healthentity.Patient
import ar.com.healthentity.janpix.utils.JanpixAssembler;

import com.janpix.exceptions.JanpixConnectionException
import com.janpix.exceptions.JanpixDuplicatePatientException
import com.janpix.exceptions.JanpixException
import com.janpix.exceptions.JanpixPossibleMatchingPatientException
import com.janpix.servidordocumentos.dto.ClinicalDocumentDTO
import com.janpix.servidordocumentos.dto.message.RetrieveDocumentRequest
import com.janpix.webclient.rup.AckMessage
import com.janpix.webclient.rup.AckQueryPatientMessage
import com.janpix.webclient.rup.AddPatientRequestMessage
import com.janpix.webclient.rup.GetAllPossibleMatchedPatientsRequestMessage
import com.janpix.webclient.rup.PatientDTO;
import com.janpix.webclient.rup.TypeCode



@Transactional
class JanpixService {
	
	def janpixRepodocServiceClient
	def janpixPixManagerServiceClient
	def grailsApplication
	
	/**
	 * Agrega un nuevo paciente en el RUP
	 * @param patient
	 */
	void addNewPatient(Patient patient){
		AckMessage ack = null
		try{
			AddPatientRequestMessage requestMessage = new AddPatientRequestMessage()
			requestMessage.person = JanpixAssembler.toPerson(patient)
			requestMessage.healthEntity = JanpixAssembler.toAssigningAuthority(grailsApplication.config.healthEntity)
			requestMessage.organizationId = patient.id
			
			log.info("Agregando paciente ["+patient+"] al Registro Unico de Pacientes")
			ack = janpixPixManagerServiceClient.addNewPatient(requestMessage)
		}
		catch(Exception ex){
			String message ="Error de conexión contra el RUP: "+ex.message
			log.error(message)
			throw new JanpixConnectionException(message);
		}
		
		this.validateACKMessageRUP(ack)
	}
	
	/**
	 * Agrega un nuevo paciente en el RUP sin validar posibles matcheos
	 * @param patient
	 */
	void addNewPatientWithoutValidation(Patient patient){
		
	}
	
	/**
	 * Retorna todos los pacientes que matchean con el paciente pasado por parametro
	 * @param patient
	 * @return
	 */
	List<Patient> getAllPossibleMatchedPatients(Patient patient){
		AckQueryPatientMessage ack = null
		try{
			// Se genera el mensaje
			GetAllPossibleMatchedPatientsRequestMessage requestMessage = new GetAllPossibleMatchedPatientsRequestMessage()
			requestMessage.person = JanpixAssembler.toPerson(patient)
			
			// Se realiza la consulta
			log.info("Consultando por posibles matcheos del paciente "+patient)
			ack = janpixPixManagerServiceClient.getAllPossibleMatchedPatients(requestMessage)
						
		}
		catch(Exception ex){
			String message ="Error de conexión contra el RUP: "+ex.message
			log.error(message)
			throw new JanpixConnectionException(message);
		}
		
		// Se valida el ACK
		this.validateACKQueryMessageRUP(ack)
		
		// Se genera la respuesta
		List<PatientDTO> matchedPatients = ack.patients.patient
		log.info("Se obtuvieron "+matchedPatients.size()+" matcheos")
		
		List<Patient> patients = new ArrayList<Patient>();
		matchedPatients.each { PatientDTO it->
			patients.add(JanpixAssembler.fromPatient(it))
		}

		return patients
	}
	
	/**
	 * Retorna el documento del Repositorio de Documentos
	 * que contiene el uuid pasado
	 */
	ClinicalDocument getDocumentByUUID(String uuid){
		try{
			RetrieveDocumentRequest requestMessage = new RetrieveDocumentRequest(uuid:uuid)
			
			log.info("Consultando Repositorio de documentos por el uuid:"+uuid)
			AckMessage ack =  janpixRepodocServiceClient.retrieveDocument(requestMessage)
			if(ack.typeCode != AckMessage.TypeCode.SuccededRetrieve){
				log.error("Error al obtener el documento. Error:"+ack.typeCode.toString()+". Mensaje:"+ack.text)
				return null;
			}
			
			log.info("Documento obtenido correctamente")
			ClinicalDocument clinicalDocument = JanpixAssembler.fromDocument(ack.clinicalDocument)
			return clinicalDocument;
		}
		catch(Exception ex){
			String message ="Error de conexión contra el repositorio: "+ex.message 
			log.error(message)
			throw new JanpixException(message);
		}
	}
	
	/**
	 * Sube un documento clinico asociado a un paciente
	 * @param clinicalDocument
	 * @return
	 */
    Boolean UploadDocument(ClinicalDocumentDTO clinicalDocument){
    
	}
	
	/**
	 * Valida los posibles valores que vienen en un ACK del RUP
	 * @param ack
	 */
	private void validateACKMessageRUP(AckMessage ack){
		if(ack.typeCode == TypeCode.SUCCEDED_CREATION){
			log.info("Paciente agregado correctamente")
			return
		}
		
		log.error("Error al agregar al paciente. Error:"+ack.typeCode.toString()+". Mensaje:"+ack.text)
		switch(ack.typeCode){
			case TypeCode.POSSIBLE_MATCHING_PATIENTS_ERROR: 
				throw new JanpixPossibleMatchingPatientException("Existen pacientes que se asemejan al paciente que intenta agregar pero no se puede determinar con precisión");
				break;
				
			case TypeCode.DUPLICATE_PATIENT_ERROR:
				throw new JanpixPossibleMatchingPatientException("Existen pacientes que se asemejan al paciente que intenta agregar pero no se puede determinar con precisión");
				break;
			
			case TypeCode.IDENTIFIER_ERROR:
				throw new JanpixDuplicatePatientException("El paciente ya se encuentra ingresado");
				break;
				
			default :
				throw new JanpixException(ack.text);
				break;
		}
	}
	
	/**
	 * Valida los posibles valores que vienen en un ACK del RUP
	 * @param ack
	 */
	private void validateACKQueryMessageRUP(AckQueryPatientMessage ack){
		if(ack.typeCode == TypeCode.SUCCEDED_QUERY){
			log.info("Query realizada correctamente")
			return
		}
		
		log.error("Error al realizar la query. Error:"+ack.typeCode.toString()+". Mensaje:"+ack.text)
		throw new JanpixException(ack.text);
	}
}
