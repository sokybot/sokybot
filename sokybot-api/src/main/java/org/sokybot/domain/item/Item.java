package org.sokybot.domain.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.Delegate;



@Data
public class Item {

	@Delegate
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private ItemEntity itemEntity ; 

	private byte slot ; 
	private int rentType ; 
	
	private int stackCount ; 
	private int attributeAssimilationProbability ; 
	
	@Delegate
	private ItemRent rent  ; 
	
	
	public Item(Item item ) { 
		this.itemEntity = item.itemEntity ; 
		this.slot = item.slot ; 
		this.rentType = item.rentType ; 
		this.stackCount = item.stackCount ; 
		this.attributeAssimilationProbability = item.attributeAssimilationProbability ; 
		this.rent = item.rent ; 
	}
	
	public Item(ItemEntity item) { 
		this.itemEntity = item ; 
	}
	
	
}
