
package org.sokybot.machine.model ; 

import java.util.List;

import org.sokybot.domain.item.ItemEntity;
import org.sokybot.domain.npc.CharacterEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 *
 * @author AMROO
 */
@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class SelectableCharacter extends CharacterEntity {

	private byte isDeleting;
	private int DeleteTime;
	private GUILD_MEMBER_CLASS GMC;
	private byte isGuildRenameRequired;
	private String currentGuildName;
	private ACADEMY_MEMBER_CLASS AMC;
	private byte itemCount;
	private List<ItemEntity> items;
	private byte avaterItemCount;
	private List<ItemEntity> avaterItems;



}
