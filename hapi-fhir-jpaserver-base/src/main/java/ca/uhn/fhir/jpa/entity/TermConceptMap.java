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
package ca.uhn.fhir.jpa.entity;

import ca.uhn.fhir.jpa.model.entity.BasePartitionable;
import ca.uhn.fhir.jpa.model.entity.IdAndPartitionId;
import ca.uhn.fhir.jpa.model.entity.ResourceTable;
import ca.uhn.fhir.util.ValidateUtil;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.length;

@Entity
@Table(
		name = "TRM_CONCEPT_MAP",
		uniqueConstraints = {
			@UniqueConstraint(
					name = "IDX_CONCEPT_MAP_URL",
					columnNames = {"PARTITION_ID", "URL", "VER"})
		},
		indexes = {
			// must have same name that indexed FK or SchemaMigrationTest complains because H2 sets this index
			// automatically
			@Index(name = "FK_TRMCONCEPTMAP_RES", columnList = "RES_ID")
		})
@IdClass(IdAndPartitionId.class)
public class TermConceptMap extends BasePartitionable implements Serializable {
	private static final long serialVersionUID = 1L;

	static final int MAX_URL_LENGTH = 200;
	public static final int MAX_VER_LENGTH = 200;

	/**
	 * Constructor
	 */
	public TermConceptMap() {
		super();
	}

	@Id()
	@SequenceGenerator(name = "SEQ_CONCEPT_MAP_PID", sequenceName = "SEQ_CONCEPT_MAP_PID")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CONCEPT_MAP_PID")
	@Column(name = "PID")
	private Long myId;

	@ManyToOne()
	@JoinColumns(
			value = {
				@JoinColumn(
						name = "RES_ID",
						referencedColumnName = "RES_ID",
						nullable = false,
						insertable = false,
						updatable = false),
				@JoinColumn(
						name = "PARTITION_ID",
						referencedColumnName = "PARTITION_ID",
						nullable = false,
						insertable = false,
						updatable = false)
			},
			foreignKey = @ForeignKey(name = "FK_TRMCONCEPTMAP_RES"))
	private ResourceTable myResource;

	@Column(name = "RES_ID", nullable = false)
	private Long myResourcePid;

	@Column(name = "SOURCE_URL", nullable = true, length = TermValueSet.MAX_URL_LENGTH)
	private String mySource;

	@Column(name = "TARGET_URL", nullable = true, length = TermValueSet.MAX_URL_LENGTH)
	private String myTarget;

	@Column(name = "URL", nullable = false, length = MAX_URL_LENGTH)
	private String myUrl;

	@Column(name = "VER", nullable = true, length = MAX_VER_LENGTH)
	private String myVersion;

	@OneToMany(mappedBy = "myConceptMap")
	private List<TermConceptMapGroup> myConceptMapGroups;

	public List<TermConceptMapGroup> getConceptMapGroups() {
		if (myConceptMapGroups == null) {
			myConceptMapGroups = new ArrayList<>();
		}

		return myConceptMapGroups;
	}

	public Long getId() {
		return myId;
	}

	public ResourceTable getResource() {
		return myResource;
	}

	public TermConceptMap setResource(ResourceTable theResource) {
		myResource = theResource;
		myResourcePid = theResource.getId().getId();
		setPartitionId(theResource.getPartitionId());
		return this;
	}

	public String getSource() {
		return mySource;
	}

	public TermConceptMap setSource(String theSource) {
		ValidateUtil.isNotTooLongOrThrowIllegalArgument(
				theSource,
				TermValueSet.MAX_URL_LENGTH,
				"Source exceeds maximum length (" + TermValueSet.MAX_URL_LENGTH + "): " + length(theSource));
		mySource = theSource;
		return this;
	}

	public String getTarget() {
		return myTarget;
	}

	public TermConceptMap setTarget(String theTarget) {
		ValidateUtil.isNotTooLongOrThrowIllegalArgument(
				theTarget,
				TermValueSet.MAX_URL_LENGTH,
				"Target exceeds maximum length (" + TermValueSet.MAX_URL_LENGTH + "): " + length(theTarget));
		myTarget = theTarget;
		return this;
	}

	public String getUrl() {
		return myUrl;
	}

	public TermConceptMap setUrl(@Nonnull String theUrl) {
		ValidateUtil.isNotBlankOrThrowIllegalArgument(theUrl, "theUrl must not be null or empty");
		ValidateUtil.isNotTooLongOrThrowIllegalArgument(
				theUrl, MAX_URL_LENGTH, "URL exceeds maximum length (" + MAX_URL_LENGTH + "): " + length(theUrl));
		myUrl = theUrl;
		return this;
	}

	public String getVersion() {
		return myVersion;
	}

	public TermConceptMap setVersion(String theVersion) {
		ValidateUtil.isNotTooLongOrThrowIllegalArgument(
				theVersion,
				MAX_VER_LENGTH,
				"Version exceeds maximum length (" + MAX_VER_LENGTH + "): " + length(theVersion));
		myVersion = theVersion;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("myId", myId)
				.append(myResource != null ? ("myResource=" + myResource.toString()) : ("myResource=(null)"))
				.append("myResourcePid", myResourcePid)
				.append("mySource", mySource)
				.append("myTarget", myTarget)
				.append("myUrl", myUrl)
				.append("myVersion", myVersion)
				.append(
						myConceptMapGroups != null
								? ("myConceptMapGroups - size=" + myConceptMapGroups.size())
								: ("myConceptMapGroups=(null)"))
				.toString();
	}
}
