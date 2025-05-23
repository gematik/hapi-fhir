/*-
 * #%L
 * HAPI FHIR - Master Data Management
 * %%
 * Copyright (C) 2014 - 2025 Smile CDR, Inc.
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
package ca.uhn.fhir.mdm.rules.config;

import ca.uhn.fhir.mdm.api.IMdmRuleValidator;
import ca.uhn.fhir.mdm.api.IMdmSettings;
import ca.uhn.fhir.mdm.api.MdmModeEnum;
import ca.uhn.fhir.mdm.rules.json.MdmRulesJson;
import ca.uhn.fhir.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static ca.uhn.fhir.mdm.api.MdmModeEnum.MATCH_AND_LINK;

@Component
public class MdmSettings implements IMdmSettings {
	public static final int DEFAULT_CANDIDATE_SEARCH_LIMIT = 10000;
	private final IMdmRuleValidator myMdmRuleValidator;

	private boolean myEnabled;
	private final int myConcurrentConsumers = MDM_DEFAULT_CONCURRENT_CONSUMERS;
	private String myScriptText;
	private String mySurvivorshipRules;
	private MdmRulesJson myMdmRules;
	private boolean myPreventEidUpdates;
	private String myGoldenResourcePartitionName;
	private boolean mySearchAllPartitionForMatch = false;
	private boolean myShouldAutoDeleteGoldenResources = true;
	private MdmModeEnum myMdmMode = MATCH_AND_LINK;

	/**
	 * If disabled, the underlying MDM system will operate under the following assumptions:
	 * <p>
	 * 1. Source resource may have more than 1 EID of the same system simultaneously.
	 * 2. During linking, incoming patient EIDs will be merged with existing Golden Resource EIDs.
	 */
	private boolean myPreventMultipleEids;

	/**
	 * When searching for matching candidates, this is the maximum number of candidates that will be retrieved.  If the
	 * number matched is equal to or higher than this, then an exception will be thrown and candidate matching will be aborted
	 */
	private int myCandidateSearchLimit = DEFAULT_CANDIDATE_SEARCH_LIMIT;

	@Autowired
	public MdmSettings(IMdmRuleValidator theMdmRuleValidator) {
		myMdmRuleValidator = theMdmRuleValidator;
	}

	@Override
	public boolean isEnabled() {
		return myEnabled;
	}

	public MdmSettings setEnabled(boolean theEnabled) {
		myEnabled = theEnabled;
		return this;
	}

	@Override
	public int getConcurrentConsumers() {
		return myConcurrentConsumers;
	}

	public String getScriptText() {
		return myScriptText;
	}

	public MdmSettings setScriptText(String theScriptText) throws IOException {
		myScriptText = theScriptText;
		setMdmRules(JsonUtil.deserialize(theScriptText, MdmRulesJson.class));
		return this;
	}

	@Override
	public MdmRulesJson getMdmRules() {
		return myMdmRules;
	}

	@Override
	public boolean isPreventEidUpdates() {
		return myPreventEidUpdates;
	}

	public MdmSettings setPreventEidUpdates(boolean thePreventEidUpdates) {
		myPreventEidUpdates = thePreventEidUpdates;
		return this;
	}

	public MdmSettings setMdmRules(MdmRulesJson theMdmRules) {
		myMdmRuleValidator.validate(theMdmRules);
		myMdmRules = theMdmRules;
		return this;
	}

	public boolean isPreventMultipleEids() {
		return myPreventMultipleEids;
	}

	@Override
	public String getRuleVersion() {
		return myMdmRules.getVersion();
	}

	public MdmSettings setPreventMultipleEids(boolean thePreventMultipleEids) {
		myPreventMultipleEids = thePreventMultipleEids;
		return this;
	}

	@Override
	public String getSurvivorshipRules() {
		return mySurvivorshipRules;
	}

	public void setSurvivorshipRules(String theSurvivorshipRules) {
		mySurvivorshipRules = theSurvivorshipRules;
	}

	@Override
	public int getCandidateSearchLimit() {
		return myCandidateSearchLimit;
	}

	public void setCandidateSearchLimit(int theCandidateSearchLimit) {
		myCandidateSearchLimit = theCandidateSearchLimit;
	}

	@Override
	public String getGoldenResourcePartitionName() {
		return myGoldenResourcePartitionName;
	}

	@Override
	public void setGoldenResourcePartitionName(String theGoldenResourcePartitionName) {
		myGoldenResourcePartitionName = theGoldenResourcePartitionName;
	}

	@Override
	public boolean getSearchAllPartitionForMatch() {
		return mySearchAllPartitionForMatch;
	}

	@Override
	public void setSearchAllPartitionForMatch(boolean theSearchAllPartitionForMatch) {
		mySearchAllPartitionForMatch = theSearchAllPartitionForMatch;
	}

	@Override
	public boolean isAutoExpungeGoldenResources() {
		return myShouldAutoDeleteGoldenResources;
	}

	@Override
	public void setAutoExpungeGoldenResources(boolean theShouldAutoExpunge) {
		myShouldAutoDeleteGoldenResources = theShouldAutoExpunge;
	}

	public void setMdmMode(MdmModeEnum theMdmMode) {
		myMdmMode = theMdmMode;
	}

	@Override
	public MdmModeEnum getMode() {
		return myMdmMode;
	}
}
