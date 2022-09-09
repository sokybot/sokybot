package org.sokybot.pk2extractor;


import java.util.stream.Stream;

import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2extractor.exception.Pk2ExtractionException;

public interface IPK2EntityExtractor<T> {

	
	Stream<T> extract(IPk2File pk2File) throws Pk2ExtractionException ;
	
}
