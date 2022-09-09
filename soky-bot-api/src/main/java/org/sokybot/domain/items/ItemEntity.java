package org.sokybot.domain.items;

import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class ItemEntity {

	private int id;

	private String longId;
	private String name;
	private boolean isMallItem;
	private ItemType itemType;
	private Race race;

	private Gender gender;
	private boolean isSOX;
	private int level;
	private int degree;
	private int maxStacks;
	private boolean isSortable;



	
	
	

}
