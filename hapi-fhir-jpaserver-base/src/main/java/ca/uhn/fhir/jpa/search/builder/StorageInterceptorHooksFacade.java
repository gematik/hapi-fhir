/*-
 * #%L
 * HAPI FHIR JPA Server
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
package ca.uhn.fhir.jpa.search.builder;

import ca.uhn.fhir.interceptor.api.HookParams;
import ca.uhn.fhir.interceptor.api.IInterceptorBroadcaster;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.interceptor.model.RequestPartitionId;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import ca.uhn.fhir.rest.server.util.CompositeInterceptorBroadcaster;
import ca.uhn.fhir.rest.server.util.ICachedSearchDetails;

/**
 * facade over raw hook interface
 */
public class StorageInterceptorHooksFacade {
	private final IInterceptorBroadcaster myInterceptorBroadcaster;

	public StorageInterceptorHooksFacade(IInterceptorBroadcaster theInterceptorBroadcaster) {
		myInterceptorBroadcaster = theInterceptorBroadcaster;
	}

	/**
	 * Interceptor call: STORAGE_PRESEARCH_REGISTERED
	 */
	public void callStoragePresearchRegistered(
			RequestDetails theRequestDetails,
			SearchParameterMap theParams,
			ICachedSearchDetails theSearch,
			RequestPartitionId theRequestPartitionId) {
		IInterceptorBroadcaster compositeBroadcaster =
				CompositeInterceptorBroadcaster.newCompositeBroadcaster(myInterceptorBroadcaster, theRequestDetails);
		if (compositeBroadcaster.hasHooks(Pointcut.STORAGE_PRESEARCH_REGISTERED)) {
			HookParams params = new HookParams()
					.add(ICachedSearchDetails.class, theSearch)
					.add(RequestDetails.class, theRequestDetails)
					.addIfMatchesType(ServletRequestDetails.class, theRequestDetails)
					.add(SearchParameterMap.class, theParams)
					.add(RequestPartitionId.class, theRequestPartitionId);
			compositeBroadcaster.callHooks(Pointcut.STORAGE_PRESEARCH_REGISTERED, params);
		}
	}
}
