package org.soky.sro.pk2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import  static  org.soky.sro.pk2.io.Pk2IO.getInputStream ;

@Slf4j
public class Pk2FileTest {


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
    public void testPk2FileFindWithLimitMethod() {

       List<JMXFile> files = pk2File.find("(?i)gateport.txt", 1);

        log.info("Files Size {} " , files.size());
        Assertions.assertEquals(1, files.size());

    }



    @Test
    public void testQueryFilesMatchRegex() {
        // query each file with name (itemdata_[0-9]+.txt)
        String regex = "itemdata_[0-9]+.txt" ;
       List<JMXFile> files =  pk2File.find(regex) ;

        Assertions.assertFalse(files.isEmpty());

       files.forEach((f)-> log.info("Item Data File : {} " ,f));

    }

    @Test
     void testQueryFileMatchRegex2(){
        String regex = "skilldata_(\\d+)(enc)?.txt$" ;

        List<JMXFile> files = pk2File.find(regex) ;
        Assertions.assertFalse(files.isEmpty());
        files.forEach((f)->{
            log.info("Skill Data File : {} " , f);
        });

    }

    @Test
    void testQueryEncryptedSkillDataFiles() {
        String regex = "skilldata_(\\d+)(enc).txt$" ;

        List<JMXFile> files = this.pk2File.find(regex) ;
        Assertions.assertTrue(!files.isEmpty());
        files.forEach((f)->{
            log.info("Skill Data File : {} " , f);
        });

    }
    // this test case run correctly for LegionSRO_15_08_2019 Media.Pk2
    @Test
     void testQueryCollectionAtParticularDir() {

        String requiredFiels = "\\icon64\\premium\\avatar\\(^([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+\\.(ddj)$)" ;

        List<JMXFile> files = this.pk2File.find(requiredFiels) ;

        Assertions.assertEquals(20 , files.size());


    }
    @Test
    public void testQueryCollectionAtParticularDir2() {

        String requiredFiels = "\\config\\(^([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+\\.(txt)$)" ;

        List<JMXFile> files = this.pk2File.find(requiredFiels) ;

        Assertions.assertEquals(6 , files.size());


    }

    @Test
    public void testQueryParticularFile() {
        String requiredFile = "\\config\\mp.txt" ;
        List<JMXFile> files = this.pk2File.find(requiredFile) ;

        Assertions.assertTrue(files.size() == 1);
    }

    @Test
    public void testFindNotFound() {


        List<JMXFile> files =  this.pk2File.find("NotFoundFile.NotFound" , -1) ;

        log.info("Files Size {} " , files.size());
        Assertions.assertTrue(files.size() == 0);

    }





    @Test
   public void testFindWithLimit0() {

    String initTest = "MyFile.txt" ;
    String regex = "(([a-zA-Z0_9]+)?.txt$)" ;
    Assertions.assertTrue(Pattern.matches(regex , initTest));
    List<JMXFile> files =  this.pk2File.find(regex , 0) ;

    log.info("Files Size {} " , files.size());
    Assertions.assertTrue(files.size() == 0);

   }

 @Test
    public void testFindWithLimit1() {

     String initTest = "MyFile.txt" ;
     String regex = "(([a-zA-Z0_9]+)?.txt$)" ;
     Assertions.assertTrue(Pattern.matches(regex , initTest));
      List<JMXFile> files =  this.pk2File.find(regex , 1) ;

     log.info("Files Size {} " , files.size());
     Assertions.assertTrue(files.size() == 1);

    }


    @Test
    public void testFindWithLimit2() {

     String initTest = "MyFile.txt";
     String regex = "(([a-zA-Z0_9]+)?.txt$)";
     Assertions.assertTrue(Pattern.matches(regex, initTest));
     List<JMXFile> files = this.pk2File.find(regex, 2);

     log.info("Files Size {} ", files.size());
     Assertions.assertTrue(files.size() == 2);

    }

 @Test
    public void testPk2FileWithNullFilePath() {

    }

    @Test
    public void testPk2FileWithNotValidFilePath() {

    }

    @Test
    public void testPk2FileInterfaceWithNotPk2File() {

    }

}
