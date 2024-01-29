package org.dusttoash.scheduler.api.internal;

import pala.libs.generic.json.JSONObject;
import pala.libs.generic.json.JSONValue;

public class Event {
	private byte daysOfWeek;
	private String name, desc;
	private DayTime start, end;

	public Event(JSONValue json) {
		if (!(json instanceof JSONObject))
			throw new RuntimeException("Provided JSON is not an Event");
		JSONObject o = (JSONObject) json;
		daysOfWeek = (byte) o.getInt("days-of-week");
		name = o.getString("name");
		if (o.containsKey("desc"))
			desc = o.getString("desc");
		start = new DayTime(o.get("start"));
		end = new DayTime(o.get("end"));
	}

	public Event(String name, String desc, DayTime start, DayTime end, DayOfWeek... days) {
		if (end.compareTo(start) < 0)
			throw new IllegalArgumentException("End time cannot be before start time");
		for (var v : days)
			daysOfWeek |= v.getBitmask();
		this.name = name;
		this.desc = desc;
		this.start = start;
		this.end = end;
	}

	public Event(String name, DayTime start, DayTime end, DayOfWeek... days) {
		this(name, null, start, end, days);
	}

	public byte getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(byte daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public boolean isDayOfWeek(DayOfWeek dow) {
		return (getDaysOfWeek() & dow.getBitmask()) != 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public DayTime getStart() {
		return start;
	}

	public void setStart(DayTime start) {
		this.start = start;
	}

	public DayTime getEnd() {
		return end;
	}

	public void setEnd(DayTime end) {
		this.end = end;
	}

	public JSONObject toJSON() {
		JSONObject res = new JSONObject();
		res.put("days-of-week", daysOfWeek).put("name", name).put("start", start.toJSON());
		res.put("end", end.toJSON());
		if (desc != null)
			res.put("desc", desc);
		return res;
	}

}
