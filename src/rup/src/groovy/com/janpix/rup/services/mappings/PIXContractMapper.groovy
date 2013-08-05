package com.janpix.rup.services.mappings

import javax.xml.bind.annotation.XmlRootElement

import org.hl7.v3.*

import com.janpix.hl7dto.hl7.v3.datatypes.AD
import com.janpix.hl7dto.hl7.v3.datatypes.CE
import com.janpix.hl7dto.hl7.v3.datatypes.CS
import com.janpix.hl7dto.hl7.v3.datatypes.II
import com.janpix.hl7dto.hl7.v3.datatypes.PN
import com.janpix.hl7dto.hl7.v3.datatypes.TEL
import com.janpix.hl7dto.hl7.v3.datatypes.TS
import com.janpix.hl7dto.hl7.v3.datatypes.enums.CommunicationFunctionType
import com.janpix.hl7dto.hl7.v3.datatypes.enums.ParticipationTargetSubject
import com.janpix.hl7dto.hl7.v3.messages.Acknowledgement
import com.janpix.hl7dto.hl7.v3.messages.PatientOperationMessage;
import com.janpix.hl7dto.hl7.v3.messages.Device
import com.janpix.hl7dto.hl7.v3.messages.HL7MessageReceiver
import com.janpix.hl7dto.hl7.v3.messages.HL7MessageSender
import com.janpix.hl7dto.hl7.v3.messages.HL7OperationMessage
import com.janpix.hl7dto.hl7.v3.messages.Organization
import com.janpix.hl7dto.hl7.v3.messages.OtherIDs
import com.janpix.hl7dto.hl7.v3.messages.QueryControlActProcess;
import com.janpix.hl7dto.hl7.v3.messages.QueryPatientOperationMessage;
import com.janpix.hl7dto.hl7.v3.messages.RegistrationEvent
import com.janpix.hl7dto.hl7.v3.messages.Subject1
import com.janpix.hl7dto.hl7.v3.messages.Subject2
import com.janpix.hl7dto.hl7.v3.messages.ack.AcknowledgementDetail
import com.janpix.hl7dto.hl7.v3.messages.ack.AcknowledgmentMessage
import com.janpix.hl7dto.hl7.v3.messages.ack.AddPatientAcknowledgmentMessage;
import com.janpix.hl7dto.hl7.v3.messages.ack.QueryAckControlActProcess;
import com.janpix.hl7dto.hl7.v3.messages.ack.QueryAckValue
import com.janpix.hl7dto.hl7.v3.messages.ack.QueryAcknowledgmentMessage;
import com.janpix.hl7dto.hl7.v3.messages.ack.TargetMessage
import com.janpix.hl7dto.hl7.v3.messages.query.QueryByParameter
import com.janpix.hl7dto.hl7.v3.messages.query.QueryParameter
import com.janpix.hl7dto.hl7.v3.messages.query.QueryParameterList
import com.janpix.rup.empi.Address
import com.janpix.rup.empi.AssigningAuthority
import com.janpix.rup.empi.City
import com.janpix.rup.empi.Country
import com.janpix.rup.empi.ExtendedDate
import com.janpix.rup.empi.HealthEntity
import com.janpix.rup.empi.Identifier
import com.janpix.rup.empi.Patient
import com.janpix.rup.empi.Person
import com.janpix.rup.empi.PersonName
import com.janpix.rup.empi.PhoneNumber
import com.janpix.rup.empi.Province
import com.janpix.rup.exceptions.MessageMappingException
import com.janpix.rup.services.contracts.ACKMessage

/**	
 * Maps domain objects to PIX Web service
 */
class PIXContractMapper {
	
	def hl7Helper
	def actualDate
	def uuidGenerator
	
	
	II getMessageIdentifier(HL7OperationMessage message) {
		return message.id
	}
	
	String getPatientId(PatientOperationMessage message) {
		return message.controlActProcess.subject[0].registrationEvent.subject1.patient.id[0].extension
	}
	
	HealthEntity mapSenderToHealthEntity(HL7OperationMessage message) {
		def oid = message.sender.device.id[0].root
		def name = message.sender.device.id[0].assigningAuthorityName
		return new HealthEntity(name: name, oid: oid)
	}
	
	HL7MessageReceiver mapHealthEntityToACKReceiver(AssigningAuthority authority) {
		def receiver = new HL7MessageReceiver()
		receiver.typeCode = CommunicationFunctionType.RCV
		receiver.device = buildACKDevice(authority)
		return receiver
	}
	
	HL7MessageSender mapAssigningAuthorityToACKSender(AssigningAuthority authority) {
		def sender = new HL7MessageSender()
		sender.typeCode = CommunicationFunctionType.SND
		sender.device = buildACKDevice(authority)
		return sender
	}
	
	Device buildACKDevice(AssigningAuthority authority) {
		def device = new Device()
		device.determinerCode = "INSTANCE"
		device.id.add(new II(root: authority.oid, assigningAuthorityName: authority.name))
		return device
	}
	
	AddPatientAcknowledgmentMessage mapACKMessageToHL7AcceptAcknowledgmentMessage(ACKMessage ackMessage, II messageIdentifier, AssigningAuthority receiver, AssigningAuthority sender) {
		def messageName = "MCCI_IN000002UV01"
		def ackHl7 = new AddPatientAcknowledgmentMessage()
		buildBaseAckMessage(ackHl7, messageName, receiver, sender)
		def ackHl7spec = buildAcknowledgement(ackMessage)
		ackHl7spec.targetMessage = buildTargetMessage(messageIdentifier)
		ackHl7.acknowledgement.add(ackHl7spec)
		
		return ackHl7
	}

	private void buildBaseAckMessage(AcknowledgmentMessage ackHl7, String messageName, AssigningAuthority receiver, AssigningAuthority sender) {
		ackHl7.id = new II(root: uuidGenerator())
		ackHl7.creationTime = hl7Helper.buildHl7DateTime(actualDate())
		ackHl7.interactionId = hl7Helper.buildInteractionId(messageName)
		ackHl7.processingCode = hl7Helper.buildProcessingCode()
		ackHl7.processingModeCode = hl7Helper.buildProcessingModeCode()
		ackHl7.acceptAckCode = hl7Helper.buildAcceptAckCode()
		ackHl7.receiver.add(mapHealthEntityToACKReceiver(receiver))
		ackHl7.sender = mapAssigningAuthorityToACKSender(sender)
	}
	
	private Acknowledgement buildAcknowledgement(ACKMessage ackMessage) {
		def ackHl7spec = new Acknowledgement()
		switch (ackMessage.typeCode) {
			case [ ACKMessage.TypeCode.SuccededCreation, ACKMessage.TypeCode.SuccededInsertion, ACKMessage.TypeCode.SuccededQuery ]:
				ackHl7spec.typeCode = new CS(code:"CA")
				break
			case [ ACKMessage.TypeCode.PossibleMatchingPatientsError, ACKMessage.TypeCode.ShortDemographicError, ACKMessage.TypeCode.IdentifierError ]:
				ackHl7spec.typeCode = new CS(code:"CR")
				break
			default:
				ackHl7spec.typeCode = new CS(code:"CE")
		}
		ackHl7spec.acknowledgementDetail.add(buildAcknowledgementDetail(ackMessage))
		return ackHl7spec
	}
	
	private TargetMessage buildTargetMessage(II identifier) {
		def targetMessage = new TargetMessage()
		targetMessage.id = identifier
		return targetMessage
	}
	
	private AcknowledgementDetail buildAcknowledgementDetail(ACKMessage ackMessage) {
		def ackDetail = new AcknowledgementDetail()
		ackDetail.code = new CE(code: ackMessage.typeCode.exceptionCode())
		return ackDetail
	}

	/**
	 * maps PRPAIN201301UV02 message to Person
	 * @param inPatientMessage {@link org.hl7.v3.PRPAIN201301UV02 PRPAIN201301UV02} object
	 * @return {@link com.janpix.rup.empi.Person Person} mapped from PRPAIN201301UV02.
	 */
	Person mapPersonFromhl7v3AddNewPatientMessage(HL7OperationMessage inPatientMessage) {
		RegistrationEvent regEvent = inPatientMessage.controlActProcess.subject[0].registrationEvent
		def patient = regEvent.subject1.patient
		com.janpix.hl7dto.hl7.v3.messages.Person patientPerson = patient.patientPerson
		
		//set name
		def person = new Person()
		person.givenName = new PersonName()
		person.givenName.firstName = patientPerson.name[0]?.given
		person.givenName.lastName = patientPerson.name[0]?.family
		person.administrativeSex =  patientPerson.administrativeGenderCode.code
		person.maritalStatus = patientPerson.maritalStatusCode?.code
		// TODO person.birthplace = placeService.findByPlace(cityName, provinceName, countryName)patientPerson.birthPlace?.addr.city
		person.birthdate = convertToExtendedDateFromTS(patientPerson.birthTime)
		person.multipleBirthIndicator = patientPerson.multipleBirthInd ? patientPerson.multipleBirthInd.value : false
		if (patientPerson.deceasedInd?.value?.value) {
			person.deathdate = convertToExtendedDateFromTS(patientPerson.deceasedTime)
		}
		patientPerson.addr.each { AD it ->
			person.addToAddresses(convertToAddress(it))
		}
		patientPerson.telecom.each { TEL it ->
			person.addToPhoneNumbers(convertToPhoneNumber(it))
		}
		return person
	}
	
	private PhoneNumber convertToPhoneNumber(TEL hl7Telephone) {
		return new PhoneNumber(number: hl7Telephone.value)
	}
	
	private Address convertToAddress(AD hl7Address) {
		def address = new Address()
		def street = hl7Address.streetAddressLine?.split(" ", 2)
		if (street) {
			if (!(street[0]?.isNumber()))
				throw new MessageMappingException('PRPAIN201301UV02 address list field "streetAddressLine" must contain format "<number> <street name>", notice a space character between fields.')
			address.number = street[0]
			address.street = street[1]
		}
		def additionalLocator = hl7Address.additionalLocator?.split(",", 2)
		if (additionalLocator) { 
			address.floor = additionalLocator[0]
			address.department = additionalLocator[1].trim()
		}
		def cityName = hl7Address.city
		def provinceName = hl7Address.province
		def countryName = hl7Address.country
		def city = new City(name: cityName, province: new Province(name: provinceName, country: new Country(name: countryName)))
		address.city = city
		address.zipCode = hl7Address.postalCode
		return address
	}
	
	private ExtendedDate convertToExtendedDateFromTS(TS hl7Date) {
		def precission = ExtendedDate.TYPE_PRECISSION_UNKNOWN
		def year = 1900
		def month = 1
		def day = 1
		if (hl7Date?.value?.length() >= 4) {
			year = hl7Date.value.substring(0, 4) as int
			precission = ExtendedDate.TYPE_PRECISSION_YEAR
		}
		if (hl7Date?.value?.length() >= 6) {
			month = hl7Date.value.substring(4, 6) as int
			precission = ExtendedDate.TYPE_PRECISSION_MONTH
		}
		if (hl7Date?.value?.length() >= 8) {
			day = hl7Date.value.substring(6, 8) as int
			precission = ExtendedDate.TYPE_PRECISSION_DAY
		}
		def date = buildDate(year, month, day)
		return new ExtendedDate(date: date, precission: precission)
	}
	
	private static Date buildDate(int year, int month, int day) {
		def calendar = new GregorianCalendar(year, month, day)
		return calendar.getTime()
	}
	
	public String mapIdentifierFromhl7QueryMessage(QueryControlActProcess controlActProcess) {
		return controlActProcess.queryByParameter.parameterList.patientIdentifier[0].value[0].extension
	}
	
	public II getQueryId(QueryPatientOperationMessage message) {
		return message.controlActProcess.queryByParameter.queryId;
	}
	
	public QueryAcknowledgmentMessage mapQueryACKMessageToHL7QueryAcknowledgmentMessage(ACKMessage ack, II messageIdentifier, HealthEntity receiver, AssigningAuthority sender, II queryId) {
		def returnAck = new QueryAcknowledgmentMessage()
		def messageName = "PRPA_IN201310UV02"
		buildBaseAckMessage(returnAck, messageName, receiver, sender)
		def ackHl7spec = buildAcknowledgement(ack)
		ackHl7spec.targetMessage = buildTargetMessage(messageIdentifier)
		returnAck.acknowledgement.add(ackHl7spec)
		def queryAck = new QueryAckValue(queryId : queryId, queryResponseCode:  buildQueryResponseCode(ack))
		returnAck.controlActProcess = buildQueryAckControlActProcess(ack, queryAck)
		return returnAck
	}
	
	private CS buildQueryResponseCode(ACKMessage ack) {
		def queryResponseCode = new CS()
		switch (ack.typeCode) {
			case [ ACKMessage.TypeCode.SuccededQuery ]:
				if (ack.patient != null)
					queryResponseCode.code = "OK"
				else
					queryResponseCode.code = "NF"
				break;
			default:
				queryResponseCode.code = "QE"
		}
		return queryResponseCode
	}
	
	private QueryAckControlActProcess buildQueryAckControlActProcess(ACKMessage ack, QueryAckValue queryAck) {
		def queryAckCAP = new QueryAckControlActProcess()
		queryAckCAP.queryAck = queryAck
		ack.patient.identifiers.each { Identifier identifier ->
			queryAckCAP.queryByParameter = new QueryByParameter()
			queryAckCAP.queryByParameter.parameterList = new QueryParameterList()
			queryAckCAP.queryByParameter.parameterList.patientIdentifier = new ArrayList<QueryParameter>()
			def queryAckIdentifier = new II(root: identifier.assigningAuthority.oid, extension:identifier.number)
			queryAckCAP.queryByParameter.parameterList.patientIdentifier.add(new QueryParameter(value: [ queryAckIdentifier ], semanticsText: [ "Patient.Id" ]))
		}
		queryAckCAP.queryByParameter.statusCode = new CS(code: "new")
		queryAckCAP.subject.add(mapPatientToSubject(ack.patient))
		return queryAckCAP
	}
	
	private Subject1 mapPatientToSubject(Patient patient) {
		Subject1 subject = new Subject1()
		subject.registrationEvent = new RegistrationEvent()
		subject.registrationEvent.classCode.add("REG")
		subject.registrationEvent.moodCode.add("EVN")
		subject.registrationEvent.statusCode = new CS(code: "active")
		subject.registrationEvent.subject1 = new Subject2()
		subject.registrationEvent.subject1.typeCode = ParticipationTargetSubject.SBJ
		subject.registrationEvent.subject1.patient = new com.janpix.hl7dto.hl7.v3.messages.Patient()
		subject.registrationEvent.subject1.patient.classCode.add("PAT")
		Identifier rupId = patient.identifiers.find { Identifier identifier ->	identifier.assigningAuthority.name == "RUP" } // FIXME pasar constante por configuracion del proyecto
		subject.registrationEvent.subject1.patient.id.add(new II(root:rupId.assigningAuthority.oid, extension:rupId.number, assigningAuthorityName: rupId.assigningAuthority.name))
		subject.registrationEvent.subject1.patient.statusCode = new CS(code:"active")
		com.janpix.hl7dto.hl7.v3.messages.Person person = new com.janpix.hl7dto.hl7.v3.messages.Person()
		PN patientName = new PN()
		patientName.given = patient.givenName.firstName
		patientName.family = patient.givenName.lastName
		person.name.add(patientName)
		person.administrativeGenderCode = new CE(code: patient.administrativeSex)
		person.birthTime = hl7Helper.buildHl7DateTime(patient.birthdate.date)
		patient.addresses.each { Address address ->
			AD ad = new AD()
			ad.streetAddressLine = "${address.street} ${address.number}"
			ad.additionalLocator = "${address.floor} ${address.department}"
			ad.city = "${address.city.name}"
			ad.province = "${address.city.province.name}"
			ad.country = "${address.city.province.country.name}"
			ad.postalCode = "${address.zipCode}"
			person.addr.add(ad)	
		}
		OtherIDs otherId = new OtherIDs()
		otherId.classCode.add("PAT")
		patient.identifiers.each { Identifier identifier ->
			otherId.id.add(new II(root: identifier.assigningAuthority.oid, extension: identifier.number, assigningAuthorityName: identifier.assigningAuthority.name))
			otherId.scopingOrganization = new Organization()
			otherId.scopingOrganization.classCode = "ORG"
			otherId.scopingOrganization.determinerCode = "INSTANCE"
			otherId.scopingOrganization.id.add( new II(root: identifier.assigningAuthority.oid))
			person.asOtherIDs.add(otherId)
		}
		subject.registrationEvent.subject1.patient.patientPerson = person
		return subject
	}
	
	public void validateHl7V3UpdatePatientMessage(PatientOperationMessage updatePatientMessage) {
		validateHl7V3AddNewPatientMessage(updatePatientMessage)
		if (updatePatientMessage.controlActProcess.subject[0].registrationEvent.custodian == null)
			throw new MessageMappingException("PRPAIN201302UV02 message must contain custodian")
	}
	
	
	public void validateHl7V3PatientQueryMessage(QueryPatientOperationMessage queryPatientOperationMessage) {
		def identifier = queryPatientOperationMessage.controlActProcess.queryByParameter.parameterList.patientIdentifier
		if (identifier.isEmpty() || identifier[0].value.isEmpty()) {
			throw new MessageMappingException("PRPAIN201309UV02 message must contain one identifier")
		}
		if (identifier.size() > 1 || identifier[0].value.size() > 1) {
			throw new MessageMappingException("PRPAIN201309UV02 message can't contain more than one identifier")
		}
	}
	
	public void validateHl7V3AddNewPatientMessage(HL7OperationMessage inPatientMessage) {
		if (inPatientMessage.controlActProcess.subject.isEmpty())
			throw new MessageMappingException("PRPAIN201301UV02 message must contain one subject")
		if (inPatientMessage.controlActProcess.subject.size() > 1)
			throw new MessageMappingException("PRPAIN201301UV02 message can't contain more than one subject at this implementation")
		if (inPatientMessage.controlActProcess.subject[0].registrationEvent.subject1.patient.patientPerson.administrativeGenderCode == null)
			throw new MessageMappingException("PRPAIN201301UV02 message must contain an administrativeGenderCode")
		
		if (inPatientMessage.sender == null || inPatientMessage.sender.device.id.size() != 1) {
			throw new MessageMappingException("PRPAIN201301UV02 sender must exists and have only one unique identifier")
		} 
	}
	
}
