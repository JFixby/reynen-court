
package com.jfixby.reynencourt.demo;

public class DataSample {

	public Long timestamp;

	public String event_id;

	public String event_type;

	public String value;

	@Override
	public String toString () {
		return "DataSample [timestamp=" + this.timestamp + ", event_id=" + this.event_id + ", event_type=" + this.event_type
			+ ", value=" + this.value + "]";
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.event_id == null) ? 0 : this.event_id.hashCode());
		result = prime * result + ((this.event_type == null) ? 0 : this.event_type.hashCode());
		result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	public boolean equals (final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final DataSample other = (DataSample)obj;
		if (this.event_id == null) {
			if (other.event_id != null) {
				return false;
			}
		} else if (!this.event_id.equals(other.event_id)) {
			return false;
		}
		if (this.event_type == null) {
			if (other.event_type != null) {
				return false;
			}
		} else if (!this.event_type.equals(other.event_type)) {
			return false;
		}
		if (this.timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!this.timestamp.equals(other.timestamp)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
