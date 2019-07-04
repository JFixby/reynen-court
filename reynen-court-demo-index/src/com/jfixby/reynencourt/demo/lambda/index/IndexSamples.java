
package com.jfixby.reynencourt.demo.lambda.index;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class IndexSamples implements RequestHandler<S3Event, String> {

	public static long REQUEST_NUM = 0;

	private static File dataFolder;

	public static void init () {
	}

	static {
		ScarabeiDesktop.deploy();

// S3.installComponent(new DesktopS3());
// final S3Component s3 = S3.invoke();
//
// SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT);
// final Map<ID, Object> settings = SystemSettings.listAllSettings();
// L.d("System settings", settings);
//
// final S3FileSystemConfig s3config = s3.newFileSystemConfig();
// final String accessKeyID = SystemSettings.getStringParameter(AWSCredentials.AWS_ACCESS_KEY(), null);
// s3config.setAccessKeyID(accessKeyID);
// final String regionName = SystemSettings.getStringParameter(AWSCredentials.AWS_REGION_NAME(), null);
//
// s3config.setRegionName(regionName);
// final String secretKeyID = SystemSettings.getStringParameter(AWSCredentials.AWS_SECRET_KEY(), null);
// s3config.setSecretKeyID(secretKeyID);
// final String s3BucketName = SystemSettings.getStringParameter(s3.BUCKET_NAME(), null);
// s3config.setBucketName(s3BucketName);
// L.d("Connecting to S3 Bucket", s3BucketName + " at " + regionName + " ...");
// L.d("s3config", s3config.toString());
// final S3FileSystem fs = s3.newFileSystem(s3config);
// final File remoteFolder = fs.ROOT();
// FilesList files;
// try {
// files = remoteFolder.listDirectChildren();
// L.d(fs + "", files);
// dataFolder = remoteFolder.child("data");
// dataFolder.makeFolder();
// } catch (final IOException e) {
// Err.reportError(e);
// }
	}

	@Override
	public final String handleRequest (final S3Event input, final Context context) {
		context.getLogger().log("Input[" + REQUEST_NUM + "]: " + input);
		REQUEST_NUM++;
		context.getLogger().log("     : " + Json.serializeToString(input));
		return "Consumed <" + input + "> " + Json.serializeToString(input);
	}

}
