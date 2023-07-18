package pala.libs.generic.util;

/**
 * Facilitates a poll-based application that executes periodic actions.
 * 
 * @author Palanath
 *
 */
public class PollTimer {
	private long millisPeriod, start;

	public void start() {
		start = System.currentTimeMillis();
	}

	/**
	 * Polls the timer to determine if the {@link #millisPeriod timer period} has
	 * passed. If it has, restarts the timer and returns <code>true</code>. The
	 * method then won't return <code>true</code> again until another
	 * {@link #millisPeriod} has passed.
	 * 
	 * @return Whether the {@link #millisPeriod} has passed since {@link #start} or
	 *         since the last call to {@link #poll()} that returned
	 *         <code>true</code>.
	 */
	public boolean poll() {
		if (System.currentTimeMillis() > millisPeriod + start) {
			millisPeriod = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	public long getMillisPeriod() {
		return millisPeriod;
	}

	public void setMillisPeriod(long millisPeriod) {
		this.millisPeriod = millisPeriod;
	}

	public long getStart() {
		return start;
	}

}
