
package com.jfixby.reynencourt.demo.deploy.run;

import java.io.IOException;

import com.jfixby.reynencourt.demo.deploy.DeploymentConfig;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.AWSCredentials;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.s3.S3;
import com.jfixby.scarabei.aws.api.s3.S3Component;
import com.jfixby.scarabei.aws.api.s3.S3FileSystem;
import com.jfixby.scarabei.aws.api.s3.S3FileSystemConfig;
import com.jfixby.scarabei.aws.desktop.s3.DesktopS3;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class TestS3BucketAccess {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		S3.installComponent(new DesktopS3());
		final S3Component s3 = S3.invoke();

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().child("credentials").child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

// L.d("awsKeys", awsKeys);

		final File separatorConfigFile = LocalFileSystem.ApplicationHome().child("configs").child("deploy-config.json");
		final JsonString configJson = Json.newJsonString(separatorConfigFile.readToString());
		final DeploymentConfig storageConfig = Json.deserializeFromString(DeploymentConfig.class, configJson);

		final S3FileSystemConfig s3config = s3.newFileSystemConfig();
		s3config.setAccessKeyID(awsKeys.getAccessKeyID());
		s3config.setRegionName(awsKeys.getRegionName());
		s3config.setSecretKeyID(awsKeys.getSecretKeyID());
		s3config.setBucketName(storageConfig.s3BucketName);

		final S3FileSystem fs = s3.newFileSystem(s3config);
		final File remoteFolder = fs.ROOT();

		L.d(fs + "", remoteFolder.listDirectChildren());
	}

}
