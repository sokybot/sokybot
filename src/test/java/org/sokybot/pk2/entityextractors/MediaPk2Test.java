package org.sokybot.pk2.entityextractors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.Pk2File;
import org.sokybot.pk2extractor.EntityExtractorFactory;
import org.sokybot.pk2extractor.IMediaPk2;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.sokybot.pk2.io.Pk2IO.getInputStream;

@Slf4j
class MediaPk2Test {

    static IMediaPk2 mediaPk2 ;
    static String gamePath ; 

    @BeforeAll
   static void init() {
         gamePath =  "E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019";
        
        mediaPk2 = new EntityExtractorFactory().getMediaPk2(gamePath) ;
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
    void testExtractDivInfo() throws Pk2ExtractionException { 
    DivisionInfo divInfo =	mediaPk2.extractDivisionInfo() ;
    assertNotNull(divInfo);
    assertNotEquals(0, divInfo.getDivisions().size());
    	log.info("Division Info : {} " , divInfo); 
    
    }
    
    @Test
    void testExtractPort() throws Pk2ExtractionException { 
    	int port = mediaPk2.extractPort() ; 
    	
    	assertNotEquals(0, port);
    	log.info("port number : {}" , port);
    }
    
    @Test
    void testExtractType() throws Pk2ExtractionException { 
    	SilkroadType type = mediaPk2.extractType() ; 
    	
    	log.info("Game Type : {}" , type);
    }
    @Test
    void testExtractItems()throws Pk2ExtractionException { 
    	mediaPk2.getItemEntities().forEach((item)->{
    		log.info(item.toString());
    		
    	});
    	
    }

    
    @Test
    void testVersionExtraction()throws Pk2ExtractionException { 
    	
      int ver = mediaPk2.extractVersion() ;
     
      log.info("getten version is {}" , ver) ; 
      assertNotEquals(ver, -1);
    
    }
    
    @Test
    void testPortExtraction()throws Pk2ExtractionException { 
     
    	int port = mediaPk2.extractPort() ; 
    	log.info("getten port is {}" , port) ; 
        assertNotEquals(port, -1);
    	
    }
    

}
