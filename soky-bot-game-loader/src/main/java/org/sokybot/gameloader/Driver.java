package org.sokybot.gameloader;

import java.io.File;
import java.net.URISyntaxException;

import javax.management.InvalidApplicationException;

public class Driver {

	public String getShellPath() {
		return getClass().getClassLoader().getResource("shell.txt").getPath();

	}

	public static void main(String args[]) {

		Driver d = new Driver();

		File file = new File(".\\Patch.dll");

		System.out.println("Path Exsits : " + file.exists());

		file = new File(d.getShellPath());

		System.out.println("Shellcode Exsits : " + file.exists());

	}
}
