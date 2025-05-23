package ca.uhn.fhir.jpa.dao.r4;

/*
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2024 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.StringType;

@ResourceDef(name = "Observation", profile = CustomObservationR4.PROFILE)
public class CustomObservationR4 extends Observation {

	public static final String PROFILE = "http://custom_ObservationR4";
	
	private static final long serialVersionUID = 1L;

	@Extension(definedLocally = false, isModifier = false, url = "http://eyeColour")
	@Child(name = "eyeColour")
	private StringType myEyeColour;

	public StringType getEyeColour() {
		return myEyeColour;
	}

	public void setEyeColour(StringType theEyeColour) {
		myEyeColour = theEyeColour;
	}
	
}
