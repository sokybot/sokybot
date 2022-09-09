package org.sokybot.nitriteexamples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.dizitart.no2.Document;
import org.dizitart.no2.IndexOptions;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mapper.JacksonFacade;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.meta.Attributes;
import org.dizitart.no2.objects.ObjectRepository;

import org.sokybot.domain.items.ItemEntity;

import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class ObjectRepoMultiRef {

	private static Nitrite nitrite;

	static {

		try {
			Files.delete(Paths.get(System.getProperty("user.dir") + "\\test.db"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		nitrite = Nitrite.builder()
				.nitriteMapper(new JacksonMapper() { 
				 public <T extends Object> org.dizitart.no2.Document asDocument(T object) {
					 
					 Document doc  =    super.asDocument(object) ; 
					    
					 System.out.println("When Trying to map object to nitrite document the result is ") ; 
					  
					 doc.forEach((k , v)->{
						System.out.println(k + ":" + v) ;  
					 });
					 
					 return doc ; 
				 };	
				})
				.registerModule(null)
				
				.filePath(System.getProperty("user.dir") + "\\test.db").openOrCreate();

		
	}

	public static void main(String args[]) {
 
		
	ObjectRepository<ItemEntity> objectRepo = 	nitrite.getRepository("amr", ItemEntity.class) ; 
	
	//objectRepo.createIndex("id", IndexOptions.indexOptions(IndexType.Unique , true));	
	
	update(objectRepo);
	
	
	//ObjectRepository<ItemEntity> objectRepo2 = 	nitrite.getRepository("amr", ItemEntity.class) ; 
	//objectRepo.createIndex("id", IndexOptions.indexOptions(IndexType.Unique , true));	
	
	//ItemEntity item = firstOrDefault(objectRepo2) ; 
	
	//System.out.println(item) ; 
 		 
	}
	
	
	static ItemEntity firstOrDefault(ObjectRepository<ItemEntity> objectRepo) {
		return objectRepo.find().firstOrDefault() ; 
	}
	
	static void update(ObjectRepository<ItemEntity> objectRepo) { 
		ItemEntity item = new ItemEntity() ; 
	      item.setId(2);
	      item.setName("item one 2");
	      
	      objectRepo.update(item , true) ; 
	      
		//objectRepo.update(item , true) ; 
		
	}
}
