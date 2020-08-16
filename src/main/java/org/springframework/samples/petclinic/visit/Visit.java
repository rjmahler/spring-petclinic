/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.visit;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Dave Syer
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotEmpty
	@Column(name = "description")
	private String description;

	@Column(name = "pet_id", nullable = false)
	private Integer petId;

	@Column(name = "vet_id", nullable = false)
	private Integer vetId;

	@NotNull
	@Column(name = "time_slot", nullable = false)
	private Integer timeSlot;

	@Transient
	private Boolean isCurrent;

	@Transient
	private String timeSlotDescription;

	/**
	 * Creates a new instance of Visit for the current date
	 */
	public Visit() {
		this.date = LocalDate.now();
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPetId() {
		return this.petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public Integer getVetId() {
		return this.vetId;
	}

	public void setVetId(Integer vetId) {
		this.vetId = vetId;
	}

	public Integer getTimeSlot() {
		return this.timeSlot;
	}

	public void setTimeSlot(Integer timeSlot) {
		this.timeSlot = timeSlot;
	}

	// called by the ui
	public Boolean getIsCurrent() {
		if (isCurrent == null) {
			isCurrent = this.date.isAfter(LocalDate.now());
		}
		return isCurrent;
	}

	// called by the ui
	public String getTimeSlotDescription() {
		if (timeSlotDescription == null) {
			timeSlotDescription = TimeSlotFormatter.getTimeSlotDescription(this.timeSlot);
		}
		return timeSlotDescription;
	}

}
