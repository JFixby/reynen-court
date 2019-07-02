
package com.jfixby.reynencourt.sns.demo.storage;

import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.aws.api.sns.SNSClient;
import com.jfixby.scarabei.aws.api.sns.SNSClientSpecs;
import com.jfixby.scarabei.aws.api.sns.SNSComponent;
import com.jfixby.scarabei.aws.api.sns.SNSPublishRequest;
import com.jfixby.scarabei.aws.api.sns.SNSPublishRequestSpecs;
import com.jfixby.scarabei.aws.api.sns.SNSPublishResult;

public class NotificationsStorage {

	public static NotificationsStorage newNotificationsStorage (final NotificationsStorageSpecs specs) {
		final NotificationsStorage S = new NotificationsStorage(specs);

		return S;
	}

	private final String inputQueueURL;
	private final AWSCredentialsProvider awsKeys;
	private final String topicArn;

	NotificationsStorage (final NotificationsStorageSpecs specs) {
		this.inputQueueURL = Debug.checkNull("queueURL", specs.inputQueueURL);
		Debug.checkEmpty("queueURL", this.inputQueueURL);

		this.awsKeys = specs.aWSCredentialsProvider;
		Debug.checkNull("awsKeys", this.awsKeys);

		this.topicArn = specs.snsTopicARN;
		Debug.checkNull("topicArn", this.topicArn);

	}

	public void consumeDataSample (final DataSample sample) {
		try {
			final SNSComponent sns = SNS.component();

			final SNSClientSpecs clSpecs = sns.newClientSpecs();
			clSpecs.setAWSCredentialsProvider(this.awsKeys);

			final SNSClient snsClient = sns.newClient(clSpecs);

			final JsonString jsonString = Json.serializeToString(sample);

			final String msg = jsonString.toString();
			final SNSPublishRequestSpecs pspec = SNS.component().newPublishRequestSpecs();
			pspec.topicArn = this.topicArn;
			pspec.messageString = msg;

			final SNSPublishRequest pr = SNS.component().newPublishRequest(pspec);

			final SNSPublishResult result = snsClient.publish(pr);

		} catch (final Throwable e) {
			L.e(e);
		} finally {
			Sys.sleep(1);
		}
	}

}
