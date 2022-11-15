package org.sokybot.domain.map;

public class NavCellLink {
	
	private Position min ; 
	private Position max ; 
	
	private byte lineFlag ; 
	private byte lineSource ; 
	private byte lineDestination ; 
	private short cellSourceSector ; 
	private short cellDestinationSector ;
	
	public Position getMin() {
		return min;
	}
	public void setMin(Position min) {
		this.min = min;
	}
	public Position getMax() {
		return max;
	}
	public void setMax(Position max) {
		this.max = max;
	}
	public byte getLineFlag() {
		return lineFlag;
	}
	public void setLineFlag(byte lineFlag) {
		this.lineFlag = lineFlag;
	}
	public byte getLineSource() {
		return lineSource;
	}
	public void setLineSource(byte lineSource) {
		this.lineSource = lineSource;
	}
	public byte getLineDestination() {
		return lineDestination;
	}
	public void setLineDestination(byte lineDestination) {
		this.lineDestination = lineDestination;
	}
	public short getCellSourceSector() {
		return cellSourceSector;
	}
	public void setCellSourceSector(short cellSourceSector) {
		this.cellSourceSector = cellSourceSector;
	}
	public short getCellDestinationSector() {
		return cellDestinationSector;
	}
	public void setCellDestinationSector(short cellDestinationSector) {
		this.cellDestinationSector = cellDestinationSector;
	}
	@Override
	public String toString() {
		return "NavCellLink [min=" + min + ", max=" + max + ", lineFlag=" + lineFlag + ", lineSource=" + lineSource
				+ ", lineDestination=" + lineDestination + ", cellSourceSector=" + cellSourceSector
				+ ", cellDestinationSector=" + cellDestinationSector + "]";
	} 
	
	

}
