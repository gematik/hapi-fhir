/*-
 * #%L
 * HAPI FHIR Subscription Server
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
package ca.uhn.fhir.jpa.subscription.match.matcher.matching;

import ca.uhn.fhir.jpa.searchparam.matcher.InMemoryMatchResult;
import ca.uhn.fhir.jpa.searchparam.matcher.InMemoryResourceMatcher;
import ca.uhn.fhir.jpa.subscription.match.matcher.subscriber.SubscriptionCriteriaParser;
import ca.uhn.fhir.jpa.subscription.model.CanonicalSubscription;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionStrategyEvaluator {

	@Autowired
	private InMemoryResourceMatcher myInMemoryResourceMatcher;

	/**
	 * Constructor
	 */
	public SubscriptionStrategyEvaluator() {
		super();
	}

	public SubscriptionMatchingStrategy determineStrategy(CanonicalSubscription theSubscription) {
		if (theSubscription.isTopicSubscription()) {
			return SubscriptionMatchingStrategy.TOPIC;
		}
		String criteriaString = theSubscription.getCriteriaString();
		return determineStrategy(criteriaString);
	}

	public SubscriptionMatchingStrategy determineStrategy(String criteriaString) {
		SubscriptionCriteriaParser.SubscriptionCriteria criteria = SubscriptionCriteriaParser.parse(criteriaString);
		if (criteria == null) {
			return SubscriptionMatchingStrategy.DATABASE;
		}
		if (criteria.getCriteria() == null) {
			return SubscriptionMatchingStrategy.IN_MEMORY;
		} else {
			InMemoryMatchResult result = myInMemoryResourceMatcher.canBeEvaluatedInMemory(criteriaString);
			if (result.supported()) {
				return SubscriptionMatchingStrategy.IN_MEMORY;
			} else {
				return SubscriptionMatchingStrategy.DATABASE;
			}
		}
	}
}
