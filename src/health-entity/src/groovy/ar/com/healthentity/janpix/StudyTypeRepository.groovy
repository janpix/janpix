package ar.com.healthentity.janpix

import ar.com.healthentity.StudyType;

class StudyTypeRepository {
	
	def listStudyTypes() {
		StudyType.all
	}
	
	def listStudyTypeRoots() {
		def query = StudyType.where {
			father == null
		}
		query.list()
	}
	
	def findById(Long id) {
		StudyType.findById(id)
	}
	
	def findByStudyTypeId(Long id) {
		StudyType.findByIdStudyType(id)
	}
}
