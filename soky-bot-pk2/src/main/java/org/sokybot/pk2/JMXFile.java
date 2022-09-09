/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.pk2;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author AMROO
 */
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder( access = AccessLevel.PACKAGE )
@Slf4j
public class JMXFile {
    
	
	@NonNull
    private final String pkFilePath;
	
	
	@NonNull
    private final String name;
	
    private final long position;
    private final int size;

    private final JMXDirectory parent = null;

   
    

    /*
     * This method decrypt bytes if it is encrypted
     *
     *
     */

    public  byte[] getFileBytes() {

        // this is old code used into Pk2Read Object ...
        // to do.. refactor it
        // byte[] res;

//		ByteBuffer buffer = ByteBuffer.allocate(jMXFile.Size);
//		try {
//			this.channel.position(jMXFile.Position);
//			this.channel.read(buffer);

//		} catch (IOException ex) {
        // sokybot.logger.Logger.Log("Pk2Reader Cannot Reader JMXFile " + fileName ,
        // sokybot.logger.Logger.Message.ERROR);
//		}
//		buffer.flip();
//		res = buffer.array();

//		if (Pk2CryptoUtils.isEncrepted(res)) {

//			res = Pk2CryptoUtils.decrypt(res);
//		}
//		return res;

        return null;
    }

}