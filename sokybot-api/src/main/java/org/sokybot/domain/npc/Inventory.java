package org.sokybot.domain.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sokybot.domain.item.Item;
import org.sokybot.domain.item.ItemEntity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Inventory {

	private byte itemInventorySize ; 
	private byte itemCount ; 
	
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Map<Byte , Item> items = new HashMap<>() ; 
	
	private byte avaterInventorySize ; 
	private byte avaterItemCount ; 
	
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Map<Byte , Item> avaterItems = new HashMap<>() ; 
	
	
	
	
	
	public void addItem(Item item ) { 
		
		byte itemSlot = item.getSlot() ; 
		
		if(itemSlot > itemInventorySize  ) 
			throw new IllegalArgumentException("ItemInventory  : invalid slot " +
		itemSlot   + " Where size "  + itemInventorySize) ; 
		items.put(itemSlot, item) ; 
	}
	
	public Optional<Item> getItemAt(byte slot) { 
		return Optional.ofNullable(items.get(slot)) ; 
	}
	
	public void removeItemAt(byte slot) { 
		this.items.remove(slot) ; 
	}

	public void addAvaterItem(Item item ) { 
		byte itemSlot = item.getSlot() ; 
		
		if(itemSlot > avaterInventorySize  ) 
			throw new IllegalArgumentException("AvaterInventory  : invalid slot " +
					itemSlot   + " Where size "  + avaterInventorySize) ; 
		avaterItems.put(itemSlot, item) ; 
	}
	
	public Optional<Item> getAvaterItemAt(byte slot) { 
		return Optional.ofNullable(avaterItems.get(slot)) ; 
	}
	
	public void removeAvaterItem(byte slot) { 
		this.avaterItems.remove(slot) ; 
	}

	public void clearItemInventory() { 
		this.items.clear(); 
	}

	public void clearAvaterInventory() { 
		this.avaterItems.clear();  
	}
	
	
}
