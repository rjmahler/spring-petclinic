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
package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.util.PersonUtil;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Richard Mahler
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final ClinicService clinicService;

	@Autowired
	public OwnerController(ClinicService clinicService) {
		this.clinicService = clinicService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping("/owners/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		return processUpdateOwnerForm(owner, result, -1);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.clinicService.findOwnerById(ownerId);
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId) {

		owner.setFirstName(owner.getFirstName().trim());
		owner.setLastName(owner.getLastName().trim());
		owner.setId(ownerId);

		if (result.hasErrors() || ownerFullNameExists(owner, ownerId, result)) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			this.clinicService.saveOwner(owner);
			if (ownerId == -1) {
				return "redirect:/owners/" + owner.getId();
			}
			return "redirect:/owners/{ownerId}";
		}
	}

	private boolean ownerFullNameExists(Owner owner, int existingId, BindingResult result) {
		Owner originalOwner = null;
		if (existingId != -1) {
			originalOwner = clinicService.findOwnerById(existingId);
		}
		Collection<Owner> ownersByLastName = clinicService.findOwnerByLastName(owner.getLastName());

		boolean existsWithSameName = PersonUtil.existsWithFullName(owner, originalOwner, ownersByLastName);

		if (existsWithSameName) {
			result.rejectValue("lastName", "duplicate_full_name", "already exists");
		}
		return existsWithSameName;
	}

	@GetMapping("/owners/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("owner", new Owner());
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // empty string signifies broadest possible search
		}

		// find owners by last name
		Collection<Owner> results = this.clinicService.findOwnerByLastName(owner.getLastName());
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}
		else if (results.size() == 1) {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		else {
			// multiple owners found
			model.put("selections", results);
			return "owners/ownersList";
		}
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId, Map<String, Object> model) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Owner owner = this.clinicService.findOwnerById(ownerId);

		for (Pet pet : owner.getPets()) {
			pet.setVisitsInternal(this.clinicService.findVisitsByPetId(pet.getId()));
		}
		model.put("pets", owner.getPets());
		mav.addObject(owner);
		return mav;
	}

	@PostMapping("/owners/{ownerId}/visit/cancel/{visitId}")
	public String cancelPetVisit(@PathVariable("ownerId") int ownerId, @PathVariable("visitId") int visitId) {
		this.clinicService.cancelVisit(visitId);
		return "redirect:/owners/{ownerId}";
	}

}
