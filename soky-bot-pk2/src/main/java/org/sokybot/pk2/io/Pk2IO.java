package org.sokybot.pk2.io;

import lombok.extern.slf4j.Slf4j;

import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2.Pk2CryptoUtils;
import org.sokybot.pk2.exception.Pk2IOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class Pk2IO {

	private Pk2IO() {
	}

	private static void ensureValidity(final String pk2FilePath, long jmxFileLocation, long jmxFileSize)
			throws IOException {

		// because JMXFile is Immutable and its builder access only from within
		// if(pk2FilePath == null || pk2FilePath.isBlank()) {
		// throw new IllegalArgumentException("The pk2file " + pk2FilePath + " is no
		// longer exists") ;
		// }

		// Validate.validState(!StringUtils.isBlank(pk2FilePath), "Pk2File path is not
		// valid");

		Path filePath = Path.of(pk2FilePath);

		// Validate.validState(Files.exists(filePath), "Pk2File At Path %s is not
		// exists", pk2FilePath);

		if (!Files.exists(filePath)) {
			throw new IllegalArgumentException("The pk2file " + pk2FilePath + " is no longer exists");
		}

		if (jmxFileSize < 0) {
			throw new IllegalArgumentException("Invalid JMXFile Attribute ( size : " + jmxFileSize + " )");
		}

		if (jmxFileLocation < 0) {
			throw new IllegalArgumentException("Invalid JMXFile Attribute ( location : " + jmxFileLocation + " ) ");
		}

		// Validate.isTrue(fileSize > 0, "File %s is empty !!!", pk2FilePath);

		// Validate.validState((jmxFileLocation >= 0 && jmxFileSize >= 0),
//				"Invalid JMXFile Attributes position %s , size %s ", jmxFileLocation, jmxFileSize);

		long pk2FileSize = Files.size(filePath);

		long fileUb = (jmxFileLocation + jmxFileSize) - 1;

		if (fileUb <= -1 || fileUb >= pk2FileSize) {
			throw new IllegalArgumentException("Invalid JMXFile Boundaries start  " + jmxFileLocation + " , end "
					+ (jmxFileLocation + jmxFileSize) + "Where file size " + pk2FileSize);
		}
		
		//Validate.exclusiveBetween(-1, pk2FileSize, (jmxFileLocation + jmxFileSize) - 1,
			//	"Invalid JMXFile Boundaries start  " + jmxFileLocation + " , end " + (jmxFileLocation + jmxFileSize)
				//		+ "Where file size " + pk2FileSize);

		// we must validate that Files.size > pos + size

	}

	/**
	 * TODO write description for this method 
	 * @param jmxFile
	 * @return InputStream for reading from the input JMXFile
	 * @throws IllegalArgumentException if the input {@code jmxFile} has invalid attributes 
	 * @throws Pk2IOException if
	 */
	public static InputStream getInputStream(JMXFile jmxFile) {
		String pkFilePath = jmxFile.getPkFilePath();
		long position = jmxFile.getPosition();
		int size = jmxFile.getSize();

		InputStream res;
		try {
			ensureValidity(pkFilePath, position, size);
			res = new JMXFileInputStream(pkFilePath, position, size);
			if (Pk2CryptoUtils.isEncryptedTextFile(jmxFile)) {
				res = new EncryptedTextInputStream(res, jmxFile.getSize());
			}

			return res;
		} catch (IOException e) {
			throw new Pk2IOException(
					"Could not read from JMXFile " + jmxFile.getName() + " at " + jmxFile.getPkFilePath(),
					pkFilePath, e);
			// res = InputStream.nullInputStream() ;
			// log.error("Could not read jmx file " + jmxFile.getName() +" at " +
			// jmxFile.getPkFilePath() , e) ;
		}

	}

	public static OutputStream getOutputStream(JMXFile jmxFile) {
		// TODO implement output stream for JMXFiles
		throw new UnsupportedOperationException();
	}

}
