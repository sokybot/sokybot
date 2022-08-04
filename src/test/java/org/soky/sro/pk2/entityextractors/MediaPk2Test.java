package org.soky.sro.pk2.entityextractors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MediaPk2Test {

    static IMediaPk2 mediaPk2 ;


    @BeforeAll
   static void init() {
        String gamePath =  "E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019";
        mediaPk2 = MediaPk2.createInstance(gamePath) ;
    }

    @AfterAll
    static void dest() {
        try {
          mediaPk2.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getEntityNames() {
        this.mediaPk2.getEntityNames().forEach((key , value)->{
            log.info("({} , {} ) " , key , value);
        });
    }

    
    @Test
    void testVersionExtraction() { 
    	
      int ver = 	mediaPk2.extractVersion() ;
     
      log.info("getten version is {}" , ver) ; 
      assertNotEquals(ver, -1);
    
    }
    
    @Test
    void testPortExtraction() { 
     
    	int port = mediaPk2.extractPort() ; 
    	log.info("getten port is {}" , port) ; 
        assertNotEquals(port, -1);
    	
    }
    

}
