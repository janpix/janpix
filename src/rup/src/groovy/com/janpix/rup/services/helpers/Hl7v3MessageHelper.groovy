package com.janpix.rup.services.helpers

import org.hl7.v3.II;
import org.hl7.v3.TS

class Hl7v3MessageHelper {
	def uuidGenerator
	
	II generateUniqueII() {
		return new II(root: uuidGenerator())
	}
	
	TS buildHl7DateTime(Date date) {
		return new TS(value: "${date.gcal.get(Calendar.YEAR)}${date.gcal.get(Calendar.MONTH)}${date.gcal.get(Calendar.DAY_OF_MONTH)}${date.gcal.get(Calendar.HOUR_OF_DAY)}${date.gcal.get(Calendar.MINUTE)}${date.gcal.get(Calendar.SECOND)}")
	}
	
	II buildInteractionId(String messageRootName) {
		return new II(root: "2.16.840.1.113883.1.6", extension: messageRootName)
	}
}