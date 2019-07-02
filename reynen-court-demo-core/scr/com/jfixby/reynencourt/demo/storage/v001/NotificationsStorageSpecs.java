
package com.jfixby.reynencourt.demo.storage.v001;

import com.jfixby.reynencourt.demo.storage.StorageIndex;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsStorageSpecs {

	public AWSCredentialsProvider aWSCredentialsProvider;
	public boolean debugMode;
	public String sQSMailboxPrefix;
	public long separatorStartProcessingDelay;
	public String inputQueueURL;
	public String snsTopicARN;
	public File storageFolder;
	public StorageIndex storageIndex;

}
