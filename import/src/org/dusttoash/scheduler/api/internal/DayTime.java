package org.dusttoash.scheduler.api.internal;

import pala.libs.generic.json.JSONNumber;
import pala.libs.generic.json.JSONValue;

public class DayTime implements Comparable<DayTime> {
	private int minsFromDayStart;

	public DayTime(int minsFromDayStart) {
		this.minsFromDayStart = minsFromDayStart;
	}

	public DayTime(int hour, int min) {
		this(hour * 60 + min);
	}

	public DayTime(int hour, int min, boolean pm) {
		this((pm ? 12 : 0) + hour, min);
	}

	public DayTime(JSONValue v) {
		if (!(v instanceof JSONNumber))
			throw new RuntimeException("JSON is not a valid DayTime");
		minsFromDayStart = ((JSONNumber) v).intValue();
	}

	public int get24Hour() {
		return minsFromDayStart / 60;
	}

	public int getHour() {
		return get24Hour() % 12;
	}

	public int getMin() {
		return minsFromDayStart % 60;
	}

	public boolean isAM() {
		return minsFromDayStart < 720;
	}

	@Override
	public String toString() {
		return (getHour() == 0 ? "12" : getHour()) + ":" + getMin() + ' ' + (isAM() ? 'A' : 'P') + 'M';
	}

	public JSONValue toJSON() {
		return new JSONNumber(minsFromDayStart);
	}

	@Override
	public int compareTo(DayTime o) {
		return minsFromDayStart - o.minsFromDayStart;
	}

}
