package pala.libs.generic.events;

import java.time.Instant;

public class Event {
	private final Instant timestamp;
	private boolean consumed;

	public Event() {
		this(Instant.now());
	}

	public Event(final Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void consume() {
		setConsumed(true);
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setConsumed(final boolean consumed) {
		this.consumed = consumed;
	}

}
