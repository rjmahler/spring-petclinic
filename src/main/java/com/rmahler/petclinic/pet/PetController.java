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

import com.rmahler.petclinic.owner.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import com.rmahler.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

/**
 * @author Richard Mahler
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private final ClinicService clinicService;

	@Autowired
	public PetController(ClinicService clinicService) {
		this.clinicService = clinicService;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return clinicService.findPetTypes();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		return clinicService.findOwnerById(ownerId);
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		Pet pet = new Pet();
		owner.addPet(pet);
		model.put("pet", pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result, ModelMap model) {
		return processUpdateForm(pet, result, owner, model, -1);
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm(@PathVariable("petId") int petId, ModelMap model) {
		Pet pet = clinicService.findPetById(petId);
		model.put("pet", pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(@Valid Pet pet, BindingResult result, Owner owner, ModelMap model,
			@PathVariable("petId") int petId) {

		pet.setName(pet.getName().trim());

		if (result.hasErrors() || petNameExists(owner, pet, petId, result)) {
			pet.setOwner(owner);
			model.put("pet", pet);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		else {
			owner.addPet(pet);
			clinicService.savePet(pet);
			return "redirect:/owners/{ownerId}";
		}
	}

	private boolean petNameExists(Owner owner, Pet pet, int existingId, BindingResult result) {
		Pet originalPet = null;
		if (existingId != -1) {
			originalPet = clinicService.findPetById(existingId);
		}
		Collection<Pet> allPets = owner.getPets();
		if (originalPet != null) {
			allPets.remove(originalPet);
		}
		boolean existsWithSameName = allPets.stream()
				.anyMatch(existingPet -> existingPet.getName().equals(pet.getName()));
		if (existsWithSameName) {
			result.rejectValue("name", "duplicate", "already exists");
		}
		return existsWithSameName;
	}

}
