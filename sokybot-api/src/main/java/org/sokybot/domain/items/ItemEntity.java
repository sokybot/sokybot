package org.sokybot.domain.items;

import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;
import org.sokybot.domain.SilkrodEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemEntity extends SilkrodEntity{


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
