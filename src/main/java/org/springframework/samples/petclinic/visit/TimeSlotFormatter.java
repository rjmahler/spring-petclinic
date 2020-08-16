package org.springframework.samples.petclinic.visit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class for formatting available time slots based on filled slots which is display in the UI
 *
 * @author Richard Mahler
 */
public class TimeSlotFormatter {

	private TimeSlotFormatter() {

	}

	public static ArrayList<TimeSlot> getAvailableSlots(Collection<Integer> filledSlots) {
		ArrayList<TimeSlot> slots = new ArrayList<>();

		for (Integer i = 1; i < 10; i++) {
			if (!filledSlots.contains(i)) {
				TimeSlot timeSlot = new TimeSlot();
				timeSlot.description = getTimeSlotDescription(i);
				timeSlot.slotNum = i;
				slots.add(timeSlot);
			}
		}

		return slots;
	}

	public static String getTimeSlotDescription(int ts) {
		if (ts <= 4) {
			return String.format("%d AM to %d AM", (7 + ts), (8 + ts));
		}
		else if (ts == 5) {
			return String.format("12 AM to 1 PM");
		}
		else {
			return String.format("%d PM to %d PM", (ts - 5), (ts - 4));
		}
	}

}
