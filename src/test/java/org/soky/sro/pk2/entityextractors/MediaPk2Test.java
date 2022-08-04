package org.soky.sro.pk2.entityextractors;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MediaPk2Test {

    static IMediaPk2 mediaPk2 ;


    @BeforeAll
    void init() {
        String gamePath =  "E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019";
        mediaPk2 = MediaPk2.createInstance(gamePath) ;

    }

    @AfterAll
    void dest() {
        try {
            this.mediaPk2.close();
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
}