
package com.jfixby.reynencourt.sns.demo.storage.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<String, String> {
	@Override
	public String handleRequest (final String input, final Context context) {
		context.getLogger().log("Input: " + input);
		return "Output: <" + input + ">";
	}
}
