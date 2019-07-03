
package com.jfixby.reynencourt.demo.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.names.ID;
import com.jfixby.scarabei.api.sys.settings.ExecutionMode;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class ConsumeDataSample implements RequestHandler<DataSample, String> {

	public static void init () {
	}

	static {
		ScarabeiDesktop.deploy();
		SystemSettings.setExecutionMode(ExecutionMode.EARLY_DEVELOPMENT);
		final Map<ID, Object> settings = SystemSettings.listAllSettings();
		L.d("System settings", settings);
		DefaultDataSample = Json.deserializeFromString(DataSample.class, "");
	}

	static final DataSample DefaultDataSample;

	@Override
	public String handleRequest (final DataSample input, final Context context) {
		if (DefaultDataSample.equals(input)) {
			return "No DataSample found: " + input;
		}
		context.getLogger().log("Input: " + input);
		return "Output: <" + input + ">";
	}
}