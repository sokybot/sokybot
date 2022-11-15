package org.sokybot.machine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sokybot.domain.item.ItemEntity;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Component
public class Inventory {

	private byte itemInventorySize ; 
	private byte itemCount ; 
	
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Map<Integer , ItemEntity> items = new HashMap<>() ; 
	
	private byte avaterInventorySize ; 
	private byte avaterItemCount ; 
	
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Map<Integer , ItemEntity> avaterItems = new HashMap<>() ; 
	
	
	
	
	
	public void setItem(int slot , ItemEntity item ) { 
		
		if(slot > itemInventorySize  ) 
			throw new IllegalArgumentException("ItemInventory  : invalid slot " +
		slot   + " Where size "  + itemInventorySize) ; 
		items.put(slot, item) ; 
	}
	
	public Optional<ItemEntity> getItem(int slot) { 
		return Optional.ofNullable(items.get(slot)) ; 
	}
	
	public void removeItem(int slot) { 
		this.items.remove(slot) ; 
	}

	public void setAvaterItem(int slot , ItemEntity item ) { 
		if(slot > avaterInventorySize  ) 
			throw new IllegalArgumentException("AvaterInventory  : invalid slot " +
		slot   + " Where size "  + avaterInventorySize) ; 
		avaterItems.put(slot, item) ; 
	}
	
	public Optional<ItemEntity> getAvaterItem(int slot) { 
		return Optional.ofNullable(avaterItems.get(slot)) ; 
	}
	
	public void removeAvaterItem(int slot) { 
		this.avaterItems.remove(slot) ; 
	}

	public void clearItemInventory() { 
		this.items.clear(); 
	}

	public void clearAvaterInventory() { 
		this.avaterItems.clear();  
	}
	
	
}
