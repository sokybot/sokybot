package org.sokybot.machine.parser;

import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;
import org.sokybot.domain.item.AbilityPet;
import org.sokybot.domain.item.Equipment;
import org.sokybot.domain.item.Item;
import org.sokybot.domain.item.ItemEntity;
import org.sokybot.domain.item.ItemExchangeCoupon;
import org.sokybot.domain.item.ItemRent;
import org.sokybot.domain.item.ItemType;
import org.sokybot.domain.item.MagParamType;
import org.sokybot.domain.item.MagicCube;
import org.sokybot.domain.item.PetScroll;
import org.sokybot.domain.item.PetStatus;
import org.sokybot.domain.item.WhiteAttribute;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.service.IGameDAO;

import picocli.CommandLine.IExitCodeExceptionMapper;

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

		// Item item = new Item(this.gameDao.getItem(refId));

		return  this.gameDao.findItem(refId).map((itemEntity)->{
			Item item = new Item(itemEntity);
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

				byte optType = reader.getByte(); // (1 => Socket)
				byte optCount = reader.getByte();

				for (int i = 0; i < optCount; i++) {
					equipment.setOptSlot(reader.getByte());
					equipment.setOptID(reader.getInt());
					equipment.setOptNParam1(reader.getInt());
				}

				optType = reader.getByte();
				optCount = reader.getByte();

				for (int i = 0; i < optCount; i++) {
					equipment.setOptSlot(reader.getByte());
					equipment.setOptID(reader.getInt());
					equipment.setOptValue(reader.getInt());
				}

				return equipment;

			} else if (item.isMagicStone() || item.isAttributeStone() || item.getItemType() == ItemType.MagicStoneMall) {

				item.setStackCount(reader.getShort());

				if (item.getItemType() != ItemType.MagicStoneMall && item.getItemType() != ItemType.MagicStoneSteady
						&& item.getItemType() != ItemType.MagicStoneLuck) {
					item.setAttributeAssimilationProbability(reader.getByte());
				}

				return item;

			} else if (item.getItemType() == ItemType.GrowthPet) {

				PetScroll pet = new PetScroll(item);
				PetStatus petStatus = PetStatus.of(reader.getByte());
				pet.setPetStatus(petStatus);

				if (petStatus != PetStatus.Unsummoned) {

					pet.setUniqueId(reader.getInt());
					pet.setPetName(new String(reader.getBytes(reader.getShort())));
					pet.setUnk02(reader.getByte());

				}

				return pet;

			} else if (item.getItemType() == ItemType.AbilityPet) {

				AbilityPet abilityPet = new AbilityPet(item);
				PetStatus petStatus = PetStatus.of(reader.getByte());
				abilityPet.setPetStatus(petStatus);

				if (petStatus != PetStatus.Unsummoned) {

					abilityPet.setUniqueId(reader.getInt());
					abilityPet.setPetName(new String(reader.getBytes(reader.getShort())));
					abilityPet.setSecondsToRentEndTime(reader.getInt());
					abilityPet.setUnk02(reader.getByte());

				}
				return abilityPet;

			} else if (item.getItemType() == ItemType.ItemExchangeCoupon) {

				ItemExchangeCoupon iec = new ItemExchangeCoupon(item);

				iec.setStackCount(reader.getShort());

				byte magParamNum = reader.getByte();

				for (int i = 0; i < magParamNum; i++) {

					iec.addMagParamValue(reader.getLong());
				}

				return iec;
			} else if (item.getItemType() == ItemType.MagicCube) {

				MagicCube cube = new MagicCube(item);

				cube.setStoredItemCount(reader.getInt());

				return cube;

			} else {
				item.setStackCount(reader.getShort());
				return item;
			}

		}).orElseGet(()->{
			ItemEntity itemEntity = ItemEntity.builder()
					.itemType(ItemType.UNKNOWN)
					.gender(Gender.Unisex)
					.race(Race.Universal)
					.build() ;
			
			Item item = new Item(itemEntity) ; 
			item.setRentType(rentType);
			item.setRent(rent);
			item.setSlot(itemSlot);
			
			
			return null ; 
		});

		
	}
}
