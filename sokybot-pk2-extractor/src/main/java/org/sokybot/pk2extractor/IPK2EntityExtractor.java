package org.sokybot.pk2extractor;


import java.util.stream.Stream;

import org.sokybot.pk2.IPk2File;


/*
 * 
 * unused interface 
 * TODO : remove it
 */
public interface IPK2EntityExtractor<T> {

	
	Stream<T> extract(IPk2File pk2File)  ;
	
}
