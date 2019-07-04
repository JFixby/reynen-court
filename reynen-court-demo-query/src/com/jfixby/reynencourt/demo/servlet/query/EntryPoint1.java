
package com.jfixby.reynencourt.demo.servlet.query;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfixby.reynencourt.demo.Deployment;
import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.scarabei.api.err.Err;

public class EntryPoint1 extends HttpServlet {

	private static final long serialVersionUID = 3199114713498054823L;
	private static RequestProcessor processor;

	static {
		try {
			final DataSampleStorage storage = Deployment.deploy();

			processor = new RequestProcessor(storage);
		} catch (final IOException e) {
			Err.reportError(e);
		}
	}

	@Override
	protected void doGet (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		processor.processRequest(request, response);
	}

	@Override
	protected void doPost (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		processor.processRequest(request, response);
	}

}
