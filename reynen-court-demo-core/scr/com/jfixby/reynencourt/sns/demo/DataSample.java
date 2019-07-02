
package com.jfixby.reynencourt.sns.demo;

public class DataSample {

	public long timestamp;

	public String event_id;

	public String event_type;

	public String value;

	@Override
	public String toString () {
		return "DataSample [timestamp=" + this.timestamp + ", event_id=" + this.event_id + ", event_type=" + this.event_type
			+ ", value=" + this.value + "]";
	}

}
