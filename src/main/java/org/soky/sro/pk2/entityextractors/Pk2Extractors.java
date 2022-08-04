package org.soky.sro.pk2.entityextractors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.reflect.TypeUtils;



public class Pk2Extractors {



	public static <T> IPK2EntityExtractor<T > getExtractorForEntity(Class<T> entity) {
		String pack = Pk2Extractors.class.getPackage().getName() ; //

		return
				new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(pack)))
						.lines()
						.filter((line)->line.endsWith(".class"))
						.map((line)-> {
							try {
								return Class.forName(pack + "." + line.substring(0 , line.lastIndexOf(".")));
							} catch (ClassNotFoundException e) {
								throw new RuntimeException(e);
							}
						})
						.filter(c-> TypeUtils.isAssignable(c , IPK2EntityExtractor.class))
						.filter((c)-> TypeUtils.getTypeArguments(c , IPK2EntityExtractor.class)
								.values().stream().anyMatch(type ->TypeUtils.equals(type , entity) ))
						.findFirst()
						.map((clazz)->{
							try {
								//  return IEntityExtractor.class.cast(clazz.getConstructor().newInstance()) ;
								System.out.println("Founded Class : " + clazz.getName()) ;
								return (IPK2EntityExtractor<T>)clazz.getConstructor().newInstance()  ;
							} catch (InstantiationException e) {
								throw new RuntimeException(e);
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							} catch (InvocationTargetException e) {
								throw new RuntimeException(e);
							} catch (NoSuchMethodException e) {
								throw new RuntimeException(e);
							}
						})
						.orElseThrow(()->new NoSuchElementException("No Entity Extractor for " + entity.getName())) ;

	}

}
