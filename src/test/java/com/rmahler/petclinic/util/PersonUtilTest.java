package com.rmahler.petclinic.util;

import com.rmahler.petclinic.owner.Owner;
import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for PersonUtil
 *
 * @author Richard Mahler
 */
public class PersonUtilTest {

	Collection<Owner> existingCollection;

	private Owner owner1;

	private Owner owner2;

	@Before
	public void setUp() {
		existingCollection = new ArrayList<>();
		owner1 = new Owner();
		owner1.setFirstName("Max");
		owner1.setLastName("Mahler");

		owner2 = new Owner();
		owner2.setFirstName("Lysundra");
		owner2.setLastName("Lee");

		existingCollection.add(owner1);
		existingCollection.add(owner2);
	}

	@Test
	public void existsWithFullName_notADuplicate_returnsFalse() {
		Owner owner = new Owner();
		owner.setFirstName("Richard");
		owner.setLastName("Mahler");
		boolean result = PersonUtil.existsWithFullName(owner, owner1, existingCollection);
		assertThat(result).isFalse();
	}

	@Test
	public void existsWithFullName_duplicate_returnsTrue() {
		Owner owner = new Owner();
		owner.setFirstName("Lysundra");
		owner.setLastName("Lee");
		boolean result = PersonUtil.existsWithFullName(owner, owner1, existingCollection);
		assertThat(result).isTrue();
	}

}
