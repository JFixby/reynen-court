
package com.jfixby.reynencourt.demo.storage;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.file.File;

public interface StorageIndex {

	void registerSampleFile (File sampleFile);

	void reset ();

	Collection<DataSample> queryFromToTimestamp (long fromTimestamp, long toTimestamp);

	Long aggregateSum (long fromTimestamp, long toTimestamp);

	Long aggregateAverage (long fromTimestamp, long toTimestamp);

}
