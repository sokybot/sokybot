package org.sokybot.service;

import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;

public interface IGameDAO {

	Integer getVersion();

	Integer getPort();

	String getRndHost();

	DivisionInfo getDivisionInfo();

	ItemEntity getItem(int refId);

	SkillEntity getSkill(int refId);

}