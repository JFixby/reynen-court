
package com.jfixby.reynencourt.sns.demo.storage.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

public class LambdaHandler implements RequestHandler<SQSEvent, Void> {
	@Override
	public Void handleRequest (final SQSEvent event, final Context context) {
		for (final SQSMessage msg : event.getRecords()) {
			System.out.println(new String(msg.getBody()));
		}
		return null;
	}
}
