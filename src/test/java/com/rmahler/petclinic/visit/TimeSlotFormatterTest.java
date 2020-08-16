package com.rmahler.petclinic.visit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for TimeSlotFormatter
 *
 * @author Richard Mahler
 */
public class TimeSlotFormatterTest {

	@Test
	public void getAvailableSlots_noFilledSlots_returnsAll() {
		ArrayList<TimeSlot> slots = TimeSlotFormatter.getAvailableSlots(Collections.EMPTY_LIST);
		assertThat(slots.size()).isEqualTo(9);
		TimeSlot first = slots.get(0);
		assertThat(first.slotNum).isEqualTo(1);
		TimeSlot last = slots.get(8);
		assertThat(last.slotNum).isEqualTo(9);
	}

	@Test
	public void getAvailableSlots_twoFilledSlots_returnsRemaining() {
		ArrayList<TimeSlot> slots = TimeSlotFormatter.getAvailableSlots(Arrays.asList(1, 5));
		assertThat(slots.size()).isEqualTo(7);
		TimeSlot first = slots.get(0);
		assertThat(first.slotNum).isEqualTo(2);
		TimeSlot last = slots.get(6);
		assertThat(last.slotNum).isEqualTo(9);
	}

	@Test
	public void getTimeSlotDescription_getSlotDescriptions_returnsCorrectDescription() {
		String morningSlot = TimeSlotFormatter.getTimeSlotDescription(4);
		assertThat(morningSlot).isEqualTo("11 AM to 12 AM");
		String noonSlot = TimeSlotFormatter.getTimeSlotDescription(5);
		assertThat(noonSlot).isEqualTo("12 AM to 1 PM");
		String afternoonSlot = TimeSlotFormatter.getTimeSlotDescription(9);
		assertThat(afternoonSlot).isEqualTo("4 PM to 5 PM");
	}

}
