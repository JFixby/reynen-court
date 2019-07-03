
package com.jfixby.reynencourt.demo.deploy.run;

import java.io.IOException;

import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FileConflistResolver;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.s3.S3FileSystem;

public class UploadJars {

	public static void main (final String[] args) throws IOException {
		final S3FileSystem fs = Setup.setup();
		final File bucketRoot = fs.ROOT();
		final File destination = bucketRoot.child("jars");

		final File source = LocalFileSystem.ApplicationHome().child("upload");

		fs.copyFolderContentsToFolder(source, destination, conflictResolution);

		L.d("Upload complete");
	}

	static FileConflistResolver conflictResolution = new FileConflistResolver() {
		@Override
		public boolean overwrite (final File fileToCopy, final File existing) throws IOException {
			final String newHash = fileToCopy.calculateHash().getMD5HashHexString();
			final String oldHash = existing.calculateHash().getMD5HashHexString();
			return !oldHash.equals(newHash);
		}
	};

}
