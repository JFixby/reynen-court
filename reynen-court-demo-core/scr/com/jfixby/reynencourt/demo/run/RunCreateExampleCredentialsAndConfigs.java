
package com.jfixby.reynencourt.demo.run;

import java.io.IOException;

import com.jfixby.reynencourt.demo.ApplicationConfig;
import com.jfixby.reynencourt.demo.credentials.AWSCredentials;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.red.json.GoogleJson;

public class RunCreateExampleCredentialsAndConfigs {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleJson());
		{
			final AWSCredentials credentials = new AWSCredentials();

			credentials.secretKeyID = "secretKeyID";
			credentials.regionName = "regionName";
			credentials.accessKeyID = "accessKeyID-" + Sys.SystemTime().currentTimeMillis();

			final File file = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config").child("credentials")
				.child("aws-credentials.example.json");
			final String data = Json.serializeToString(credentials).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}

		{
			final ApplicationConfig config = new ApplicationConfig();

			config.inputQueueURL = "http://inout.queue";
			config.separatorStartProcessingDelay = 1000;
			config.sqsMailboxPrefix = "kns-usr-nbox";

			final File file = LocalFileSystem.ApplicationHome().parent().child("reynen-court-demo-config").child("configs")
				.child("storage-config.example.json");
			final String data = Json.serializeToString(config).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}

	}
}
