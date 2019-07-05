
package com.jfixby.reynencourt.demo.lambda.unarchive;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.io.InputStream;
import com.jfixby.scarabei.api.io.OutputStream;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.names.ID;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.aws.api.AWSCredentials;
import com.jfixby.scarabei.aws.api.s3.S3;
import com.jfixby.scarabei.aws.api.s3.S3Component;
import com.jfixby.scarabei.aws.api.s3.S3FileSystem;
import com.jfixby.scarabei.aws.api.s3.S3FileSystemConfig;
import com.jfixby.scarabei.aws.desktop.s3.DesktopS3;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class UnArchive implements RequestStreamHandler {

	private static File archivesFolder;

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
		final String accessKeyID = SystemSettings.getStringParameter(AWSCredentials.AWS_ACCESS_KEY(), null);
		s3config.setAccessKeyID(accessKeyID);
		final String regionName = SystemSettings.getStringParameter(AWSCredentials.AWS_REGION_NAME(), null);

		s3config.setRegionName(regionName);
		final String secretKeyID = SystemSettings.getStringParameter(AWSCredentials.AWS_SECRET_KEY(), null);
		s3config.setSecretKeyID(secretKeyID);
		final String s3BucketName = SystemSettings.getStringParameter(s3.BUCKET_NAME(), null);
		s3config.setBucketName(s3BucketName);
		L.d("Connecting to S3 Bucket", s3BucketName + " at " + regionName + " ...");
		L.d("s3config", s3config.toString());
		final S3FileSystem fs = s3.newFileSystem(s3config);
		final File remoteFolder = fs.ROOT();
		FilesList files;
		try {
			files = remoteFolder.listDirectChildren();
			L.d(fs + "", files);
			archivesFolder = remoteFolder.child("archives");
			archivesFolder.makeFolder();
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}
	}

	@Override
	public void handleRequest (final java.io.InputStream javaInput, final java.io.OutputStream javaOutput, final Context context) {
		try {
			final InputStream is = IO.newInputStream( () -> javaInput);
			final OutputStream os = IO.newOutputStream( () -> javaOutput);
// <APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>

			is.open();
			final String inputString = is.readAllToString();
			is.close();

			context.getLogger().log("Input: RQ>" + inputString + "<");

			final UnArchiveRequest input = Json.deserializeFromString(UnArchiveRequest.class, inputString);

			final String archiveFileName = input.archive_id;
			final File archive = archivesFolder.child(archiveFileName);
			if (!archive.exists()) {
				final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
				response.setStatusCode(404);
				response.setBody(inputString);
				final byte[] bytes = Json.serializeToString(response).toString().getBytes();
				{
					os.open();
					os.write(bytes);
					os.close();
				}
				return;
			}

			final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
			response.setStatusCode(200);
			final String base64 = new String(Base64.encodeBase64(archive.readBytes().toArray()));
			response.setBody(base64);
			response.setIsBase64Encoded(true);

			final byte[] bytes = Json.serializeToString(response).toString().getBytes();
			{
				os.open();
				os.write(bytes);
				os.close();
			}

		} catch (final IOException e) {
			e.printStackTrace();
// Err.reportError(e);
		}
	}

}
