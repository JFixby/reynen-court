
package com.jfixby.reynencourt.demo.storage.v001;

import java.io.IOException;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.reynencourt.demo.storage.StorageIndex;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FileOutputStream;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.io.GZipOutputStream;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.io.InputStream;
import com.jfixby.scarabei.api.io.OutputStream;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.md5.MD5;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsStorage_V_0_0_1 implements DataSampleStorage {

	public static NotificationsStorage_V_0_0_1 newNotificationsStorage (final NotificationsStorageSpecs specs) {
		final NotificationsStorage_V_0_0_1 S = new NotificationsStorage_V_0_0_1(specs);
		S.reloadIndex();
		return S;
	}

	private final String inputQueueURL;
	private final AWSCredentialsProvider awsKeys;
	private final String topicArn;
	private final File storageFolder;
	private final File samplesStorage;
	private final File archivesStorage;

	private StorageIndex index;

	NotificationsStorage_V_0_0_1 (final NotificationsStorageSpecs specs) {
		this.inputQueueURL = Debug.checkNull("queueURL", specs.inputQueueURL);
		Debug.checkEmpty("queueURL", this.inputQueueURL);
		this.awsKeys = specs.aWSCredentialsProvider;
		Debug.checkNull("awsKeys", this.awsKeys);
		this.topicArn = specs.snsTopicARN;
		Debug.checkNull("topicArn", this.topicArn);

		this.storageFolder = specs.storageFolder;
		Debug.checkNull("storageFolder", this.storageFolder);

		this.samplesStorage = this.storageFolder.child("data-samples");
		this.archivesStorage = this.storageFolder.child("archives");

		this.index = specs.storageIndex;
		if (this.index == null) {
			this.index = new SimpleStorageIndex();
		}

	}

	public void reloadIndex () {
		this.index.reset();
		FilesList files;
		try {
			files = this.samplesStorage.listAllChildren();
			for (final File sampleFile : files) {
				this.index.registerSampleFile(sampleFile);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}
	}

	// Consume data of structure
	// {
	// 'timestamp':<timestamp>,
	// 'event_id':event_id,
	// 'event_type':event_type,
	// 'value':value
	// }
	@Override
	public void consumeDataSample (final DataSample sample) {
		final String sampleFileName = this.sampleFileNameBySample(sample);
		final File sampleFile = this.samplesStorage.child(sampleFileName);
		final JsonString json = Json.serializeToString(sample);
		try {
			sampleFile.writeString(json.toString());
			this.index.registerSampleFile(sampleFile);
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}
	}

	private String sampleFileNameBySample (final DataSample sample) {
		return sample.timestamp + "-" + sample.event_id;
	}

	@Override
	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp) {
		return this.index.queryFromToTimestamp(fromTimestamp, toTimestamp);
	}

	@Override
	public String aggregateAverage (final long fromTimestamp, final long toTimestamp) {
		return this.index.aggregateAverage(fromTimestamp, toTimestamp);
	}

	@Override
	public String aggregateSum (final long fromTimestamp, final long toTimestamp) {
		return this.index.aggregateSum(fromTimestamp, toTimestamp);
	}

	@Override
	public String archive (final long fromTimestamp, final long toTimestamp) throws IOException {
		final Collection<DataSample> result = this.queryFromToTimestamp(fromTimestamp, toTimestamp);
		final String archiveId = "archive-" + fromTimestamp + "-" + toTimestamp;
		final File archiveFile = this.archivesStorage.child(this.fileNameById(archiveId));
		final FileOutputStream os = archiveFile.newOutputStream();
		final GZipOutputStream zip = IO.newGZipStream(os);
		os.open();
		zip.open();
		this.writeDataSamplesToStream(result, zip);
		zip.flush();
		zip.close();
		os.close();
		return archiveId;
	}

	@Override
	public InputStream readArchive (final String archiveId) {
		final File archiveFile = this.archivesStorage.child(this.fileNameById(archiveId));
		return archiveFile.newInputStream();
	}

	private String fileNameById (final String archiveId) {
		return Sys.SystemTime().currentTimeMillis() + "" + MD5.md5String(archiveId);
	}

	@Override
	public void writeDataSamplesToStream (final Collection<DataSample> result, final OutputStream os) throws IOException {
		for (final DataSample s : result) {
			final JsonString string = Json.serializeToString(s);
			final byte[] bytes = string.toString().getBytes();
			os.write(bytes);
		}
		os.flush();
	}

}
