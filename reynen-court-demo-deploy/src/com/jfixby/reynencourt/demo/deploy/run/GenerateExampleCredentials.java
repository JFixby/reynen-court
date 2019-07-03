
package com.jfixby.reynencourt.demo.deploy.run;

import java.io.IOException;

import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.aws.api.AWSCredentials;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class GenerateExampleCredentials {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		{
			final AWSCredentials credentials = new AWSCredentials();

			credentials.secretKeyID = "secretKeyID";
			credentials.regionName = "us-west-1";
			credentials.accessKeyID = "accessKeyID-" + Sys.SystemTime().currentTimeMillis();

			final File file = LocalFileSystem.ApplicationHome().child("credentials").child("aws-credentials.example.json");
			final String data = Json.serializeToString(credentials).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}
	}

}
