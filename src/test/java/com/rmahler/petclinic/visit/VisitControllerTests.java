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

package com.rmahler.petclinic.visit;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.rmahler.petclinic.pet.PetRepository;
import com.rmahler.petclinic.vet.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.rmahler.petclinic.pet.Pet;
import com.rmahler.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
@WebMvcTest(VisitController.class)
class VisitControllerTests {

	private static final int TEST_PET_ID = 1;

	private static final int TEST_VISIT_ID = 3;

	private static final int TEST_VET_ID = 5;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VisitRepository visits;

	@MockBean
	private PetRepository pets;

	@MockBean
	private ClinicService clinicService;

	@BeforeEach
	void init() {
		Vet james = new Vet();
		james.setFirstName("James");
		james.setLastName("Carter");
		james.setId(1);
		Vet helen = new Vet();
		helen.setFirstName("Helen");
		helen.setLastName("Leary");
		helen.setId(2);

		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setTimeSlot(1);
		visit.setDate(LocalDate.now());

		given(this.pets.findById(TEST_PET_ID)).willReturn(new Pet());
		given(clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
		given(clinicService.findVets()).willReturn(Arrays.asList(james, helen));
		given(clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
		given(clinicService.findVisitsByPetId(TEST_PET_ID)).willReturn(Collections.singletonList(visit));
		given(clinicService.findFilledSlots(TEST_VET_ID, LocalDate.now())).willReturn(Collections.emptyList());
	}

	@Test
	void testInitNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/*/pets/{petId}/visits/new", TEST_PET_ID)).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID).param("name", "George")
				.param("description", "Visit Description").param("timeSlot", "1")
				.param("vetSelection", String.valueOf(TEST_VET_ID)).param("action", "save"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessNewVisitFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID).param("name", "George")
				.param("vetSelection", String.valueOf(TEST_VET_ID)).param("timeSlot", "1").param("action", "save"))
				.andExpect(model().attributeHasErrors("visit")).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

}
