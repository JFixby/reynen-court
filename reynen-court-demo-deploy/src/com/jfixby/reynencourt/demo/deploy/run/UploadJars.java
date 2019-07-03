
package com.jfixby.reynencourt.demo.deploy.run;

import java.io.IOException;

import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.aws.api.s3.S3FileSystem;

public class UploadJars {

	public static void main (final String[] args) throws IOException {
		final S3FileSystem fs = Setup.setup();
		final File bucketRoot = fs.ROOT();

		LocalFileSystem.ApplicationHome().parent().child("").
	}

}
