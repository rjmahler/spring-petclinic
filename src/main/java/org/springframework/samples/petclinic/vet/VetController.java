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
package org.springframework.samples.petclinic.vet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.util.PersonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Richard Mahler
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

	private static final String VIEWS_VETS_CREATE_OR_UPDATE_FORM = "vets/createOrUpdateVetForm";

	private ClinicService clinicService;

	@Autowired
	public VetController(ClinicService clinicService) {
		this.clinicService = clinicService;
	}

	@GetMapping("/vets.html")
	public String showVetList(Map<String, Object> model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.clinicService.findVets());
		model.put("vets", vets);
		return "vets/vetList";
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(clinicService.findVets());
		return vets;
	}

	@GetMapping("/vets/new")
	public String initCreationForm(ModelMap model) {
		populateSpecialities(null, model);
		Vet vet = new Vet();
		model.put("vet", vet);
		return VIEWS_VETS_CREATE_OR_UPDATE_FORM;
	}

	private void populateSpecialities(Vet vet, Map<String, Object> model) {
		ArrayList<Specialty> spcs = new ArrayList<>();

		Collection<Specialty> selectedSpc = null;
		if (vet != null)
			selectedSpc = vet.getSpecialtiesInternal();

		Collection<Specialty> specialties = clinicService.getVetSpecialties();
		for (Specialty s : specialties) {
			if (selectedSpc != null) {
				for (Specialty spec : selectedSpc) {
					if (s.getId().intValue() == spec.getId().intValue()) {
						s.setSelected(true);
					}
				}
			}
			spcs.add(s);
		}
		model.put("allspecialties", spcs);
	}

	@PostMapping("/vets/new")
	public String processCreationForm(@Valid Vet vet, BindingResult result,
			@RequestParam(value = "selectSpecList", required = false) ArrayList<String> selectSpecList,
			ModelMap model) {

		return processUpdateForm(vet, result, selectSpecList, model, -1);
	}

	@GetMapping("/vets/{vetId}/edit")
	public String initUpdateForm(@PathVariable("vetId") int vetId, ModelMap model) {
		Vet vet = clinicService.findVetById(vetId);

		populateSpecialities(vet, model);
		model.put("vet", vet);
		return VIEWS_VETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/vets/{vetId}/edit")
	public String processUpdateForm(@Valid Vet vet, BindingResult result,
			@RequestParam(value = "selectSpecList", required = false) ArrayList<String> selectSpecList, ModelMap model,
			@PathVariable("vetId") int vetId) {

		vet.setFirstName(vet.getFirstName().trim());
		vet.setLastName(vet.getLastName().trim());

		populateSpecialities(vet, model);

		if (result.hasErrors() || vetFullNameExists(vet, vetId, result)) {
			model.put("vet", vet);
			return VIEWS_VETS_CREATE_OR_UPDATE_FORM;
		}
		else {
			if (selectSpecList != null) {
				for (String s : selectSpecList) {
					String[] st = s.split("_");
					Specialty spc = new Specialty();
					spc.setId(Integer.parseInt(st[0]));
					spc.setName(st[1]);
					vet.addSpecialty(spc);
				}
			}
			clinicService.saveVet(vet);
			return "redirect:/vets.html";
		}
	}

	private boolean vetFullNameExists(Vet vet, int existingId, BindingResult result) {
		Vet originalVet = null;
		if (existingId != -1) {
			originalVet = clinicService.findVetById(existingId);
		}
		Collection<Vet> allVets = clinicService.findVets();

		boolean existsWithSameName = PersonUtil.existsWithFullName(vet, originalVet, allVets);
		if (existsWithSameName) {
			result.rejectValue("lastName", "duplicate_full_name", "already exists");
		}
		return existsWithSameName;
	}

}
