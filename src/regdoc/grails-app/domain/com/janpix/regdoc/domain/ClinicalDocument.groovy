package com.janpix.regdoc.domain

import com.janpix.regdoc.infrastructure.DocumentStateUserType

enum FormatType {
	PDF,
	ScannedLab
}

class ClinicalDocument {
	String uniqueId // Utilizado para referenciar otros docs
	String patientId // El paciente al que representa
	Author author // El autor del documento
	FileAttributes file // Atributos que pertenecen unicamente al archivo
	DocumentState state // Estado asignado por los auditores

	String comments // Comentarios sobre el documento
	String language

	Date documentCreationStarted
	Date documentCreationEnded

	ClinicalDocumentType documentType // Tipo de documento
	FormatType format // Indica el formato del documento. Tiene algo que ver con el formato del archivo, pero también tiene que ver con el tipo de documento.

	static hasMany = [
		relatedTo: DocumentRelationship
	]
	DocumentRelationship relatedFrom
	static mappedBy = [ relatedTo: 'parentDocument', relatedFrom: 'relatedDocument' ]

	static mapping = {
		state type: DocumentStateUserType, {
			column name: "state"
		}
	}
}
