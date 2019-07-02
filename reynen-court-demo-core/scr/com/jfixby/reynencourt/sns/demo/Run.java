
package com.jfixby.reynencourt.sns.demo;

import java.io.IOException;

import com.jfixby.reynencourt.sns.credentials.AWSCredentials;
import com.jfixby.reynencourt.sns.demo.storage.DataSample;
import com.jfixby.reynencourt.sns.demo.storage.NotificationsStorage;
import com.jfixby.reynencourt.sns.demo.storage.NotificationsStorageConfig;
import com.jfixby.reynencourt.sns.demo.storage.NotificationsStorageSpecs;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.desktop.sns.DesktopSNS;
import com.jfixby.scarabei.aws.desktop.sqs.DesktopSQS;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class Run {

	public static void main (final String[] args) throws IOException {

		ScarabeiDesktop.deploy();
		SNS.installComponent(new DesktopSNS());
		SQS.installComponent(new DesktopSQS());

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config")
			.child("credentials").child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		L.d("awsKeys", awsKeys);

		final File separatorConfigFile = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config")
			.child("configs").child("storage-config.json");
		final JsonString configJson = Json.newJsonString(separatorConfigFile.readToString());
		final NotificationsStorageConfig storageConfig = Json.deserializeFromString(NotificationsStorageConfig.class, configJson);

		if (storageConfig.debugMode) {
			SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT); // Offensive programming mode
		} else {
			SystemSettings.setExecutionMode(ExecutionMode.PUBLIC_RELEASE); // Defensive programming mode
		}

		final NotificationsStorageSpecs specs = new NotificationsStorageSpecs();
		specs.inputQueueURL = (storageConfig.inputQueueURL);

		specs.aWSCredentialsProvider = awsKeys;
		specs.debugMode = storageConfig.debugMode;
		specs.sQSMailboxPrefix = storageConfig.sqsMailboxPrefix;
		specs.separatorStartProcessingDelay = storageConfig.separatorStartProcessingDelay;
		specs.snsTopicARN = storageConfig.snsTopicARN;

		final NotificationsStorage storage = NotificationsStorage.newNotificationsStorage(specs);
		final DataSample sample = new DataSample();
		sample.timestamp = Sys.SystemTime().currentTimeMillis();
		storage.consumeDataSample(sample);

		final Collection<DataSample> list = storage.queryFromToTimestamp(0L, Long.MAX_VALUE);
		L.d("list", list);
	}

}
