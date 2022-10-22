package org.sokybot.gameloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sokybot.service.IGameLoader;
import org.sokybot.utils.SilkroadUtils;

public class GameLoader implements IGameLoader {

	private IProcessLoader processLoader;

	public GameLoader() {

		this.processLoader = ProcessLoader.createInstance();
	}

	@Override
	public long launch(String clientPath) {

		return launch(clientPath, "0 /" + 22 + " 0 0");

	}

	@Override
	public long launch(String clientPath, String command) {

		if (!Files.exists(Paths.get(clientPath)) || !clientPath.endsWith(".exe")) {
			throw new IllegalArgumentException("Invalid client path");
		}

		String dllRef = System.getProperty("user.dir").concat("\\").concat("sokybotpatch.dll");


		Path dllPath = Paths.get(dllRef);

		if (!Files.exists(dllPath)) {
			try {
				Files.copy(getClass().getClassLoader().getResourceAsStream(".\\sokybotpatch.dll"), dllPath);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		
		String shellRef = System.getProperty("user.dir").concat("\\").concat("shell.txt") ; 
		Path shellPath = Paths.get(shellRef) ; 
		
		if(!Files.exists(shellPath)) { 
			try {
				Files.copy(getClass().getClassLoader().getResourceAsStream(".\\shell.txt"), shellPath);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
				

		return this.processLoader.loadProcessImage(clientPath, command, dllRef, shellRef);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer minimumVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer maximumVersion() {
		// TODO Auto-generated method stub
		return null;
	}
}
