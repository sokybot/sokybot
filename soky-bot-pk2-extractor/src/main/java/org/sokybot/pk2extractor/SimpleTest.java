package org.sokybot.pk2extractor;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.Functions;
import org.apache.commons.lang3.Streams.FailableStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.function.Failable;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;
import org.sokybot.pk2extractor.exception.Pk2InvalidResourceFormatException;
import org.sokybot.pk2extractor.exception.Pk2MissedResourceException;

import io.vavr.control.Try;

import static org.sokybot.pk2extractor.Pk2ExtractorUtils.*;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleTest {

	private static IPk2File file = IPk2File.open("E:\\Amroo\\Silkroad Games\\LegionSRO_15_08_2019\\Media.pk2");

	public static void main(String args[]) {
 
			try {
				test6() ;
			} catch (Pk2ExtractionException e) {
				System.out.println(e.getMessage()) ; 
			}
	
	}

	private static void test6() throws Pk2ExtractionException { 
	
	 JMXFile jmxFile = file.findFirst("itemdatas.txt")
		.orElseThrow(()->new Pk2MissedResourceException("Could not found itemdata.txt ", "itemdata.txt", null));
	
	   toBufferedReader(jmxFile)
		.lines()
		.flatMap((theFileName)->file.find("(?i)"+theFileName).stream());
	   
	}
	private static void test5() throws Pk2ExtractionException { 
		
		Failable.apply((jmx)->{
			System.out.println(jmx) ; 
			return jmx.getName() ; 
		}, file.findFirst("itemdatas.txt")
				.orElseThrow(()->new Pk2MissedResourceException("Could not found itemdata.txt ", "itemdata.txt", null)));
		
	}
	private static void test4() throws Pk2ExtractionException { 
		 
		Try.of(()->{
			
		JMXFile jmx = 	file.findFirst("itemdata.txt")
			.orElseThrow(()->new Pk2MissedResourceException("Could not found itemdata.txt ", "itemdata.txt", null)) ; 
			
			return	toBufferedReader(jmx)
					.lines()
					.flatMap((theFileName)->file.find("(?i)"+theFileName).stream());
		})
		.get().forEach((jmx)->{
			System.out.println(jmx) ; 
		});  ; 
		
	}
	private static void test3() throws Pk2ExtractionException { 
		
		        

		      file.find("itemdata.txt" , 1)
			 .stream()
			 .map((jmx)->toBufferedReader(jmx))
			 .map(BufferedReader::lines) 
			 .flatMap((theFileName)-> { 
				 System.out.println("The File Name To Query : " + theFileName) ; 
				 return file.find("(?i)" + theFileName).stream();
			 })
			 
			 .forEach((jmxFile)->{
				 System.out.println(jmxFile) ; 
			 });
			 
			  
			 
	 
	
		
		
	}
	private static void test2() throws Pk2InvalidResourceFormatException {

		file.find("itemdata.txt", 1).stream()
		.map(Pk2ExtractorUtils::toBufferedReader)
		.flatMap(BufferedReader::lines)

				.map((line) -> 
					
						 {
							try {
								return file.findFirst(line)
										.orElseThrow(()->new Pk2ExtractionException(null, null, null));
							} catch (Pk2ExtractionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return null; 
							}
						}
				
				)
				.forEach((s) -> System.out.println(s));

	}

	private static void testItemdata1() {
		file.findFirst("itemdata.txt")

				.ifPresent((jmx) -> {

					try {
						String theItemdataFile = new String(toByteArray(jmx), Charsets.UTF_16LE);
						Pk2ExtractorUtils.toString(jmx);
						System.out.println(theItemdataFile);
					} catch (Pk2InvalidResourceFormatException ex) {
						System.err.println(ex.getMessage());
					}
				});
	}

	private void test() throws Pk2ExtractionException {

		Try.of(() -> {

			return IPk2File.open(null).findFirst("itemdata.txt")

					.map((jmx) -> {
						return new String();
					}).orElseThrow();

		});

		IPk2File.open("").findFirst("itemdata.txt").ifPresentOrElse((jmx) -> {
			// toLines(jmx) ;
		}, () -> {

		});
		// .map((itemDataFile)->toLines(itemDataFile))
		// .orElseThrow(()->new Pk2MissedResourceException(null, null, null)) ;
	}

}
