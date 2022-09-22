package org.sokybot.nitriteexamples;

import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

public class CreateDocExample {
	
	private static Nitrite nitrite ; 
	
	static { 
		
		nitrite = Nitrite.builder().filePath(System.getProperty("user.dir") + "\\test.db").openOrCreate() ; 
		
	}
	
	
	public static void main(String args[]) { 
		
		 
		
		Document idoc = Document.createDocument("name", "test").put("path", "mygroup-path") ; 
		
		collection().update(idoc, true) ; 
		
	 collection().find().forEach((doc)->{
		 doc.forEach((kv)->{
			 System.out.println(kv.getKey() + " : " + kv.getValue()) ; 
		 });	 
	 });
	 
	}
	
	private static NitriteCollection collection() { 
		return nitrite.getCollection("bot-machine-groups") ; 
	}
	

}
