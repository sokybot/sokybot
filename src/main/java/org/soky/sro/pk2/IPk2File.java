package org.soky.sro.pk2;

import javax.management.JMX;
import java.io.Closeable;
import java.util.List;
import java.util.Optional;


public interface IPk2File extends Closeable {
	//	public boolean open()  ;
	//public boolean isOpened();



	/*
	 * return specific file by a full path  ,
	 * this method is useful in case we read a file
	 * path from any other file and we want to map it
	 *
	 *
	 * return specific file by a full path
	 * or null if this file is not exists
	 *
	 */
	//public JMXFile getFile(String filePath);

	 /*
	  * perform deep scan for a file(s) that matches the given regex 
	  *  
	  * this method return file(s) with name matches input regex 
	  * 
	  * if not matches file(s) exists then this method return empty list 
	  * 
	  */

	  List<JMXFile> find(String regx) ;

	 /*
	  * 
	  * this method return file(s) with name matches input regex 
	  *  
	  *  the result list size will not exceed the input limit
	  *    
	  * if there not exists file names matches the regex  ,  this method will return empty list 
	  * 
	  */
	  List<JMXFile> find(String regx , int limit) ;

	  Optional<JMXFile> findFirst(String regex) ;
}
