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
package com.rmahler.petclinic.pet;

import com.rmahler.petclinic.model.NamedEntity;
import com.rmahler.petclinic.owner.Owner;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.format.annotation.DateTimeFormat;
import com.rmahler.petclinic.visit.Visit;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Simple business object representing a pet.
 *
 * @author Richard Mahler
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
@Getter
@Setter
public class Pet extends NamedEntity {

	@Column(name = "birth_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	@ManyToOne
	@JoinColumn(name = "type_id")
	private PetType type;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@Transient
	private Set<Visit> visits = new LinkedHashSet<>();

	protected Set<Visit> getVisitsInternal() {
		if (this.visits == null) {
			this.visits = new HashSet<>();
		}
		return this.visits;
	}

	public void setVisitsInternal(Collection<Visit> visits) {
		this.visits = new LinkedHashSet<>(visits);
	}

	public List<Visit> getVisits() {
		List<Visit> sortedVisits = new ArrayList<>(getVisitsInternal());
		PropertyComparator.sort(sortedVisits, new MutableSortDefinition("date", false, false));
		return Collections.unmodifiableList(sortedVisits);
	}

	public void addVisit(Visit visit) {
		getVisitsInternal().add(visit);
		visit.setPetId(this.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != Pet.class) {
			return false;
		}
		Pet otherPet = (Pet) obj;
		return this.getName() != null && this.getName().equalsIgnoreCase(otherPet.getName())
			&& this.getOwner() != null && this.getOwner().equals(otherPet.getOwner());
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 + hash + (this.getName() != null ? this.getName().hashCode() : 0);
		hash = 89 + hash + (this.getOwner() !=  null ? this.getOwner().hashCode() : 0);
		return hash;
	}

}
