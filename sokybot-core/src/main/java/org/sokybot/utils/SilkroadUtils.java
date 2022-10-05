package org.sokybot.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.jdesktop.swingx.search.ListSearchable;


public class SilkroadUtils {

	/*
	public static  boolean isContainsFile(File directory, String fileName) {

		if (directory.isDirectory()) {
			File[] fileList;
			fileList = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return (name.equalsIgnoreCase(fileName));
				}
			});
			if (fileList != null) {
				for (File FileEntry : fileList) {
					if (!FileEntry.isDirectory()) {

						if (FileEntry.getName().equalsIgnoreCase(fileName)) {
							return true;
						}
						// res = fileName.equalsIgnoreCase(FileEntry.getName()) ;
					}
				}
			}

		}
		return false;
	}
*/
	
	public static  boolean isSilkraodDirectory(File directory) {
		
		
		Set<String> fileSet = new HashSet<>() ; 
		fileSet.add("sro_client.exe");
		fileSet.add("data.pk2");
		fileSet.add("map.pk2");
		fileSet.add("media.pk2");
		fileSet.add("music.pk2");
		fileSet.add("particles.pk2");
		fileSet.add("gfxfilemanager.dll");

		
		Stream.of(directory.list()).map(String::toLowerCase).forEach(fileSet::remove);
		
		
		return fileSet.isEmpty() ; 
		

	}


	public static  boolean isValidSilkroadDirectory(String path) {
		// this game distribution path is valid only if the passed path is extists and
		// is silkroad path
		return (Files.exists(Paths.get(path)) && isSilkraodDirectory(new File(path)));
	}

}
