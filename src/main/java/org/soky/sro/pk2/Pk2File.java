/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soky.sro.pk2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.soky.sro.security.Blowfish;

import static org.soky.sro.pk2.Constants.*;

/**
 * @author AMROO
 */
@Slf4j
public class Pk2File implements IPk2File {


    private FileChannel channel;

    // private RandomAccessFile file;
    private final String filePath;
    private boolean isOpened = false;

    private Blowfish blowfish;

    public Pk2File(String pk2FilePath) {
        this.filePath = pk2FilePath;
    }


    // need to optimization
    private void openIfClosed() {
        if (this.channel == null || !this.channel.isOpen()) {
            try {
                this.channel = FileChannel.open(Paths.get(this.filePath), StandardOpenOption.READ);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }

            this.blowfish = new Blowfish();

            blowfish.setWorkingKey(HEADER_VALIDATION_KEY);
            try {
                channel.position(0);
            } catch (IOException e) {
                log.error("Could not  Open Pk2 file {}  ", this.filePath);
            }

            Pk2Header header = readHeader(channel);

            if (!Arrays.equals(Arrays.copyOf(blowfish.encode(0, HEADER_VALIDATION_STATEMENT.getBytes()), 3),
                    Arrays.copyOf(header.verify, 3))) {
                log.error("cannot read {} , Invalid Blowfish Key !!" , this.filePath);
                return;
            }

            if (!isValidName(header)) {
                log.error("Invalid Pk2 JMXFile Name !!");
                return;
            }

            if (!isValidVer(header)) {
                log.error("Invalid Pk2 JMXFile Version !!");
                return;
            }
            this.isOpened = true;
        }
    }





    @Override
    public Optional<JMXFile> findFirst(String regex) {
        return find(regex , 1).stream().findFirst();
    }

    @Override
    public List<JMXFile> find(String regex) {

        return find(regex, -1);
    }

    @Override
    public List<JMXFile> find(String regex, int limit) {

        List<JMXFile> res = new ArrayList<>();


        openIfClosed() ;
      //  log.debug("Channel Opened with file {} and arguments verified" , this.filePath);
        if (!StringUtils.isBlank(regex)) {

            Stack<String> pathStack = getPathStack(regex);

            String fileName = pathStack.remove(0);
        //    log.debug("File name to query {} " , fileName);
            if (pathStack.isEmpty()) {
                res = findFile(fileName, FIRST_BLOCK_POS, limit);
          //      log.debug("While find file , result is {}" , res);
            } else {
                res = find(pathStack, fileName, limit);
            }

        }
        return res;

    }

    private List<JMXFile> findFile(String regex, long pos, int limit) {
        List<JMXFile> res = new ArrayList<>();

        if(limit == 0) return res;

        List<JMXDirectory> jMXDirectories = new ArrayList<>();
        EntryBlock block = readBlock(pos);
        Iterator<Pk2Entry> ite = block.getIterator();


        //log.debug("Finding file {} , At pos {}  , With limit {}" , regex , pos , limit) ;

       // log.debug("EntryBlock Length : " +
         //       block.block.length);

        while (ite.hasNext()) {
            Pk2Entry entry = ite.next();
            switch (entry.type) {
                case 0:
                    break;
                case 1:

                    if (!entry.name.contains(".") && !entry.name.contains("..")) {

                        jMXDirectories.add(new JMXDirectory(entry.name, entry.position));
                    }
                    break;
                case 2:
                    //log.debug("Determine File Entry {} , Regex {} " , entry.name, regex);
                    if (Pattern.matches(regex, entry.name)) {
                       // log.debug("File Entry {} Matches Regex {} "  , entry.name, regex);
                        res.add(
                                JMXFile.builder()
                                        .pkFilePath(this.filePath)
                                        .name(entry.name)
                                        .position(entry.position).size(entry.size).build());

                    }

                    if (limit > 0 && res.size() == limit) {
             //           log.debug("Query limit reached");
                        return res;
                    }

                    break;

            }
            // System.out.println("Next Chain : " +entry.NextChain);
        }

        long nextChain = block.getLastEntry().nextChain;

        if (nextChain > 0) {
       // log.info("continue at next chain {} " , nextChain);
            List<JMXFile> nextFiles = findFile(regex, nextChain, limit - res.size());
            res.addAll(nextFiles);

            if (limit > 0 && res.size() == limit) {
                log.debug("Query limit reached");
                return res;
            }

        }

        for (JMXDirectory jMXDirectory : jMXDirectories) {

            List<JMXFile> children = findFile(regex, jMXDirectory.getPosition(), limit - res.size());
            res.addAll(children);
            if (limit > 0 && res.size() == limit) {
                log.debug("Query limit reached");
                return res;
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
            dirs.forEach(pos -> posStack.push(pos.position));

        }

        //(limit > 0 && res.size() < limit) || limit <= 0)
        while (!posStack.isEmpty() && (limit <= 0 || res.size() < limit)) {

            res.addAll(findFile(regex, posStack.pop(), limit));

        }

        return res;

    }

    private List<Pk2Entry> findDirEntry(long pos, String regex) {

        EntryBlock block = readBlock(pos);
        Iterator<Pk2Entry> ite = block.getIterator();
        List<Pk2Entry> res = new ArrayList<>();

        while (ite.hasNext()) {
            Pk2Entry entry =  ite.next();

            if (entry.type == 1) {
                if (!entry.name.contains(".") && !entry.name.contains("..")) {

                    if (Pattern.matches(regex, entry.name)) {
                        System.out.println("Matched Dir is : " + entry.position + " " + entry.name);
                        res.add(entry);
                    }

                }

            }
            // System.out.println("Next Chain : " +entry.nextChain);
        }
        long nextChain = block.getLastEntry().nextChain;
        if (nextChain > 0) {

            res.addAll(findDirEntry(nextChain, regex)); // getFile(nextChain, fileName);

        }

        return res;

    }

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

                        if (!StringUtils.isBlank(pathComp)) {
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
        if (!StringUtils.isBlank(pathComp)) {
            pathStack.push(pathComp);
        }

        return pathStack;

    }

    private boolean isValidName(Pk2Header header) {
        return (header.name.trim().equalsIgnoreCase("JoyMax File Manager!"));
    }

    private boolean isValidVer(Pk2Header header) {
        return (header.version == 16777218);
    }

    private Pk2Header readHeader(FileChannel pk2FileChannel) {
        Pk2Header res = new Pk2Header();
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try {

           int totalReadBytes =  pk2FileChannel.read(buffer);
            log.info("While reading header , total read bytes {} " , totalReadBytes);
            buffer.position(0) ;
            byte[] nameBuffer = new byte[30];
            buffer.get(nameBuffer);
            res.name = new String(nameBuffer);
            res.version = buffer.getInt();
            res.encrypted = buffer.get();

            res.verify = new byte[16];
            buffer.get(res.verify);

        } catch (IOException ex) {
           log.error("Pk2 Reader Cannot Read From {}" , this.filePath);

        }
        return res;

    }

    private JMXFile getFile(long pos, String fileName) {
        List<JMXDirectory> jMXDirectories = new ArrayList<>();
        EntryBlock block = readBlock(pos);
        Iterator<Pk2Entry> ite = block.getIterator();
        while (ite.hasNext()) {
            Pk2Entry entry = ite.next();
            switch (entry.type) {
                case 0:
                    break;
                case 1:
                    if (!entry.name.contains(".") && !entry.name.contains("..")) {

                        jMXDirectories.add(new JMXDirectory(entry.name, entry.position));
                    }
                    break;
                case 2:
                    if (entry.name.equalsIgnoreCase(fileName)) {
                        return JMXFile.builder()
                                .name(entry.name)
                                .position(entry.position).size(entry.size).build();
                    }
                    break;
            }
        }
        long nextChain = block.getLastEntry().nextChain;
        if (nextChain > 0) {
            JMXFile target = getFile(nextChain, fileName);
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

    private JMXFile getFile(JMXDirectory parent, String fileName) {
        return getFile(parent.getPosition(), fileName);
    }


    //		this.channel = null;
//		this.blowfish = null;
//		this.reader = null;


    @Override
    public void close() throws IOException {
        if(this.isOpened) {
            this.channel.close();
            this.channel = null;
            this.isOpened = false;
        }
    }

    private EntryBlock readBlock(long pos) {
        long startLocation = pos ;

        Pk2Entry[] block = new Pk2Entry[20];
        for (int i = 0; i < 20; i++) {
            block[i] = readEntry(startLocation);
            startLocation += 128;
        }
        return new EntryBlock(block);
    }

    private Pk2Entry readEntry(long pos) {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        Pk2Entry entry = new Pk2Entry();

        try {
            channel.position(pos);
            channel.read(buffer);
            byte[] decode = buffer.array();
            blowfish.decode(0, decode);
            buffer = ByteBuffer.wrap(decode);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // reader.setBuffer(buffer);

            entry.type = buffer.get();

            byte[] nameBuffer = new byte[81];
            buffer.get(nameBuffer);
            entry.name = new String(nameBuffer).trim();

            entry.accessTime = buffer.getLong();
            entry.createTime = buffer.getLong();
            entry.modifyTime = buffer.getLong();
            entry.position = buffer.getLong();
            entry.size = buffer.getInt();
            entry.nextChain = buffer.getLong();

            entry.padding = new byte[2];
            buffer.get(entry.padding);

        } catch (IOException ex) {
            // sokybot.logger.Logger.Log("Could not read a PK2Entry Object",
            // sokybot.logger.Logger.Message.ERROR);
            log.error(ex.getMessage());
        }
        return entry;

    }


    private class Pk2Header {
        private String name;
        private int version;
        private byte encrypted;
        private byte[] verify;
        private byte[] reserved;
    }


    private class Pk2Entry {
        private byte type; // 1 JMXDirectory , 2 JMXFile , 0 null
        private String name;
        private long accessTime, createTime, modifyTime;
        private long position, nextChain;
        private int size;
        private byte[] padding;
    }

    private class EntryBlock {
        private final Pk2Entry[] block;

         EntryBlock(Pk2Entry[] entries) {
            this.block = entries;
        }

        public Pk2Entry getLastEntry() {
            return block[19];
        }


        public Iterator<Pk2Entry> getIterator() {

            return Arrays.asList(block).iterator();
        }
    }

    /*
     * @Override public List<JMXFile> find(String regex, int limit) { return
     * find(regex, FIRST_BLOCK_POS, limit); }
     */

    /*
     * private Pk2Entry findDirEntry(long pos, Stack<String> path) {
     *
     * EntryBlock block = readBlock(pos); Iterator ite = block.getIterator();
     *
     * while (ite.hasNext()) { Pk2Entry entry = (Pk2Entry) ite.next();
     *
     * if (entry.Type == 1) { if (!entry.Name.contains(".") &&
     * !entry.Name.contains("..")) {
     *
     * if (path.peek().equalsIgnoreCase(entry.Name)) { path.pop();
     *
     * return (path.isEmpty()) ? entry : findDirEntry(entry.Position, path);
     *
     * }
     *
     * }
     *
     * } // System.out.println("Next Chain : " +entry.NextChain); } long NextChain =
     * block.getLastEntry().NextChain; if (NextChain > 0) {
     *
     * Pk2Entry target = findDirEntry(NextChain, path); // getFile(NextChain,
     * fileName); if (target != null) { return target; } }
     *
     * return null; }
     *
     * private List<JMXFile> findAt(String regex, long pos, int limit) {
     *
     * EntryBlock block = readBlock(pos); Iterator ite = block.getIterator();
     * List<JMXFile> res = new ArrayList<>();
     *
     * while (ite.hasNext()) { Pk2Entry entry = (Pk2Entry) ite.next();
     *
     * if (entry.Type == 2) {
     *
     * if (Pattern.matches(regex, entry.Name)) { res.add(new JMXFile(entry));
     * System.out.println("Matched : " + entry.Name); if (limit > 0 && res.size() ==
     * limit) return res; } else {
     * System.out.println("Entry Name Not Match With Regex : " + entry.Name + " " +
     * regex); } }
     *
     * }
     *
     * long nextChain = block.getLastEntry().NextChain; if (nextChain > 0) {
     *
     * List<JMXFile> nextFiles = findAt(regex, nextChain, limit - res.size());
     * res.addAll(nextFiles);
     *
     * }
     *
     * return res; }
     */

}
