package org.sokybot.machine.parser;
import org.sokybot.domain.item.Equipment;
import org.sokybot.domain.item.Item;
import org.sokybot.domain.item.ItemRent;
import org.sokybot.machine.model.Inventory;
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
	private Trainer  trainer ; 
	
	@Autowired
	private Inventory inventory ; 
	
	
	@Autowired
	private IGameDAO gameDao ; 
	
	
	@Autowired
	public CharacterDataParser(IPacketPublisher packetPublisher) { 
		packetPublisher.subscribe(this, 0x3013) ; 
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
		
		switch(opcode) { 
		
		case 0x3013 : 
			
			IStreamReader reader = packet.getStreamReader() ; 
			
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
			
			
			
			
			
			break ; 
		}
	}
}
