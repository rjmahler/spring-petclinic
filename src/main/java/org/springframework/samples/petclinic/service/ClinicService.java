/*
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.samples.petclinic.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Facade interface for the controllers
 *
 * @author Richard Mahler
 */
public interface ClinicService {

	// owners
	Owner findOwnerById(int id) throws DataAccessException;

	void saveOwner(Owner owner) throws DataAccessException;

	Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;

	// pets
	Pet findPetById(int id) throws DataAccessException;

	Collection<PetType> findPetTypes() throws DataAccessException;

	void savePet(Pet pet) throws DataAccessException;

	// vets
	Collection<Vet> findVets() throws DataAccessException;

	Vet findVetById(int id) throws DataAccessException;

	Collection<Specialty> getVetSpecialties() throws DataAccessException;

	void saveVet(Vet vet) throws DataAccessException;

	// visits
	Collection<Integer> findFilledSlots(int vetId, LocalDate day);

	void cancelVisit(int visitId);

	Collection<Visit> findVisitsByPetId(int petId);

	void saveVisit(Visit visit) throws DataAccessException;

	Visit findVisitById(int visitId);

}
