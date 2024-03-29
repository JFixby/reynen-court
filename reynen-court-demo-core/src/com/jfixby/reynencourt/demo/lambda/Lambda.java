
package com.jfixby.reynencourt.demo.lambda;

import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.json.Json;

public abstract class Lambda {

	public static <T> LambdaJsonResponse respondMessage (final T result, final int statusCode, final Map<String, String> headers) {
		final LambdaJsonResponse R = new LambdaJsonResponse();
		R.statusCode = statusCode;
		if (headers != null) {
			R.headers.putAll(headers.toJavaMap());
		}
		R.body = Json.serializeToString(result).toString();
		return R;
	}

	public static <T> LambdaJsonResponse respondMessage (final T result) {
		return respondMessage(result, 200, null);
	}

	public static <T> LambdaJsonResponse respondMessage (final T result, final int statusCode) {
		return respondMessage(result, statusCode, null);
	}
}
