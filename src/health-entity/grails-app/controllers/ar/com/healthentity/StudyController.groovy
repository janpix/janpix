package ar.com.healthentity

import grails.plugins.springsecurity.Secured

import org.codehaus.groovy.grails.validation.Validateable
import org.joda.time.LocalDate
import org.springframework.web.multipart.MultipartFile

import com.janpix.exceptions.ErrorUploadingDocumentException
import com.janpix.exceptions.PatientDoesNotExistsException
import com.janpix.exceptions.StudyDoesNotExistsException
import com.janpix.healthentity.StudyService

@Validateable
class CreateStudyCommand {
	Long patientId
	String studyTitle
	LocalDate creationDate
	Long studyType
	FormatType formatType
	String observations
	MultipartFile studyFile
	
	static constraints = {
		studyFile nullable: false
		observations  nullable: true, blank: false
		studyType nullable: false
		creationDate nullable: false
		studyTitle nullable: false, blank: false
		patientId nullable: false
		formatType nullable: false
	}
}

@Validateable
class DownloadRemoteCommand {
	String title
	Long idPatient
	Date creationDate
	String uniqueId
	String localDocId
	Long idStudyType
	String observation
	String filename

	static constraints = {
		title nullable: false, blank: false
		idPatient nullable: false
		uniqueId nullable: false
		localDocId nullable: false
		filename nullable: false
		observation nullable: true, blank: true
		idStudyType nullable: false
		creationDate nullable: false
	}
}

@Validateable
class UploadStudyCommand {
	Long id
}

@Secured("hasRole('HealthWorker')")
class StudyController {
	
	def studyTypeService
	def studyService
	def springSecurityService
	
	static allowedMethods = [
		create:"POST",
		uploadToJanpix: "POST",
		download: "GET",
		downloadRemote: "POST"
	]

	def create(CreateStudyCommand createStudyCommand) {
		withForm {
			createStudyCommand.validate()
			if (!createStudyCommand.hasErrors()) {
				studyService.createStudy(createStudyCommand, springSecurityService.currentUser, studyTypeService.findByStudyTypeId(createStudyCommand.studyType))
				flash.success = "Estudio creado correctamente"
				redirect mapping:'showPatient', id: createStudyCommand.patientId
			} else {
				render view:"/patient/show", model:[patientInstance: Patient.findById(createStudyCommand.patientId), studyTypeRoots: studyTypeService.listStudyTypeRoots(), createStudyModel: createStudyCommand]
			}
		}.invalidToken {
			flash.warning = "Ha intentado enviar información que ya ha enviado anteriormente. Si realmente desea ingresar datos nuevos, recargue la página."
			redirect mapping: 'showPatient', id: createStudyCommand.patientId
		}
	}
	
	def download(String id) {
		if(id != null){
			try {
				ClinicalDocument document = studyService.getDocumentByStudyId(id)
				String nameDocument = document.filename
				String mimeType = document.mimeType
				response.setContentLength((int)document.size)
				response.addHeader("Content-disposition", "attachment; filename=${nameDocument}")
				response.addHeader("Content-type", "${mimeType}")
				
				OutputStream out = response.getOutputStream()
				File f = new File("${studyService.uploadsPath}/${document.fileLocation}")
				out.write(f.readBytes())
				out.close()
			}
			catch (StudyDoesNotExistsException ex) {
				log.error("No se puede descargar archivo para studyId=${id}", ex)
				render status: 404
			}
		} else {
			log.error("id == null")
			render status: 404
		}
	}

	def uploadToJanpix(UploadStudyCommand uploadStudyCommand) {
		try{
			uploadStudyCommand.validate()
			if (!uploadStudyCommand.hasErrors()) {
				studyService.uploadStudy(uploadStudyCommand)
				respond uploadStudyCommand,[model:[upload_correct: true, idStudy: uploadStudyCommand.id], view: 'upload_study']
			} else {
				respond uploadStudyCommand,[model:[upload_correct: false, idStudy: uploadStudyCommand.id], view: 'upload_study', status: 500]
			}
		}
		catch(ErrorUploadingDocumentException e) {
			log.error("error en StudyController: ",e)
			render "Error al subir el documento. Esto puede deberse a un problema de conexion. Error: "+e
			return
		}
		catch(Exception e){
			log.error("error en StudyController: ",e)
			render "Error inesperado al subir el documento. Error: "+e
			return
		}
	}

	def refreshRemoteDocuments(Long id) {
		if (id == null) {
			log.error("id == null")
			render status: 400
		} else {
			try {
				def studies = studyService.downloadRemoteStudies(id)
				respond id,[view: "remote_documents", model:[idPatient: id, remoteStudies: studies]]
			}
			catch(PatientDoesNotExistsException ex) {
				log.error("Paciente no existe.", ex)
				render status: 404
			}
		}
	}

	def downloadRemote(DownloadRemoteCommand cmd) {
		cmd.validate()
		if (!cmd.hasErrors()) {
			def remoteStudy = studyService.obtainRemoteStudyForPatient(cmd)
			flash.success = "El estudio \"${remoteStudy.title}\" se pudo descargar correctamente"
		} else {
			log.error("error validating DownloadRemoteCommand")
			cmd.errors.each {
				log.error("validation error: ${it}")
			}
			flash.warning = "El archivo no se pudo descargar"
		}
		render view:"/patient/show_documents", model:[patientInstance: Patient.findById(cmd.idPatient), urldownload: studyService.uploadsPath]
	}

}
