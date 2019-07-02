
package com.jfixby.reynencourt.demo.api;

import com.jfixby.reynencourt.sns.demo.DataSample;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.io.InputStream;

public interface DataSampleStorage {

	public void consumeDataSample (final DataSample sample);

	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp);

	public String aggregateAverage (final long fromTimestamp, final long toTimestamp);

	public String aggregateSum (final long fromTimestamp, final long toTimestamp);

	public String archive (final long fromTimestamp, final long toTimestamp);

	public InputStream readArchive (String archiveId);

}
