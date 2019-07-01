
package com.jfixby.reynencourt.sns.demo.storage;

import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.aws.api.sns.SNSClient;
import com.jfixby.scarabei.aws.api.sns.SNSClientSpecs;
import com.jfixby.scarabei.aws.api.sns.SNSComponent;

public class NotificationsStorage {

	public static NotificationsStorage newNotificationsStorage (final NotificationsStorageSpecs specs) {
		final NotificationsStorage S = new NotificationsStorage(specs);

		return S;
	}

	private final String inputQueueURL;
	private final AWSCredentialsProvider awsKeys;

	NotificationsStorage (final NotificationsStorageSpecs specs) {
		this.inputQueueURL = Debug.checkNull("queueURL", specs.inputQueueURL);
		Debug.checkEmpty("queueURL", this.inputQueueURL);

		this.awsKeys = specs.aWSCredentialsProvider;
		Debug.checkNull("awsKeys", this.awsKeys);

	}

	public void consumeDataSample (final DataSample sample) {
		try {
			final SNSComponent sns = SNS.component();

			final SNSClientSpecs clSpecs = sns.newClientSpecs();
			clSpecs.setAWSCredentialsProvider(this.awsKeys);

			final SNSClient snsClient = sns.newClient(clSpecs);

		} catch (final Throwable e) {
			L.e(e);
		} finally {
			Sys.sleep(1);
		}
	}

}
