
package com.jfixby.reynencourt.demo.lambda.consume;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.jfixby.reynencourt.demo.lambda.DataSample;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.names.ID;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.aws.api.s3.S3;
import com.jfixby.scarabei.aws.api.s3.S3Component;
import com.jfixby.scarabei.aws.api.s3.S3FileSystem;
import com.jfixby.scarabei.aws.api.s3.S3FileSystemConfig;
import com.jfixby.scarabei.aws.desktop.s3.DesktopS3;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class ConsumeDataSample implements RequestHandler<DataSample, String> {

	private static File dataFolder;

	public static void init () {
	}

	static {
		ScarabeiDesktop.deploy();

		S3.installComponent(new DesktopS3());
		final S3Component s3 = S3.invoke();

		SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT);
		final Map<ID, Object> settings = SystemSettings.listAllSettings();
		L.d("System settings", settings);

		final S3FileSystemConfig s3config = s3.newFileSystemConfig();
		final String accessKeyID = SystemSettings.getStringParameter(s3.AWS_ACCESS_KEY(), "");
		s3config.setAccessKeyID(accessKeyID);
		final String regionName = SystemSettings.getStringParameter(s3.AWS_REGION_NAME(), "");

		s3config.setRegionName(regionName);
		final String secretKeyID = SystemSettings.getStringParameter(s3.AWS_SECRET_KEY(), "");
		s3config.setSecretKeyID(secretKeyID);
		final String s3BucketName = SystemSettings.getStringParameter(s3.BUCKET_NAME(), "");
		s3config.setBucketName(s3BucketName);
		L.d("Connecting to S3 Bucket", s3BucketName + " at " + regionName + " ...");
		L.d("s3config", s3config.toString());
		final S3FileSystem fs = s3.newFileSystem(s3config);
		final File remoteFolder = fs.ROOT();
		FilesList files;
		try {
			files = remoteFolder.listDirectChildren();
			L.d(fs + "", files);
			dataFolder = remoteFolder.child("data");
			dataFolder.makeFolder();
		} catch (final IOException e) {
			Err.reportError(e);

		}
	}

	@Override
	public String handleRequest (final DataSample input, final Context context) {
		context.getLogger().log("Input: " + input);
		if (DataSample.isInvalid(input)) {
			return "Invalid DataSample <" + input + ">";
		}
		final String fileName = this.fileNameFor(input);
		final File sampleFile = dataFolder.child(fileName);
		final String jsonString = Json.serializeToString(input).toString();
		try {
			sampleFile.writeString(jsonString);
		} catch (final IOException e) {
			Err.reportError(e);
			Sys.exit();
		}
		return "Consumed <" + input + ">";
	}

	private String fileNameFor (final DataSample input) {
		return input.event_id + ".json";
	}

}
