package com.janpix.healthentity

import org.springframework.web.multipart.MultipartFile;

import ar.com.healthentity.ClinicalDocument
import ar.com.healthentity.CreateStudyCommand;
import ar.com.healthentity.Study
import ar.com.healthentity.StudyType;
import ar.com.healthentity.User
import ar.com.healthentity.Patient
import ar.com.healthentity.janpix.StudyTypeRepository;
import ar.com.healthentity.janpix.utils.JanpixAssembler;

import com.janpix.exceptions.StudyDoesNotExistsException
import grails.transaction.Transactional

@Transactional
class StudyService {
	
	def grailsApplication
	def janpixService
	def springSecurityService

    def createStudy(CreateStudyCommand cmd, User author, StudyType type) {
		def random = new Random().nextInt().abs().toString()
		def randomName = "${random}${cmd.studyFile.name}"
		copy(cmd.studyFile, randomName)
		def cd = new ClinicalDocument(
			filename: cmd.studyFile.name,
			mimeType: cmd.studyFile.contentType,
			size: cmd.studyFile.size,
			fileLocation: randomName
		)
		cd.save(failOnError: true)
		def study = new Study(
			date: cmd.creationDate.toDateTimeAtStartOfDay().toDate(),
			title: cmd.studyTitle,
			observation: cmd.observations,
			document: cd,
			type: type
		)
		def patient = Patient.findById(cmd.patientId)
		patient.addToStudies(study)
		study.save(failOnError: true)
    }

	def uploadStudy(def cmd) {
		def study = Study.get(cmd.id)
		if (!study)
			throw new StudyDoesNotExistsException()
		
		def currentUser = springSecurityService.currentUser
			
		janpixService.uploadDocument(study,currentUser)
	}
	
	private def copy(def file, def fileRandomName) {
		final def path = grailsApplication.mainContext.servletContext.getRealPath("/uploads")
		final def destination = new File(path, fileRandomName)
		file.transferTo(destination)
	}
}