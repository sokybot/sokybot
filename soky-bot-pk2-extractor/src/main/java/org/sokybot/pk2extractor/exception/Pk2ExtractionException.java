package org.sokybot.pk2extractor.exception;
import lombok.Getter;



/**
 * 
 * TODO write document
 * @author Amr
 */

@Getter
public class Pk2ExtractionException extends Exception {

	private String resourceName ; 
	
     public Pk2ExtractionException(String message ,String resourceName ,  Throwable t) { 
    	 super(message , t) ; 	
    	 this.resourceName = resourceName  ; 
    	 	 
     }
     
     
	
	
}
