/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.bot.pk2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import Test.primarydatatypes.BinaryReader;
import sokybot.bot.network.security.Blowfish;

/**
 *
 * @author AMROO
 */
public class Pk2File implements IPk2File {
	private IPk2Reader reader = null;
	private RandomAccessFile file;
	private String filePath;
	private boolean isOpened = false;

	public Pk2File(String filePath) {
		try {
			this.filePath = filePath;
			this.file = new RandomAccessFile(filePath, "r");
			
		
		} catch (FileNotFoundException ex) {
			//sokybot.logger.Logger.Log("Cannot found file " + filePath, sokybot.logger.Logger.Message.ERROR);
			System.out.println("Cannot found file " + filePath) ; 
		}

	}

	@Override
	public boolean open() {
		FileChannel channel = this.file.getChannel();

		Blowfish blowfish = new Blowfish();
		byte[] key = new byte[] { (byte) 0x32, (byte) 0xCE, (byte) 0xDD, (byte) 0x7C, (byte) 0xBC, (byte) 0xA8 };
		blowfish.setWorkingKey(key);
		try {
			channel.position(0);
		} catch (IOException e) {
			//sokybot.logger.Logger.Log("Cannot Open Pk2 file  : " + this.filePath ,
				//sokybot.logger.Logger.Message.ERROR);
		}
		Pk2Header header = readHeader(channel);

		if (!Arrays.equals(Arrays.copyOf(blowfish.encode(0 , "Joymax Pak JMXFile".getBytes()), 3),
				Arrays.copyOf(header.Verify, 3))) {

			//sokybot.logger.Logger.Log("cannot read " + filePath + "  , Invalid Blowfish Key !!",
				//	sokybot.logger.Logger.Message.ERROR);
		  System.out.println("Cannot read " + filePath + " , Invalid Blowfish key !!") ; 
			return false;
		}
		if (!isValidName(header)) {
			
			System.out.println("Invalid Pk2 JMXFile Name !!");
			//sokybot.logger.Logger.Log("Invalid Pk2 JMXFile Name !!", sokybot.logger.Logger.Message.ERROR);
			return false;
		}
		if (!isValidVer(header)) {
			
			//sokybot.logger.Logger.Log("Invalid Pk2 JMXFile Name !!", sokybot.logger.Logger.Message.ERROR);
			return false;
		}
		this.reader = new Pk2Reader(channel, blowfish);
		this.isOpened = true;
		return true;
	}

	@Override
	public boolean isOpened() {
		return this.isOpened;
	}

	@Override
	public IPk2Reader getReader() {
		return this.reader;
	}

	private boolean isValidName(Pk2Header header) {
		return (header.Name.trim().equalsIgnoreCase("JoyMax File Manager!"));
	}

	private boolean isValidVer(Pk2Header header) {
		return (header.Version == 16777218);
	}

	private Pk2Header readHeader(FileChannel channel) {
		Pk2Header res = new Pk2Header();
		ByteBuffer buffer = ByteBuffer.allocate(256);
		try {
			BinaryReader Breader = new BinaryReader(buffer);
			channel.read(buffer);

			Breader.setBuffer(buffer);

			res.Name = Breader.getSTR(30).get();
			res.Version = Breader.getDword().toInteger();
			res.Encryption = Breader.getByte().get();
			res.Verify = Breader.getBytes(16);

		} catch (IOException ex) {
			//sokybot.logger.Logger.Log("Pk2 Reader Cannot Read From " + this.filePath,
				//	sokybot.logger.Logger.Message.ERROR);
		}
		return res;
	}

	@Override
	public void close() {
		this.reader.close();
		this.file = null;
		this.filePath = null;
		this.isOpened = false;
	}

	
}
