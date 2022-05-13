/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.bot.pk2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.JdkConstants.PatternFlags;

import Test.primarydatatypes.BinaryReader;
import sokybot.bot.network.security.Blowfish;

/**
 *
 * @author AMROO
 */
public class Pk2Reader implements IPk2Reader {
	private FileChannel channel;
	private Blowfish blowfish;
	private BinaryReader reader;
	private static final long FIRST_BLOCK_POS = 256;

	public Pk2Reader(FileChannel channel, Blowfish blowfish) {
		this.blowfish = blowfish;
		this.channel = channel;
		reader = new BinaryReader();
	}

	@Override
	public void close() {
		try {
			this.channel.close();
		} catch (IOException ex) {
			// sokybot.logger.Logger.Log("Cannot Close Pk2Reader",
			// sokybot.logger.Logger.Message.ERROR);
		}
		this.channel = null;
		this.blowfish = null;
		this.reader = null;
	}

	@Override
	public List<JMXFile> find(String regex) {

		return find(regex, -1);
	}

	/*
	 * @Override public List<JMXFile> find(String regex, int limit) { return
	 * find(regex, FIRST_BLOCK_POS, limit); }
	 */

	@Override
	public List<JMXFile> find(String regex, int limit) {

		List<JMXFile> res = new ArrayList<>();
		if (!regex.isBlank()) {

			Stack<String> pathStack = getPathStack(regex);

			String fileName = pathStack.remove(0);

			if (pathStack.isEmpty()) {
				res = findFile(fileName, FIRST_BLOCK_POS, limit);
			} else {
				res = find(pathStack, fileName, limit);
			}

		}
		return res;

	}

	private List<JMXFile> find(Stack<String> pathStack, String regex, int limit) {

		List<JMXFile> res = new ArrayList<>();

		Stack<Long> posStack = new Stack<>();
		posStack.push(FIRST_BLOCK_POS);

		while (!pathStack.isEmpty()) {
			String targetDir = pathStack.pop();

			List<Pk2Entry> dirs = new ArrayList<>();

			while (!posStack.isEmpty()) {
				dirs.addAll(findDirEntry(posStack.pop(), targetDir));

			}
			dirs.forEach(pos -> posStack.push(pos.Position));

		}

		while (!posStack.isEmpty() && ((limit > 0 && res.size() < limit) || limit <= 0)) {

			res.addAll(findFile(regex, posStack.pop(), limit));

		}

		return res;

	}

	private List<Pk2Entry> findDirEntry(long pos, String regex) {

		EntryBlock block = readBlock(pos);
		Iterator ite = block.getIterator();
		List<Pk2Entry> res = new ArrayList<>();

		while (ite.hasNext()) {
			Pk2Entry entry = (Pk2Entry) ite.next();

			if (entry.Type == 1) {
				if (!entry.Name.contains(".") && !entry.Name.contains("..")) {

					if (Pattern.matches(regex, entry.Name)) {
						System.out.println("Matched Dir is : " + entry.Position + " " + entry.Name);
						res.add(entry);
					}

				}

			}
			// System.out.println("Next Chain : " +entry.NextChain);
		}
		long NextChain = block.getLastEntry().NextChain;
		if (NextChain > 0) {

			res.addAll(findDirEntry(NextChain, regex)); // getFile(NextChain, fileName);

		}

		return res;

	}

	private List<JMXFile> findFile(String regex, long pos, int limit) {
		List<JMXDirectory> jMXDirectories = new ArrayList<>();
		EntryBlock block = readBlock(pos);
		Iterator ite = block.getIterator();
		List<JMXFile> res = new ArrayList<>();

		while (ite.hasNext()) {
			Pk2Entry entry = (Pk2Entry) ite.next();
			switch (entry.Type) {
			case 0:
				break;
			case 1:

				if (!entry.Name.contains(".") && !entry.Name.contains("..")) {

					jMXDirectories.add(new JMXDirectory(entry));
				}
				break;
			case 2:

				if (Pattern.matches(regex, entry.Name)) {

					res.add(new JMXFile(entry));

				}

				if (limit > 0 && res.size() == limit)
					return res;

				break;

			}
			// System.out.println("Next Chain : " +entry.NextChain);
		}

		long nextChain = block.getLastEntry().NextChain;
		if (nextChain > 0) {

			List<JMXFile> nextFiles = findFile(regex, nextChain, limit - res.size());
			res.addAll(nextFiles);

		}

		for (JMXDirectory jMXDirectory : jMXDirectories) {

			List<JMXFile> children = findFile(regex, jMXDirectory.Position, limit - res.size());
			res.addAll(children);

		}

		return res;

	}
/*
	private Pk2Entry findDirEntry(long pos, Stack<String> path) {

		EntryBlock block = readBlock(pos);
		Iterator ite = block.getIterator();

		while (ite.hasNext()) {
			Pk2Entry entry = (Pk2Entry) ite.next();

			if (entry.Type == 1) {
				if (!entry.Name.contains(".") && !entry.Name.contains("..")) {

					if (path.peek().equalsIgnoreCase(entry.Name)) {
						path.pop();

						return (path.isEmpty()) ? entry : findDirEntry(entry.Position, path);

					}

				}

			}
			// System.out.println("Next Chain : " +entry.NextChain);
		}
		long NextChain = block.getLastEntry().NextChain;
		if (NextChain > 0) {

			Pk2Entry target = findDirEntry(NextChain, path); // getFile(NextChain, fileName);
			if (target != null) {
				return target;
			}
		}

		return null;
	}

	private List<JMXFile> findAt(String regex, long pos, int limit) {

		EntryBlock block = readBlock(pos);
		Iterator ite = block.getIterator();
		List<JMXFile> res = new ArrayList<>();

		while (ite.hasNext()) {
			Pk2Entry entry = (Pk2Entry) ite.next();

			if (entry.Type == 2) {

				if (Pattern.matches(regex, entry.Name)) {
					res.add(new JMXFile(entry));
					System.out.println("Matched : " + entry.Name);
					if (limit > 0 && res.size() == limit)
						return res;
				} else {
					System.out.println("Entry Name Not Match With Regex : " + entry.Name + " " + regex);
				}
			}

		}

		long nextChain = block.getLastEntry().NextChain;
		if (nextChain > 0) {

			List<JMXFile> nextFiles = findAt(regex, nextChain, limit - res.size());
			res.addAll(nextFiles);

		}

		return res;
	}
*/
	private Stack<String> getPathStack(String path) {

		Stack<String> pathStack = new Stack<>();
		Stack<String> nestedFlag = new Stack<>();

		String pathComp = "";
		char c;
		for (int i = path.length() - 1; i >= 0; i--) {
			c = path.charAt(i);
			switch (c) {
			case '\\':

				if (nestedFlag.isEmpty()) {
					if (!pathComp.isBlank()) {
						pathStack.push(pathComp);
						pathComp = "";
					}
				} else {
					pathComp = c + pathComp;
				}
				break;
			case ')':
				nestedFlag.push(c + "");
				pathComp = c + pathComp;
				break;
			case '(':
				nestedFlag.pop();
				pathComp = c + pathComp;
				break;
			default:
				pathComp = c + pathComp;
			}
		}
		if (!pathComp.isBlank()) {
			pathStack.push(pathComp);
		}

		return pathStack;

	}

	/*
	 * This method decrypt bytes if it is encrypted
	 * 
	 * 
	 */
	@Override
	public byte[] getFileBytes(JMXFile jMXFile) {
		byte[] res;

		ByteBuffer buffer = ByteBuffer.allocate(jMXFile.Size);
		try {
			this.channel.position(jMXFile.Position);
			this.channel.read(buffer);

		} catch (IOException ex) {
			// sokybot.logger.Logger.Log("Pk2Reader Cannot Reader JMXFile " + fileName ,
			// sokybot.logger.Logger.Message.ERROR);
		}
		buffer.flip();
		res = buffer.array();

		if (Pk2CryptoUtils.isEncrepted(res)) {

			res = Pk2CryptoUtils.decrypt(res);
		}
		return res;
	}

	private JMXFile getFile(long pos, String fileName) {
		List<JMXDirectory> jMXDirectories = new ArrayList<>();
		EntryBlock block = readBlock(pos);
		Iterator ite = block.getIterator();
		while (ite.hasNext()) {
			Pk2Entry entry = (Pk2Entry) ite.next();
			switch (entry.Type) {
			case 0:
				break;
			case 1:
				if (!entry.Name.contains(".") && !entry.Name.contains("..")) {

					jMXDirectories.add(new JMXDirectory(entry));
				}
				break;
			case 2:
				if (entry.Name.equalsIgnoreCase(fileName)) {
					return new JMXFile(entry);
				}
				break;
			}
		}
		long NextChain = block.getLastEntry().NextChain;
		if (NextChain > 0) {
			JMXFile target = getFile(NextChain, fileName);
			if (target != null) {
				return target;
			}
		}

		for (JMXDirectory jMXDirectory : jMXDirectories) {
			JMXFile target = getFile(jMXDirectory, fileName);
			if (target != null) {
				return target;

			}
		}
		return null;
	}

	private JMXFile getFile(JMXDirectory Parent, String fileName) {
		return getFile(Parent.Position, fileName);
	}

	@Override
	public byte[] getFileBytes(String fileName) { // need refactor to depend on find method
		byte[] res;
		JMXFile jMXFile = null;
		List<JMXFile> searchRes = find(fileName, 1);
		if (searchRes.size() == 0)
			return null;

		jMXFile = searchRes.get(0);

		ByteBuffer buffer = ByteBuffer.allocate(jMXFile.Size);
		try {
			this.channel.position(jMXFile.Position);
			this.channel.read(buffer);
		} catch (IOException ex) {
			// sokybot.logger.Logger.Log("Pk2Reader Cannot Reader JMXFile " + fileName ,
			// sokybot.logger.Logger.Message.ERROR);
		}
		buffer.flip();
		res = buffer.array();
		return res;
	}

	private Pk2Entry readEntry(long pos) {
		ByteBuffer buffer = ByteBuffer.allocate(128);
		Pk2Entry entry = new Pk2Entry();

		try {
			channel.position(pos);
			channel.read(buffer);
			byte[] Decode = buffer.array();
			blowfish.decode(0, Decode);
			buffer = ByteBuffer.wrap(Decode);

			reader.setBuffer(buffer);
			entry.Type = reader.getByte().get();
			entry.Name = reader.getSTR(81).get().trim();
			entry.AccessTime = reader.getQword();
			entry.CreateTime = reader.getQword();
			entry.ModifyTime = reader.getQword();
			entry.Position = reader.getQword().toLong();
			entry.Size = reader.getDword().toInteger();
			entry.NextChain = reader.getQword().toLong();
			entry.Padding = reader.getBytes(2);
		} catch (IOException ex) {
			// sokybot.logger.Logger.Log("Could not read a PK2Entry Object",
			// sokybot.logger.Logger.Message.ERROR);
		}
		return entry;
	}

	private EntryBlock readBlock(long pos) {
		Pk2Entry[] block = new Pk2Entry[20];
		for (int i = 0; i < 20; i++) {
			block[i] = readEntry(pos);
			pos += 128;
		}
		return new EntryBlock(block);
	}

}
