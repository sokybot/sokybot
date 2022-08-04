package org.soky.sro.pk2.entityextractors;


import java.util.stream.Stream;

import org.sokybot.pk2.IPk2File;

public interface IPK2EntityExtractor<T> {

	
	Stream<T> extract(IPk2File pk2File) ;
	
}
