package org.sokybot.domain.item;

import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;
import org.sokybot.domain.SilkrodEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ItemEntity extends SilkrodEntity {

	private boolean isMallItem;
	private ItemType itemType;
	private Race race;

	private Gender gender;
	private boolean isSOX;
	private int level;
	private int degree;
	private int maxStacks;
	private boolean isSortable;

	public boolean isConsumable() {
		return (this.itemType.getValue() >> 8) == 3;
	}

	public boolean isEquipmentItem() {

		return (this.itemType.getValue() >> 8) == 1;
	}

	public boolean isWeapon() {
		return (this.itemType.getValue() & 0x160) == 0x160;
	}

	public boolean isShield() {
		return (this.itemType.getValue() & 0x140) == 0x140;
	}

	public boolean IsClothing() {
		int value = this.itemType.getValue();
		return isEquipmentItem() && !((value & 0x140) == 0x140 || (value & 0x150) == 0x150 || (value & 0x160) == 0x160
				|| (value & 0x1C0) == 0x1C0);
	}

	public boolean isAccessory() {
		return (itemType.getValue() & 0x150) == 0x150 || (this.itemType.getValue() & 0x1C0) == 0x1C0;
	}

}
