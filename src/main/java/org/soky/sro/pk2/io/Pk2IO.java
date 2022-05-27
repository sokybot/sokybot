package org.soky.sro.pk2.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.soky.sro.pk2.JMXFile;
import org.soky.sro.pk2.Pk2CryptoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class Pk2IO {

    private Pk2IO(){}

    private static  void ensureValidity(final String pk2FilePath , long jmxFileLocation , long jmxFileSize ) throws IOException {
 
        Validate.validState(!StringUtils.isBlank(pk2FilePath) ,
                "Pk2File path is not valid"  );

        Path filePath = Path.of(pk2FilePath);


        Validate.validState(
                        Files.exists(filePath) ,
                "Pk2File At Path %s is not exists" , pk2FilePath);

        long fileSize = Files.size(filePath) ;
        Validate.isTrue(fileSize > 0 , "File %s is empty !!!"  , pk2FilePath);

        Validate.validState((jmxFileLocation >= 0 &&
                        jmxFileSize >= 0 ) ,
                "Invalid JMXFile Attributes position %s , size %s " , jmxFileLocation , jmxFileSize );


        Validate.exclusiveBetween(-1 ,
                fileSize ,
                (jmxFileLocation + jmxFileSize) -1 ,
                "Invalid JMXFile Boundaries start  " +
                        jmxFileLocation + " , end " +
                        (jmxFileLocation + jmxFileSize -1)  + "Where file size " + fileSize  );

        //we must validate that Files.size > pos + size

    }



    public static InputStream getInputStream(JMXFile jmxFile) throws IOException {
            String pkFilePath = jmxFile.getPkFilePath();
            long position = jmxFile.getPosition();
            int size = jmxFile.getSize();
            ensureValidity(pkFilePath, position, size);
            InputStream res = new JMXFileInputStream(pkFilePath, position, size);
            if (Pk2CryptoUtils.isEncryptedTextFile(jmxFile)) {
              res = new EncryptedTextInputStream(res , jmxFile.getSize()) ;
            }

            return res ;

    }

    public static OutputStream getOutputStream(JMXFile jmxFile) {
      throw new UnsupportedOperationException() ;
    }

}
