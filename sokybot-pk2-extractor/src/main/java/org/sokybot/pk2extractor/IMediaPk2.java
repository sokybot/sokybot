package org.sokybot.pk2extractor;

import java.io.Closeable;

import java.util.stream.Stream;

import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.item.ItemEntity;
import org.sokybot.domain.skill.SkillEntity;
import org.sokybot.pk2extractor.exception.Pk2InvalidResourceFormatException;

public interface IMediaPk2 extends Closeable {

	/**
	 * return object holds useful information like language , country . <br>
	 * These information resides into type.txt file
	 * 
	 * @throws Pk2MissedResourceException        if the corresponding joymax file(s)
	 *                                           which contains this information not
	 *                                           found.
	 * @throws Pk2InvalidResourceFormatException if the corresponding joymax file(s)
	 *                                           is found but the content has
	 *                                           unexpected format or empty
	 * @return SilkroadType object holds some properties of the game like language
	 *         and country etc.
	 */
	public SilkroadType extractType();

	/**
	 * extracts division information from media pk2 file , these information include
	 * local ,division name , host address .
	 * 
	 * <br>
	 * These information resides into divisioninfo.txt file
	 * 
	 * 
	 * @throws Pk2MissedResourceException        if the corresponding joymax file(s)
	 *                                           which contains this information not
	 *                                           found.
	 * @throws Pk2InvalidResourceFormatException if the corresponding joymax file(s)
	 *                                           is found but the content has
	 *                                           unexpected format or empty
	 */
	public DivisionInfo extractDivisionInfo();

	/**
	 * extract port used to connect to gateway server. <br>
	 * these information resides into 'gateport.txt' file
	 *
	 * @throws Pk2MissedResourceException        if the corresponding joymax file(s)
	 *                                           which contains this information not
	 *                                           found.
	 * @throws Pk2InvalidResourceFormatException if the corresponding joymax file(s)
	 *                                           is found but the content has
	 *                                           unexpected format or empty
	 * 
	 * @return the port to connect to gateway server
	 * 
	 * @see {@link #extractDivisionInfo()} for host information
	 */
	public int extractPort();

	/**
	 * extract last registered game version . <br>
	 * These information resides into SV.T file
	 * 
	 * @throws Pk2MissedResourceException        if the corresponding joymax file(s)
	 *                                           which contains this information not
	 *                                           found.
	 * @throws Pk2InvalidResourceFormatException if the corresponding joymax file(s)
	 *                                           is found but the content has
	 *                                           unexpected format or empty
	 * 
	 * @return last registered game version
	 */
	public int extractVersion();

	public Stream<SkillEntity> getSkillEntities();

	public Stream<ItemEntity> getItemEntities();

}
