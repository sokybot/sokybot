package org.sokybot.domain.nav;

public class NavBorder {

	
	private Position min ; 
	private Position max ; 
	
	private byte lineFlag ; 
	private byte lineSource ; 
	private byte lineDestination ; 
	private short cellSourceSector ; 
	private short cellDestinationSector ; 
	private short regionSourceYX ; 
	private short regionDestionationYX ;
	
	
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
	public short getRegionSourceYX() {
		return regionSourceYX;
	}
	public void setRegionSourceYX(short regionSourceYX) {
		this.regionSourceYX = regionSourceYX;
	}
	public short getRegionDestionationYX() {
		return regionDestionationYX;
	}
	public void setRegionDestionationYX(short regionDestionationYX) {
		this.regionDestionationYX = regionDestionationYX;
	}
	@Override
	public String toString() {
		return "NavBorder [min=" + min + ", max=" + max + ", lineFlag=" + lineFlag + ", lineSource=" + lineSource
				+ ", lineDestination=" + lineDestination + ", cellSourceSector=" + cellSourceSector
				+ ", cellDestinationSector=" + cellDestinationSector + ", regionSourceYX=" + regionSourceYX
				+ ", regionDestionationYX=" + regionDestionationYX + "]";
	} 
	
	
	
	
}
