
package com.jfixby.reynencourt.demo;

import java.io.IOException;

import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.reynencourt.demo.credentials.AWSCredentials;
import com.jfixby.reynencourt.demo.storage.v001.NotificationsStorageSpecs;
import com.jfixby.reynencourt.demo.storage.v001.NotificationsStorage_V_0_0_1;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.s3.S3;
import com.jfixby.scarabei.aws.api.s3.S3Component;
import com.jfixby.scarabei.aws.api.s3.S3FileSystem;
import com.jfixby.scarabei.aws.api.s3.S3FileSystemConfig;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.desktop.sns.DesktopSNS;
import com.jfixby.scarabei.aws.desktop.sqs.DesktopSQS;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class Deployment {

	public static DataSampleStorage deploy () throws IOException {
		ScarabeiDesktop.deploy();
		SNS.installComponent(new DesktopSNS());
		SQS.installComponent(new DesktopSQS());
		final S3Component s3 = S3.invoke();

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config")
			.child("credentials").child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		L.d("awsKeys", awsKeys);

		final File separatorConfigFile = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config")
			.child("configs").child("storage-config.json");
		final JsonString configJson = Json.newJsonString(separatorConfigFile.readToString());
		final ApplicationConfig storageConfig = Json.deserializeFromString(ApplicationConfig.class, configJson);

		if (storageConfig.debugMode) {
			SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT); // Offensive programming mode
		} else {
			SystemSettings.setExecutionMode(ExecutionMode.PUBLIC_RELEASE); // Defensive programming mode
		}

		final S3FileSystemConfig s3config = s3.newFileSystemConfig();
		s3config.setAccessKeyID(awsKeys.getAccessKeyID());
		s3config.setRegionName(awsKeys.getRegionName());
		s3config.setSecretKeyID(awsKeys.getSecretKeyID());
		s3config.setBucketName(storageConfig.s3BucketName);

		final S3FileSystem fs = s3.newFileSystem(s3config);
		final File remoteFolder = fs.ROOT();

		final NotificationsStorageSpecs specs = new NotificationsStorageSpecs();
		specs.inputQueueURL = (storageConfig.inputQueueURL);

		specs.aWSCredentialsProvider = awsKeys;
		specs.debugMode = storageConfig.debugMode;
		specs.sQSMailboxPrefix = storageConfig.sqsMailboxPrefix;
		specs.separatorStartProcessingDelay = storageConfig.separatorStartProcessingDelay;
		specs.snsTopicARN = storageConfig.snsTopicARN;
		specs.storageFolder = remoteFolder;

		final NotificationsStorage_V_0_0_1 storage = NotificationsStorage_V_0_0_1.newNotificationsStorage(specs);

		return storage;
	}

}
