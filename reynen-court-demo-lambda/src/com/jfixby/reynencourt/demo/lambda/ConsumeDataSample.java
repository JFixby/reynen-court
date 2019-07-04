
package com.jfixby.reynencourt.demo.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.LambdaResponse;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.names.ID;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class ConsumeDataSample implements RequestHandler<DataSample, LambdaResponse> {

	public static void init () {
	}

	static {
		ScarabeiDesktop.deploy();
		SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT);
		final Map<ID, Object> settings = SystemSettings.listAllSettings();
		L.d("System settings", settings);
		DefaultDataSample = Json.deserializeFromString(DataSample.class, "{}");
	}

	static final DataSample DefaultDataSample;

	@Override
	public LambdaResponse handleRequest (final DataSample input, final Context context) {
		context.getLogger().log("Input: " + input);
		if (input == null || DefaultDataSample.equals(input)) {
			return Lambda.respondMessage("No DataSample found: <" + input + ">", 400);
		}
		return Lambda.respondMessage("Output: <" + input + ">");
	}

}
