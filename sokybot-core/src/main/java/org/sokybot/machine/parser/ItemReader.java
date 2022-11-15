package org.sokybot.machine.parser;

import org.sokybot.domain.item.Equipment;
import org.sokybot.domain.item.Item;
import org.sokybot.domain.item.ItemRent;
import org.sokybot.domain.item.MagParamType;
import org.sokybot.domain.item.WhiteAttribute;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.service.IGameDAO;

public class ItemReader {

	private IStreamReader reader;
	private IGameDAO gameDao;

	protected ItemReader(IStreamReader reader, IGameDAO gameDao) {
		this.reader = reader;
		this.gameDao = gameDao;
	}

	public Item readItem() {

		byte itemSlot = reader.getByte();
		int rentType = reader.getInt();

		ItemRent rent = new ItemRent();

		switch (rentType) {
		case 1:
			rent.setCanDelete(reader.getShort());
			rent.setPeriodBeginTime(reader.getInt());
			rent.setPeriodEndTime(reader.getInt());
			break;
		case 2:
			rent.setCanDelete(reader.getShort());
			rent.setCanRecharge(reader.getShort());
			rent.setMeterRateTime(reader.getInt());

			break;
		case 3:
			rent.setCanDelete(reader.getShort());
			rent.setPeriodBeginTime(reader.getInt());
			rent.setPeriodEndTime(reader.getInt());
			rent.setCanRecharge(reader.getShort());
			rent.setPackingTime(reader.getInt());

			break;

		}

		int refId = reader.getInt();

		Item item = new Item(this.gameDao.getItem(refId));
		item.setSlot(itemSlot);
		item.setRentType(rentType);
		item.setRent(rent);

		if (item.isEquipmentItem()) {
			Equipment equipment = new Equipment(item);
			equipment.setOptLvl(reader.getByte());
			equipment.setAttributeValue(reader.getLong());
			equipment.setDurability(reader.getInt());

			byte magParamNumber = reader.getByte();
			
			equipment.setMagParamNum(magParamNumber);
			for (int i = 0; i < magParamNumber; i++) {
				MagParamType type = MagParamType.of(reader.getInt());
				equipment.setMagParamValue(type, magParamNumber);
				
			}
			
			
		}

		return item;
	}
}
