package org.soky.sro.pk2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.soky.sro.pk2.Pk2CryptoUtils.ENCRYPTED_TEXT_FOOTER;
import static org.soky.sro.pk2.io.Pk2IO.getInputStream;

@Slf4j
class Pk2CryptoUtilsTest {


    static private String filePath ;
    static private Pk2File pk2File ;


    @BeforeAll
    static void init() {
        filePath =  "E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019\\Media.pk2";
        pk2File = new Pk2File(filePath);


    }

    @AfterAll
    static  void destroy() throws IOException {
        pk2File.close();
    }



    @Test
    void testFooterOfEncFiles() {
        String regex = "skilldata_(\\d+)(enc).txt$" ;
            pk2File.find(regex).forEach((jmxFile)->{
                try {
                    InputStream inputStream =  getInputStream(jmxFile) ;

                    inputStream.skip(jmxFile.getSize() - ENCRYPTED_TEXT_FOOTER.length  );
                    byte actualFooterBytes [] = inputStream.readNBytes(ENCRYPTED_TEXT_FOOTER.length) ;
                    assertArrayEquals(ENCRYPTED_TEXT_FOOTER , actualFooterBytes);
                    byte b  =  (byte) inputStream.read() ;
                    assertEquals(-1 , b);
                    String expectedFooter = new String(ENCRYPTED_TEXT_FOOTER , StandardCharsets.UTF_16LE) ;
                    String actualFooter = new String(actualFooterBytes , StandardCharsets.UTF_16LE) ;
                    log.info("Actual : {} " , actualFooter);
                    log.info("Expected : {} " , expectedFooter);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            });
    }

    @Test
    void testEncryptionVerification() {
        String regex = "skilldata_(\\d+)(enc).txt$" ;
        // first get some encrypted files
        List<JMXFile> encryptedFiles = pk2File.find(regex);

        Assertions.assertFalse(encryptedFiles.isEmpty());

        encryptedFiles.forEach(f-> {
            try {
                Assertions.assertTrue(Pk2CryptoUtils.isEncryptedTextFile(f));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
    @Test
    void  testInputStreamForAnyEncText() {

        String query  = "^.*enc.*$" ;

        pk2File.find(query)
                .stream()
                .filter((f)->f.getName().endsWith(".txt"))
                .forEach((jmxFile)->{
                    try(InputStream input = getInputStream(jmxFile)) {
                        long actual  = IOUtils.consume(input) ;
                        log.info("Consuming {} bytes from file {}  , {}" , actual , jmxFile.getName() , jmxFile.getSize());
                        assertEquals(jmxFile.getSize() , actual);
                    }catch (IOException ex) {
                        log.error(ex.getMessage());
                    }
                });
    }
    @Test
    public void testExtractAnySkillDataEncText() {
        String query  = "^.*enc.*$" ;

        pk2File.find(query).stream().filter(file->file.getName().endsWith(".txt")).forEach((jmxFile)->{
            Path p = Paths.get("src/test/resources/" + jmxFile.getName()) ;
            try {
                Files.deleteIfExists(p) ;
                //   Files.createFile(p) ;
                Files.copy(getInputStream(jmxFile) , p) ;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }


    @Test
    void  testInputStreamForAnyEncText2() throws IOException {
        Path p = Paths.get("src/test/resources/encoutput.txt") ;

        //FileOutputStream out = new FileOutputStream(p.toFile()) ;
       // FileWriter writer = new FileWriter(p.toFile() , true) ;

        String query = "skilldata_(\\d+)(enc).txt$" ;
        AtomicLong totalSize = new AtomicLong();
        CountingInputStream countingInputStream   = new CountingInputStream(pk2File.find(query)
                .stream()
                .filter((f)->f.getName().endsWith(".txt"))
                .filter(f-> {
                    try {
                        return Pk2CryptoUtils.isEncryptedTextFile(f);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map((f)->{
                    try {
                        log.info("Encrypted File {} " , f.getName());
                        totalSize.addAndGet(f.getSize()) ;
                        return getInputStream(f) ;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .reduce(InputStream.nullInputStream() ,
                        ( sequenceInputStream, jmxFileInputStream)->
                                new SequenceInputStream( sequenceInputStream , jmxFileInputStream)));

        Files.deleteIfExists(p);
        Files.copy(countingInputStream , p) ; // Note++ can read this file properly


        long consumedBytes = countingInputStream.getByteCount() ;
        log.info("Consumed Bytes : {} " , consumedBytes);
        log.info("Total Size : {} " , totalSize.get());

        assertEquals(totalSize.get() , consumedBytes);
        //   writer.close();
    }




}