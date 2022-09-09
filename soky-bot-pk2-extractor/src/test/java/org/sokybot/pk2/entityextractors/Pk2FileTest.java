package org.sokybot.pk2.entityextractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sokybot.pk2.io.Pk2IO.getInputStream;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2.Pk2File;

import io.vavr.API.Match;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pk2FileTest {

	String gamePath = "E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019";

	@Test
	void testExtractPort1() {
		IPk2File file = new Pk2File(gamePath + "\\Media.pk2");
		Integer port = Try.of(() -> {
			JMXFile jmx = file.findFirst("(?i)gateportt.txt").get();
			String thePort = IOUtils.toString(getInputStream(jmx), "UTF-8");
			return Integer.parseInt(thePort.trim());
		}).onFailure(NoSuchElementException.class, (ex) -> System.err.println("Jmxfile not found"))
				.onFailure((ex) -> System.out.println("We Could Handle Errors At This Pipe line "))
				.onSuccess((thePort) -> System.out.println("We Could Handle Success At This Pipe line ")).getOrElse(-1);

		assertNotEquals(-1, port);
	}

	@Test
	void testExtractPort2() {
		IPk2File file = new Pk2File(gamePath + "\\Media.pk2");

		assertThrows(NoSuchElementException.class, () -> {
			Try.of(() -> {
				JMXFile jmx = file.findFirst("(?i)gateportt.txt").get();
				String thePort = IOUtils.toString(getInputStream(jmx), "UTF-8");
				return Integer.parseInt(thePort.trim());
			}).onFailure(NoSuchElementException.class, (ex) -> System.err.println("Jmxfile not found"))
					.onSuccess((thePort) -> System.out.println("We Could Handle Success At This Pipe line ")).get();
		});

	}

	@Test
	void testExtractPort3() {

		IPk2File file = new Pk2File(gamePath + "\\Media.pk2");

		assertThrows(IOException.class, () -> {

			Try.of(() -> file.findFirst("(?i)gateports.txt").orElseThrow(() -> new IOException())).get();
		});

	}

	@Test
	void testExtractPort4() {
		IPk2File file = new Pk2File(gamePath + "\\Media.pk2");

		Integer port = Try.of(() -> {
			JMXFile jmx = file.findFirst("(?i)gateportt.txt").get();
			String thePort = IOUtils.toString(getInputStream(jmx), "UTF-8");
			return Integer.parseInt(thePort.trim());
		}).recover(NoSuchElementException.class, (e) -> -1)
				.onFailure(NoSuchElementException.class, (ex) -> System.err.println("Jmxfile not found"))
				.onSuccess((thePort) -> System.out.println("We Could Handle Success At This Pipe line "))
				.get();
		assertEquals(port, -1);
	}
 
	@Test
	void testTryMap() { 
		IPk2File file = new Pk2File(gamePath + "\\Media.pk2");
		
		Integer port = Try.of(()->file.findFirst("(?i)gateport.txt").get())
				.mapTry((jmx)->Integer.parseInt(IOUtils.toString(getInputStream(jmx) , "UTF-8").trim()))
			//	.map(thePort->NumberUtils.isParsable(thePort) ? Integer.parseInt(thePort) : -1)
				.recover(NumberFormatException.class , (ex)->{
					System.err.println("Invalid port value {}  ") ; 
					return -1 ; 
				})
				.recover(NoSuchElementException.class , (ex)->{
					System.err.println("JMXFile like (?i)gateport.txt does not exists") ; 
					return -1 ; 
				}).peek((p)->{
					System.out.println(p) ; 
				})
				.get() ; 
		assertNotEquals(-1, port);
	}
	
	@Test
	void testTryMap2() { 
		IPk2File fiel = new Pk2File(gamePath + "\\Media.pk2") ;
		
		
		
	}
	
	
	@Test
	void testRecoverMethod() { 
	 Integer r = 	Try.of(() -> 1/0).recover(Error.class, x -> Integer.MAX_VALUE).get();
	 assertEquals(r, Integer.MAX_VALUE);
	}
}
