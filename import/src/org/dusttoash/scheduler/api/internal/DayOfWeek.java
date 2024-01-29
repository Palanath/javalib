package org.dusttoash.scheduler.api.internal;

public enum DayOfWeek {
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

	private static final DayOfWeek[] DAYS = values();

	public byte getBitmask() {
		return (byte) (1 << ordinal());
	}

	public DayOfWeek previous(int amount) {
		return DAYS[7 - (amount - ordinal()) % 7];
	}

	public DayOfWeek previous() {
		return previous(1);
	}

	public DayOfWeek next(int amount) {
		return DAYS[(ordinal() + amount) % 7];
	}

	public DayOfWeek next() {
		return next(1);
	}

	public static DayOfWeek get(int dayofweek) {
		return DAYS[dayofweek];
	}
}