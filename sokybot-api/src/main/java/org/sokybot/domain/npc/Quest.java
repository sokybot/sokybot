package org.sokybot.domain.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Quest {

	private int refID;
	private byte achievementCount;
	private byte requiresSharePt;
	private byte type;
	private byte status;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private Map<Byte, Objective> objectives = new HashMap<>();

	private byte objectiveCount;
	private byte taskCount;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE) 
	private List<Integer> taskRefObjID = new ArrayList<Integer>(); // (=> NPCs to deliver to, when complete you get
																	// reward)

	public void addTaskRefObjId(int val) { 
		this.taskRefObjID.add(val) ; 
	}
	
	public void addQuestObjective(Objective objective) {
		if (objective != null) {
			this.objectives.put(objective.ID, objective);
		}
	}

	@Data
	public class Objective {
		private byte ID;
		private byte Status; // (00 = done, 01 = incomplete)
		private short NameLength;
		private String Name;
		private List<Integer> Tasks = new ArrayList<Integer>();
		private byte AchievementCount;
		private byte TaskCount;
	}

}
