
package com.jfixby.reynencourt.demo.run;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.lambda.ConsumeDataSample;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;

public class RunStub {

	public static void main (final String[] args) {
		ConsumeDataSample.init();
		final DataSample ds = new DataSample();
		ds.event_id = "test_event";
		ds.event_type = "test_event_type";
		ds.timestamp = Sys.SystemTime().currentTimeMillis();
		ds.value = "test_event_value";
		L.d(ds.toJsonString());
	}

}
