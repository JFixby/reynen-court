
package com.jfixby.reynencourt.demo.deploy.run;

import java.io.IOException;

import com.jfixby.reynencourt.demo.deploy.DeploymentConfig;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.red.desktop.ScarabeiDesktop;

public class GenerateExampleConfig {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		{
			final DeploymentConfig config = new DeploymentConfig();
			config.s3BucketName = "reynen-court-demo-jars-bucket";
			final File file = LocalFileSystem.ApplicationHome().child("configs").child("deploy-config.example.json");
			final String data = Json.serializeToString(config).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}
	}

}
