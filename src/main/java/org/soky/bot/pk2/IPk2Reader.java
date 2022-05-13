package org.soky.bot.pk2;

import java.util.List;

public interface IPk2Reader {

	 public void close() ; 
	 
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
	 public List<JMXFile> find(String regx) ; 

	 /*
	  * 
	  * this method return file(s) with name matches input regex 
	  *  
	  *  the result list size will not exceed the input limit
	  *    
	  * if there not exists file names matches the regex  ,  this method will return empty list 
	  * 
	  */
	 public List<JMXFile> find(String regx , int limit) ; 
	 
	 /*
	  *
	  * 
	  * perform deep scan for a file(s) that matches the given regex 
	  * and exists into a specified directory into pk2 file 
	  * 
	  * 
	  * represents the file path  ,
	  * path may consists of regex itself  
	  * note , any regex back slash must enclosed into parentheses
	  * exmple ,  find("\\icon(\\d*)\\item\\etc","archemy_astral.ddj$" , 3  ) ; 
	  * this will find at most 3 files at "icon or icon_64 \item\etc "
	  *   
	  */
	// public List<JMXFile> find(String path , String regex) ; 
	 //public List<JMXFile> find(String path ,String regex , int limit) ; 
	 
	
	 public byte [] getFileBytes(JMXFile jMXFile) ; 
	 public byte [] getFileBytes(String fileName);
}
