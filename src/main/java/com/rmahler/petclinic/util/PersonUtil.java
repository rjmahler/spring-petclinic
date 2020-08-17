package com.rmahler.petclinic.util;

import com.rmahler.petclinic.model.Person;

import java.util.Collection;

/**
 * Util class for Person and inherited objects - namely Vet and Owner
 *
 * @author Richard Mahler
 */
public class PersonUtil {

	private PersonUtil() {
	}

	/**
	 * Duplicate name check for update operations to make sure we don't collide with other
	 * Persons
	 * @param updatingPerson - Person whose name we are to check
	 * @param existingPerson - Original person object
	 * @param persons - all the Person objects in the store
	 * @return true, if this is a duplicate among the remaining objects
	 */
	public static boolean existsWithFullName(Person updatingPerson, Person existingPerson,
			Collection<? extends Person> persons) {
		if (existingPerson != null) {
			persons.removeIf(person -> (person.getFirstName().equalsIgnoreCase(existingPerson.getFirstName())
					&& person.getLastName().equalsIgnoreCase(existingPerson.getLastName())));
		}

		return persons.stream()
				.anyMatch(person -> updatingPerson.getFirstName() != null
						&& updatingPerson.getFirstName().equalsIgnoreCase(person.getFirstName())
						&& updatingPerson.getLastName() != null
						&& updatingPerson.getLastName().equalsIgnoreCase(person.getLastName()));
	}

}
