package org.dusttoash.scheduler.api.internal;

import pala.libs.generic.json.JSONObject;
import pala.libs.generic.json.JSONValue;

public class Task implements Comparable<Task> {
	private String name, desc;
	// mins = mins the task takes to complete
	private int mins, dueDay;// dueDay = days past epoch.
	private DayTime dueTime;
	private boolean finished;

	public boolean isFinished() {
		return finished;
	}

	public DayTime getDueTime() {
		return dueTime;
	}

	public void setDueTime(DayTime dueTime) {
		this.dueTime = dueTime;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Task(String name, String desc, int mins, int dueDay, DayTime dueTime) {
		this.name = name;
		this.desc = desc;
		this.mins = mins;
		this.dueDay = dueDay;
		this.dueTime = dueTime;
	}

	public Task(String name, int mins, int dueDay, DayTime dueTime) {
		this(name, null, mins, dueDay, dueTime);
	}

	public Task(JSONValue json) {
		if (!(json instanceof JSONObject))
			throw new RuntimeException("Provided JSON is not a Task");
		JSONObject o = (JSONObject) json;
		name = o.getString("name");
		if (o.containsKey("desc"))
			desc = o.getString("desc");
		mins = o.getInt("mins");
		dueDay = o.getInt("due-day");
		finished = o.getBoolean("finished");
		dueTime = new DayTime(o.get("due-time"));
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

	public int getMins() {
		return mins;
	}

	public void setMins(int mins) {
		this.mins = mins;
	}

	public int getDueDay() {
		return dueDay;
	}

	public void setDueDay(int dueDay) {
		this.dueDay = dueDay;
	}

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject().put("name", name).put("mins", mins).put("due-day", dueDay).put("finished",
				finished);
		obj.put("due-time", dueTime.toJSON());
		if (desc != null)
			obj.put("desc", desc);
		return obj;
	}

	@Override
	public int compareTo(Task o) {
		return dueDay == o.dueDay ? dueTime.compareTo(o.dueTime) : dueDay - o.dueDay;
	}

}
