
package com.jfixby.reynencourt.sns.demo.storage.api;

import com.jfixby.reynencourt.sns.demo.storage.DataSample;
import com.jfixby.scarabei.api.collections.Collection;

public interface DataSampleStorage {

	public void consumeDataSample (final DataSample sample);

	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp);

	public String aggregateAverage (final long fromTimestamp, final long toTimestamp);

	public String aggregateSum (final long fromTimestamp, final long toTimestamp);

}
