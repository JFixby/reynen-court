
package com.jfixby.reynencourt.demo.storage.api;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.io.InputStream;
import com.jfixby.scarabei.api.io.OutputStream;

public interface DataSampleStorage {

	InputStream readArchive (String archiveId);

	String aggregateSum (long fromTimestamp, long toTimestamp);

	String archive (long fromTimestamp, long toTimestamp);

	String aggregateAverage (long fromTimestamp, long toTimestamp);

	Collection<DataSample> queryFromToTimestamp (long fromTimestamp, long toTimestamp);

	void writeDataSamplesToStream (Collection<DataSample> result, OutputStream os);

	void consumeDataSample (DataSample dataSample);

}
