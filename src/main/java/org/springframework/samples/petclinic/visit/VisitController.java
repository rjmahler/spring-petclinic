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

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

	private static final String PETS_CREATE_OR_UPDATE_VISITS_FORM = "pets/createOrUpdateVisitForm";

	private final ClinicService clinicService;

	@Autowired
	public VisitController(ClinicService clinicService) {
		this.clinicService = clinicService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("vets")
	public Collection<Vet> findOwner(Map<String, Object> model) {

		Collection<Vet> vets = this.clinicService.findVets();
		model.put("selectedvet", vets.iterator().next());

		return vets;
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId - of the pet to load
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("petId") int petId, Map<String, Object> model) {
		Pet pet = this.clinicService.findPetById(petId);
		Collection<Visit> visits = clinicService.findVisitsByPetId(petId);

		pet.setVisitsInternal(visits);
		model.put("pet", pet);
		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called
	@GetMapping("/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
		return PETS_CREATE_OR_UPDATE_VISITS_FORM;
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result,
			@RequestParam(value = "action", required = true) String action,
			@RequestParam(value = "vetSelection", required = true) String selectedVet, Map<String, Object> model) {

		if (selectedVet.isEmpty()) {
			return PETS_CREATE_OR_UPDATE_VISITS_FORM;
		}
		visit.setVetId(Integer.parseInt(selectedVet));

		if (action.equals("checkavail")) {
			int vetId = Integer.parseInt(selectedVet);
			model.put("selectedvet", getVetFromModelMap(model, vetId));

			LocalDate visitDate = visit.getDate();
			if (isVisitDayUnavailable(visitDate)) {
				model.put("availableslots", Collections.emptyList());
				return PETS_CREATE_OR_UPDATE_VISITS_FORM;
			}

			Collection<Integer> slotIds = clinicService.findFilledSlots(vetId, visit.getDate());
			Collection<TimeSlot> slots = TimeSlotFormatter.getAvailableSlots(slotIds);
			model.put("availableslots", slots);

			return PETS_CREATE_OR_UPDATE_VISITS_FORM;
		}
		else {
			if (result.hasErrors()) {
				return PETS_CREATE_OR_UPDATE_VISITS_FORM;
			}
			this.clinicService.saveVisit(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	// can't book visit in the past, and assume no same day appointments nor weekends
	private boolean isVisitDayUnavailable(LocalDate visitDay) {
		LocalDate currentDay = LocalDate.now();
		boolean isWeekend = visitDay.getDayOfWeek() == DayOfWeek.SATURDAY
				|| visitDay.getDayOfWeek() == DayOfWeek.SUNDAY;

		return visitDay.isBefore(currentDay) || visitDay.isEqual(currentDay) || isWeekend;
	}

	@GetMapping("/owners/*/pets/{petId}/visits")
	public String showVisits(@PathVariable int petId, Map<String, Object> model) {
		model.put("visits", this.clinicService.findPetById(petId).getVisits());
		return "visitList";
	}

	private Vet getVetFromModelMap(Map<String, Object> model, Integer id) {
		@SuppressWarnings("unchecked")
		List<Vet> allVets = (List<Vet>) model.get("vets");

		return allVets.stream().filter(v -> v.getId().equals(id)).findFirst().get();
	}

}
