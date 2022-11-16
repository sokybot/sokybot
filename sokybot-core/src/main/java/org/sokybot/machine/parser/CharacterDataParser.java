package org.sokybot.machine.parser;

import org.sokybot.domain.npc.Inventory;
import org.sokybot.domain.npc.Mastery;
import org.sokybot.domain.skill.Skill;
import org.sokybot.domain.skill.SkillEntity;
import org.sokybot.domain.skill.SkillType;
import org.sokybot.machine.model.Trainer;
import org.sokybot.network.IPacketObserver;
import org.sokybot.network.IPacketPublisher;
import org.sokybot.network.packet.IStreamReader;
import org.sokybot.network.packet.ImmutablePacket;
import org.sokybot.service.IGameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CharacterDataParser implements IPacketObserver {

	@Autowired
	private Trainer trainer;

	@Autowired
	private Inventory inventory;

	@Autowired
	private IGameDAO gameDao;

	@Autowired
	public CharacterDataParser(IPacketPublisher packetPublisher) {
		packetPublisher.subscribe(this, 0x3013);
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNext(int opcode, ImmutablePacket packet) {

		switch (opcode) {

		case 0x3013:

			IStreamReader reader = packet.getStreamReader();

			trainer.setServerTime(reader.getInt());
			trainer.setRefId(reader.getInt());
			trainer.setCharScale(reader.getByte());
			trainer.setCharLvl(reader.getByte());
			trainer.setMaxlvl(reader.getByte());
			trainer.setCharEXPOffset(reader.getLong());
			trainer.setSexpOffSet(reader.getInt());
			trainer.setGold(reader.getLong());
			trainer.setSkillPoint(reader.getInt());
			trainer.setCharStatPoint(reader.getShort());
			trainer.setZerkCount(reader.getByte());
			trainer.setGatheredExpPoint(reader.getInt());
			trainer.setCharHP(reader.getInt());
			trainer.setCharMP(reader.getInt());
			trainer.setAutoInverstExp(reader.getByte());
			trainer.setDailyPK(reader.getByte());
			trainer.setTotalPK(reader.getShort());
			trainer.setPkPenaltyPoint(reader.getInt());
			trainer.setZerkLvl(reader.getByte());
			trainer.setFreePVP(reader.getByte());

			trainer.setItemInventorySize(reader.getByte());
			trainer.setItemCount(reader.getByte());
			;

			trainer.clearItemInventory();
			ItemReader itemReader = new ItemReader(reader, gameDao);

			for (int i = 0; i < trainer.getItemCount(); i++) {
				trainer.addItem(itemReader.readItem());
			}

			trainer.setAvaterInventorySize(reader.getByte());
			trainer.setAvaterItemCount(reader.getByte());

			trainer.clearAvaterInventory();

			for (int i = 0; i < trainer.getAvaterItemCount(); i++) {
				trainer.addAvaterItem(itemReader.readItem());
			}

			trainer.setHasMask(reader.getByte());

			byte mastryFlag = reader.getByte();

			while (mastryFlag == 1) {
				Mastery mastery = new Mastery();
				mastery.setMasteryID(reader.getInt());
				mastery.setMasteryLevel(reader.getByte());
				trainer.addMastery(mastery);
				mastryFlag = reader.getByte();
			}

			reader.getByte(); // Mastery end byte

			byte skillFlag = reader.getByte(); // [0 = done, 1 = Skill]

			while (skillFlag == 1) {

				 int refId = reader.getInt() ; 
				 
				this.gameDao.findSkill(refId).ifPresentOrElse((skillEntity) -> {
					Skill skill = new Skill(skillEntity);
					skill.setIsEnabled(reader.getByte());
					
					trainer.addSkill(skill) ; 
					
				}, () -> {
					SkillEntity skillEntity = SkillEntity.builder().type(SkillType.UNKNOWN).refId(refId).build() ;
					Skill skill = new Skill(skillEntity); 
					skill.setIsEnabled(reader.getByte()); 
					trainer.addSkill(skill) ;
				});

				skillFlag = reader.getByte();
			}

			break;
		}
	}
}
