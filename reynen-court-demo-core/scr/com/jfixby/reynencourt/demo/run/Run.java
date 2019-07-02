
package com.jfixby.reynencourt.demo.run;

import java.io.IOException;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.Deployment;
import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;

public class Run {

	public static void main (final String[] args) throws IOException {

		final DataSampleStorage storage = Deployment.deploy();
		final DataSample sample = new DataSample();
		sample.timestamp = Sys.SystemTime().currentTimeMillis();
		storage.consumeDataSample(sample);

		final Collection<DataSample> list = storage.queryFromToTimestamp(0L, Long.MAX_VALUE);
		L.d("list", list);
	}

}
