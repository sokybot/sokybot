package org.sokybot.domain;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class SilkrodEntity {
	private int refId;
	private String longId;
	private String name;
}
