
package com.jfixby.reynencourt.sns.demo;

import java.io.IOException;

import com.jfixby.reynencourt.sns.credentials.AWSCredentials;
import com.jfixby.reynencourt.sns.demo.storage.NotificationsStorage;
import com.jfixby.reynencourt.sns.demo.storage.NotificationsStorageConfig;
import com.jfixby.reynencourt.sns.demo.storage.NotificationsStorageSpecs;
import com.jfixby.scarabei.amazon.aws.sns.RedSNS;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class Run {

	public static void main (final String[] args) throws IOException {

		ScarabeiDesktop.deploy();
		SNS.installComponent(new RedSNS());

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config")
			.child("credentials").child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		L.d("awsKeys", awsKeys);

		final File separatorConfigFile = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config")
			.child("configs").child("storage-config.json");
		final JsonString configJson = Json.newJsonString(separatorConfigFile.readToString());
		final NotificationsStorageConfig storageConfig = Json.deserializeFromString(NotificationsStorageConfig.class, configJson);

		final NotificationsStorageSpecs specs = new NotificationsStorageSpecs();
		specs.inputQueueURL = (storageConfig.inputQueueURL);

		specs.aWSCredentialsProvider = (awsKeys);
		specs.debugMode = (storageConfig.debugMode);
		specs.sQSMailboxPrefix = (storageConfig.sqsMailboxPrefix);
		specs.separatorStartProcessingDelay = (storageConfig.separatorStartProcessingDelay);
		specs.aWSCredentialsProvider = awsKeys;
		specs.SNSEndpoint = "";

		final NotificationsStorage storage = NotificationsStorage.newNotificationsStorage(specs);
		storage.consumeDataSample(null);

	}

}
