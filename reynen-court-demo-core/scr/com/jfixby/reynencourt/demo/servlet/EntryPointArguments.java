
package com.jfixby.reynencourt.demo.servlet;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.names.ID;
import com.jfixby.scarabei.api.util.path.RelativePath;

public class EntryPointArguments {

	public long request_number;
	public ID requestID;
	public ServletOutputStream server_to_client_stream;
	public long timestamp;
	public Map<String, List<String>> inputHeaders;
	public ServletInputStream client_to_server_stream;
	public boolean isHeathCheck = false;
	public long receivedTimestamp;
	public String sentTimestamp;
	public String versionString;
	public String writtenTimestamp;
	public String token;
	public Long installID;
	public byte[] resializedBody;
	public String sessionID;
	public String subject;
	public String author;
	public RelativePath relativePath;

	public void print () {
		L.d("---[" + this.request_number + "]-----------------------------------");
		L.d("       requestID", this.requestID);
		L.d("       timestamp", this.timestamp);
	}

}
