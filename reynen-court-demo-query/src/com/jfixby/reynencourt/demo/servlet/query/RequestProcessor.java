
package com.jfixby.reynencourt.demo.servlet.query;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jfixby.reynencourt.demo.DataSample;
import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.debug.DebugTimer;
import com.jfixby.scarabei.api.floatn.Float2;
import com.jfixby.scarabei.api.geometry.Geometry;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.io.InputStream;
import com.jfixby.scarabei.api.io.OutputStream;
import com.jfixby.scarabei.api.io.StreamPipe;
import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.java.Int;
import com.jfixby.scarabei.api.java.gc.GCFisher;
import com.jfixby.scarabei.api.java.gc.MemoryStatistics;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.math.Average;
import com.jfixby.scarabei.api.math.FloatMath;
import com.jfixby.scarabei.api.names.Names;
import com.jfixby.scarabei.api.strings.Strings;
import com.jfixby.scarabei.api.sys.SystemInfoTags;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.api.util.Utils;
import com.jfixby.scarabei.api.util.path.RelativePath;
import com.jfixby.scarabei.api.ver.Version;

public class RequestProcessor {
	public static Version version;
	static {
		version = new Version();
		version.major = "0";
		version.minor = "0";
		version.build = "1";
		version.packageName = "com.jfixby.reynencourt.demo";
		version.versionCode = 1;

		SystemSettings.setStringParameter(Version.Tags.PackageName, version.packageName);
		SystemSettings.setStringParameter(Version.Tags.VersionCode, version.versionCode + "");
		SystemSettings.setStringParameter(Version.Tags.VersionName, version.getPackageVersionString());

	}
	final String instance_id;
	final long MAX_BYTES_TO_READ = 1024 * 100;
	long request = 0;
	private final DataSampleStorage storage;

	public RequestProcessor (final DataSampleStorage storage) {
		super();
		this.instance_id = AWSServletUtil.readInstanceID();
		this.average = FloatMath.newAverage(500);
		this.storage = storage;
	}

	public void processRequest (final HttpServletRequest request, final HttpServletResponse response) {
		final EntryPointArguments arg = new EntryPointArguments();
		String client_ip_addr = "unknown";
		try {
			final boolean https = request.isSecure();

			if (this.PROTOCOL_POLICY() == PROTOCOL_POLICY.HTTPS_ONLY && !https) {
				ServletUtil.forceHttpS(request, response);
				return;
			}
			if (this.PROTOCOL_POLICY() == PROTOCOL_POLICY.HTTP_ONLY && https) {
				ServletUtil.forceHttp(request, response);
				return;
			}

			String path_info = request.getPathInfo();
			if (path_info == null) {
				path_info = "";
			}

			arg.relativePath = Utils.newRelativePath(path_info);

			final String reqUrl = request.getRequestURL() + "";
			if (reqUrl.toLowerCase().endsWith("favicon.ico")) {
				return;
			}
			if (reqUrl.toLowerCase().endsWith("health")) {
				arg.isHeathCheck = true;
			}

			arg.timestamp = System.currentTimeMillis();
			arg.request_number = this.request_number();
			final HttpSession session = request.getSession();
			final String session_id = session.getId();
			arg.requestID = Names.ROOT().child("iid-" + this.instance_id).child("sid-" + session_id)
				.child("rqn-" + arg.request_number);

			L.d("----Request[" + arg.requestID + "]----------------------------------------------");

			final ServletInputStream client_to_server_stream = request.getInputStream();
			final ServletOutputStream server_to_client_stream = response.getOutputStream();
			final Map<String, List<String>> client_to_server_headers = Collections.newMap();
			final Enumeration<String> header_names = request.getHeaderNames();

			while (header_names.hasMoreElements()) {
				final String key = header_names.nextElement();
				final String value = request.getHeader(key);
				client_to_server_headers.put(key, Collections.newList(value));
			}
			client_to_server_headers.put("reqUrl", Collections.newList(reqUrl));
			client_to_server_headers.put("path_info", Collections.newList(path_info));
			client_ip_addr = ServletUtil.getClientIpAddr(request);
			client_to_server_headers.put(SystemInfoTags.Net.client_ip.toString(), Collections.newList(client_ip_addr));

			final java.util.Map<String, String[]> param_map = request.getParameterMap();
			final Iterator<String> iterator = param_map.keySet().iterator();
			while (iterator.hasNext()) {
				final String key = iterator.next();
				final String[] values = param_map.get(key);
				final List<String> list = Collections.newList(values);
				client_to_server_headers.put(key.toLowerCase(), list);
			}
			final Map<String, String> server_to_client_headers = Collections.newMap();

			arg.inputHeaders = client_to_server_headers;
			arg.server_to_client_stream = server_to_client_stream;
			arg.client_to_server_stream = client_to_server_stream;
// arg.print();
			this.processRequest(arg);

			final Iterator<String> i = server_to_client_headers.keys().iterator();
			final String new_location = server_to_client_headers.get("WEB_SERVER.REDIRECT");
			if (new_location != null) {
				response.sendRedirect(new_location);
				L.d("redirect: " + new_location);
			} else {
				while (i.hasNext()) {
					final String key = i.next();
					final String value = server_to_client_headers.get(key);
					response.setHeader(key, value);
				}
			}
			server_to_client_stream.flush();
			server_to_client_stream.close();

		} catch (final Throwable e) {
			L.e("failed request " + arg.requestID, e);

		}
		final long processed_in = System.currentTimeMillis() - arg.timestamp;
		L.d("request", arg.requestID);
		L.d("processed in", processed_in + " ms");
		L.d("          ip", client_ip_addr);
		final MemoryStatistics memoryStats = GCFisher.getMemoryStatistics();
		L.d("memory usage", memoryStats);
	}

	void processRequest (final EntryPointArguments arg) {
		try {
			final String len = getHeader("content-length", arg.inputHeaders);
			if (arg.isHeathCheck) {
				this.sayHello(arg);
				return;
			}
			if (len == null) {
				this.sayHello(arg);
				return;
			}
			if ("0".equals(len)) {
				this.sayHello(arg);
				return;
			}

			if (arg.relativePath.equals(this.PATH_CONSUME)) {
				this.consumeDataSample(arg);
			} else if (arg.relativePath.equals(this.PATH_query)) {

				final String oper = getHeader("agg", arg.inputHeaders);
				if (oper == null) {
					this.queryFromTo(arg);
				} else if (oper.equals("avg")) {
					this.queryFromToAVG(arg);
				} else if (oper.equals("sum")) {
					this.queryFromToSUM(arg);
				} else {
					this.queryFromTo(arg);
				}

			} else if (arg.relativePath.equals(this.PATH_archive)) {
				this.archive(arg);
			} else if (arg.relativePath.equals(this.PATH_unarchive)) {
				this.unarchive(arg);
			}

		} catch (

		final Throwable e) {
			L.e("failed request " + arg.requestID, e);
		}
// outputHeaders.print("outputHeaders");

		this.addValueToAverage(this.measureProcessingTime(arg), null, null);
	}

	private void unarchive (final EntryPointArguments arg) throws IOException {
		final String archiveId = (getHeader("archive_id", arg.inputHeaders));

		final InputStream input_stream = this.storage.readArchive(archiveId);
		final OutputStream output_stream = IO.newOutputStream( () -> arg.server_to_client_stream);
		final StreamPipe pipe = IO.newStreamPipe(input_stream, output_stream, null);

		input_stream.open();
		output_stream.open();
		pipe.transferAll();
		output_stream.close();
		input_stream.close();
	}

	private void archive (final EntryPointArguments arg) throws IOException {
		final long fromTimestamp = Long.parseLong(getHeader("from", arg.inputHeaders));
		final long toTimestamp = Long.parseLong(getHeader("to", arg.inputHeaders));
		final String result = this.storage.archive(fromTimestamp, toTimestamp);
		final OutputStream os = IO.newOutputStream( () -> arg.server_to_client_stream);
		os.open();
		final byte[] bytes = result.toString().getBytes();
		os.write(bytes);
		os.flush();
		os.close();
	}

	private void queryFromToSUM (final EntryPointArguments arg) throws IOException {
		final long fromTimestamp = Long.parseLong(getHeader("from", arg.inputHeaders));
		final long toTimestamp = Long.parseLong(getHeader("to", arg.inputHeaders));
		final String result = this.storage.aggregateSum(fromTimestamp, toTimestamp);
		final OutputStream os = IO.newOutputStream( () -> arg.server_to_client_stream);
		os.open();
		final byte[] bytes = result.toString().getBytes();
		os.write(bytes);
		os.flush();
		os.close();
	}

	private void queryFromToAVG (final EntryPointArguments arg) throws IOException {
		final long fromTimestamp = Long.parseLong(getHeader("from", arg.inputHeaders));
		final long toTimestamp = Long.parseLong(getHeader("to", arg.inputHeaders));
		final String result = this.storage.aggregateAverage(fromTimestamp, toTimestamp);
		final OutputStream os = IO.newOutputStream( () -> arg.server_to_client_stream);
		os.open();
		final byte[] bytes = result.toString().getBytes();
		os.write(bytes);
		os.flush();
		os.close();
	}

	private void queryFromTo (final EntryPointArguments arg) throws IOException {
		final long fromTimestamp = Long.parseLong(getHeader("from", arg.inputHeaders));
		final long toTimestamp = Long.parseLong(getHeader("to", arg.inputHeaders));
		final Collection<DataSample> result = this.storage.queryFromToTimestamp(fromTimestamp, toTimestamp);
		final OutputStream os = IO.newOutputStream( () -> arg.server_to_client_stream);
		os.open();
		this.storage.writeDataSamplesToStream(result, os);
		os.close();
	}

	RelativePath PATH_ROOT = Utils.newRelativePath();
	RelativePath PATH_CONSUME = Utils.newRelativePath().child("api").child("consume");
	RelativePath PATH_query = Utils.newRelativePath().child("api").child("data");
	RelativePath PATH_archive = Utils.newRelativePath().child("api").child("archive");
	RelativePath PATH_unarchive = Utils.newRelativePath().child("api").child("unarchive");

	public void consumeDataSample (final EntryPointArguments arg) throws IOException {
		final InputStream is = IO.newInputStream( () -> arg.client_to_server_stream);
		is.open();
		final ByteArray inputBytes = is.readAll();
		final String raw_json = Strings.newString(inputBytes);
		final DataSample dataSample = Json.deserializeFromString(DataSample.class, raw_json);
		is.close();

		this.storage.consumeDataSample(dataSample);

		final OutputStream os = IO.newOutputStream( () -> arg.server_to_client_stream);
		os.open();
// os.write(compressedResponse);
		os.close();
	}

	public PROTOCOL_POLICY PROTOCOL_POLICY () {
		return Debug.checkNull("PROTOCOL_POLICY", http_mode);
	}

	private static PROTOCOL_POLICY http_mode = PROTOCOL_POLICY.ALLOW_BOTH;
	public final String SEPARATOR = System.getProperty("line.separator");

	public String getHealthReport (final HealthReportType type, final EntryPointArguments arg) {

		final StringBuilder msg = new StringBuilder();
		if (type == HealthReportType.LATEST) {
			this.readServiceState();
		}
		msg.append("             <Service Health>").append(this.SEPARATOR);
		final double val = this.measureProcessingTime(arg);
		final Float2 value = Geometry.newFloat2();
		final Int size = new Int();
		this.addValueToAverage(val, value, size);
		final double sec = FloatMath.roundToDigit(val, 3);
		msg.append("         server time: " + new Date()).append(this.SEPARATOR);
		final MemoryStatistics memoryStats = GCFisher.getMemoryStatistics();
		msg.append("             version: " + version.getPackageVersionString()).append(this.SEPARATOR);
		msg.append(this.SEPARATOR);
		msg.append("        memory usage: " + memoryStats).append(this.SEPARATOR);
		msg.append(this.SEPARATOR);
		if (arg.inputHeaders != null) {
			msg.append("           client ip: " + arg.inputHeaders.get(SystemInfoTags.Net.client_ip)).append(this.SEPARATOR);
		}
		msg.append("          request id: " + arg.requestID).append(this.SEPARATOR);
		msg.append(this.SEPARATOR);
		msg.append("request processed in: " + sec + " sec").append(this.SEPARATOR);
		msg.append("average for the last: " + size.value + " requests").append(this.SEPARATOR);
		msg.append("                  is: " + FloatMath.roundToDigit(value.getX(), 3) + " sec").append(this.SEPARATOR);

		return msg.toString();
	}

	public final void readServiceState () {
		final DebugTimer timer = Debug.newTimer();
	}

	Average average;

	final synchronized private void addValueToAverage (final double val, final Float2 value, final Int size) {
		this.average.addValue(val);
		if (size != null) {
			size.value = this.average.size();
		}
		if (value != null) {
			value.setX(this.average.getAverage());
		}
	}

	private synchronized long request_number () {
		this.request++;
		return this.request;
	}

	private void sayHello (final EntryPointArguments arg) throws IOException {
		String report;
		if (arg.isHeathCheck) {
			report = this.getHealthReport(HealthReportType.ON_LAST_CALL, arg);
		} else {
			report = this.getHealthReport(HealthReportType.LATEST, arg);
		}
		arg.server_to_client_stream.write(report.getBytes());
	}

	private double measureProcessingTime (final EntryPointArguments arg) {
		return (System.currentTimeMillis() - arg.timestamp) / 1000d;
	}

	static public String getHeader (final String string, final Map<String, List<String>> inputHeaders) {
		final List<String> list = inputHeaders.get(string);
		if (list == null) {
			return null;
		}
		if (list.size() == 0) {
			return null;
		}
		return list.getElementAt(0);
	}
}
