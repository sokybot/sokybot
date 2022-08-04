package org.soky.sro.pk2.entityextractors;

import org.sokybot.domain.SilkroadType;

public class SilkroadDataExtractor implements IPK2EntityExtractor<SilkroadData> {

	private IPk2File reader;

	public SilkroadDataExtractor(IPk2File reader) {
		this.reader = reader;
	}

	private SilkroadType extractType() {
		byte[] bytes = reader.getFileBytes("type.txt");

		String Type = new String(bytes);
		String[] lines = Type.split(System.getProperty("line.separator"));

		SilkroadType type = new SilkroadType();
		int x = 0;
		for (int i = 0; i < 2; i++) {
			for (; x < lines.length;) {
				String parts[] = lines[x++].split("=");

				if (parts[0].trim().equalsIgnoreCase("Language") || parts[0].trim().equalsIgnoreCase("Country")) {
					type.addProperty(parts[0].trim(), parts[1].replace("\"", " ").trim());

					break;
				}
				// ++x ;
			}
		}

		return type;
	}

	


	private DivisionInfo extractDivisionInfo() {
		DivisionInfo divisionInfo = new DivisionInfo();
		if(reader == null) System.out.println("Reader is null") ;  
		byte bytes[] = reader.getFileBytes("divisioninfo.txt");
		BinaryReader binaryReader = new BinaryReader(bytes);
		divisionInfo.local = binaryReader.getByte().get();
		byte DivisionCount = binaryReader.getByte().get();
		for (byte i = 0; i < DivisionCount; i++) {
			Division division = new Division();
			int NameLength = binaryReader.getDword().toInteger();
			String Name = binaryReader.getSTR(NameLength).get();
			division.name = Name;
			binaryReader.skip(1);
			byte ipCount = binaryReader.getByte().get();
			for (byte x = 0; x < ipCount; x++) {
				int ipLength = binaryReader.getDword().toInteger();
				String ip = binaryReader.getSTR(ipLength).get();

				division.addHost(ip);
				binaryReader.skip(1);
			}

			divisionInfo.addDivision(division);
		}
		return divisionInfo;
	}

	@Override
	public SilkroadData extract() {
		SilkroadData res = new SilkroadData();
		res.setDivisionInfo(extractDivisionInfo());
		res.setSilkroadType(extractType());
		res.setPort(extractPort());
		res.setVersion(extractVersion());

		return res;
	}

}
