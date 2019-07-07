
package example;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.io.InputStream;
import com.jfixby.scarabei.api.io.OutputStream;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class Hello implements RequestStreamHandler {

	static {
		ScarabeiDesktop.deploy();
	}

	@Override
	public void handleRequest (final java.io.InputStream javaInput, final java.io.OutputStream javaOutput, final Context context) {
		try {

			final InputStream is = IO.newInputStream( () -> javaInput);
			is.open();
			final String inputString = is.readAllToString();
			is.close();

			context.getLogger().log("Input: inputString>" + inputString + "<");

// final APIGatewayProxyRequestEvent rq = Json.deserializeFromString(APIGatewayProxyRequestEvent.class, inputString);
// final Map<String, String> params = rq.getQueryStringParameters();
// final String ask = params.get("ask");
//
			final OutputStream os = IO.newOutputStream( () -> javaOutput);
// context.getLogger().log("Input: RQ>" + ask + "<");
			final String response = "RESPONSE: " + inputString;

			os.open();
			os.write(response.getBytes());
			os.close();

// <APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
		} catch (final IOException e) {
			e.printStackTrace();
			Sys.sleep(100);
			Err.reportError(e);
		}
	}

}
