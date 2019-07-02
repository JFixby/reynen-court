
package com.jfixby.reynencourt.demo.storage.v001;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.storage.StorageIndex;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.file.File;

public class SimpleStorageIndex implements StorageIndex {

	@Override
	public void registerSampleFile (final File sampleFile) {
	}

	@Override
	public void reset () {
	}

	@Override
	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp) {
		return null;
	}

	@Override
	public String aggregateSum (final long fromTimestamp, final long toTimestamp) {
		return null;
	}

	@Override
	public String aggregateAverage (final long fromTimestamp, final long toTimestamp) {
		return null;
	}

}
