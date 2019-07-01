
package com.jfixby.reynencourt.sns.demo.storage;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsStorageSpecs {

	public AWSCredentialsProvider aWSCredentialsProvider;
	public boolean debugMode;
	public String sQSMailboxPrefix;
	public long separatorStartProcessingDelay;
	public String inputQueueURL;
	public String SNSEndpoint;

}
