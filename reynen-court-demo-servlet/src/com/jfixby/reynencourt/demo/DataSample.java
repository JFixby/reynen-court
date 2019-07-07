
package com.jfixby.reynencourt.demo;

public class DataSample implements Comparable<DataSample> {

	public long timestamp;

	public String event_id;

	public String event_type;

	public Long value;

	@Override
	public String toString () {
		return "DataSample [timestamp=" + this.timestamp + ", event_id=" + this.event_id + ", event_type=" + this.event_type
			+ ", value=" + this.value + "]";
	}

	@Override
	public int compareTo (final DataSample y) {
		final DataSample x = this;
		return Long.compare(x.timestamp, y.timestamp);
	}

}
