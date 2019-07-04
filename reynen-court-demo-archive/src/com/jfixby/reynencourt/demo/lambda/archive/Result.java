
package com.jfixby.reynencourt.demo.lambda.archive;

import java.util.HashMap;

public class Result {

	public int statusCode = 0;
	public HashMap<String, String> headers = new HashMap<>();
	public String body = "";

	public Result newResult () {
		return new Result();
	}

}
