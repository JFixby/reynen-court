
package com.jfixby.reynencourt.demo.api;

import java.io.IOException;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.io.InputStream;
import com.jfixby.scarabei.api.io.OutputStream;

public interface DataSampleStorage {

	public void consumeDataSample (final DataSample sample);

	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp);

	public String aggregateAverage (final long fromTimestamp, final long toTimestamp);

	public String aggregateSum (final long fromTimestamp, final long toTimestamp);

	public String archive (final long fromTimestamp, final long toTimestamp) throws IOException;

	public InputStream readArchive (String archiveId);

	public void writeDataSamplesToStream (Collection<DataSample> lisamplesst, OutputStream os) throws IOException;

}
