
package com.jfixby.reynencourt.demo.storage.v001;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.CollectionConverter;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.io.InputStream;
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
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.api.sqs.SQSClienSpecs;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;
import com.jfixby.scarabei.aws.api.sqs.SQSComponent;
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageResult;

public class NotificationsStorage_V_0_0_1 implements DataSampleStorage {

	public static NotificationsStorage_V_0_0_1 newNotificationsStorage (final NotificationsStorageSpecs specs) {
		final NotificationsStorage_V_0_0_1 S = new NotificationsStorage_V_0_0_1(specs);

		return S;
	}

	private final String inputQueueURL;
	private final AWSCredentialsProvider awsKeys;
	private final String topicArn;

	NotificationsStorage_V_0_0_1 (final NotificationsStorageSpecs specs) {
		this.inputQueueURL = Debug.checkNull("queueURL", specs.inputQueueURL);
		Debug.checkEmpty("queueURL", this.inputQueueURL);
		this.awsKeys = specs.aWSCredentialsProvider;
		Debug.checkNull("awsKeys", this.awsKeys);
		this.topicArn = specs.snsTopicARN;
		Debug.checkNull("topicArn", this.topicArn);
	}

	// Consume data of structure
	// {
	// 'timestamp':<timestamp>,
	// 'event_id':event_id,
	// 'event_type':event_type,
	// 'value':value
	// }
	@Override
	public void consumeDataSample (final DataSample sample) {
		try {
			final SNSComponent sns = SNS.invoke();
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
// L.d(result);
		} catch (final Throwable e) {
			L.e(e);
		} finally {
			Sys.sleep(1);
		}
	}

	@Override
	public Collection<DataSample> queryFromToTimestamp (final long fromTimestamp, final long toTimestamp) {
		final SQSComponent sqs = SQS.invoke();
		final SQSClienSpecs clSpecs = sqs.newSQSClienSpecs();
		clSpecs.setAWSCredentialsProvider(this.awsKeys);

		final SQSClient sqsClient = sqs.newClient(clSpecs);

		final SQSReceiveMessageParams params = sqs.newReceiveMessageParams();
		params.setQueueURL(this.inputQueueURL);
		final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);
		final SQSReceiveMessageResult result = sqsClient.receive(request);

		final Collection<SQSMessage> msgs = result.listMessages();
		L.d("msgs", msgs);
		final List<DataSample> samples = Collections.newList();
		Collections.convertCollection(msgs, samples, this.messageToDataSampleConverter);

		return samples;
	}

	private final CollectionConverter<SQSMessage, DataSample> messageToDataSampleConverter = new CollectionConverter<SQSMessage, DataSample>() {
		@Override
		public DataSample convert (final SQSMessage input) {
			return NotificationsStorage_V_0_0_1.messageToDataSample(input);
		}
	};

	protected static DataSample messageToDataSample (final SQSMessage input) {
		final String bodyString = input.getBody();
		final DataSample s = Json.deserializeFromString(DataSample.class, bodyString);
		return s;
	}

	@Override
	public String aggregateAverage (final long fromTimestamp, final long toTimestamp) {
		Err.throwNotImplementedYet();
		return null;
	}

	@Override
	public String aggregateSum (final long fromTimestamp, final long toTimestamp) {
		Err.throwNotImplementedYet();
		return null;
	}

	@Override
	public String archive (final long fromTimestamp, final long toTimestamp) {
		Err.throwNotImplementedYet();
		return null;
	}

	@Override
	public InputStream readArchive (final String archiveId) {
		Err.throwNotImplementedYet();
		return null;
	}

}
