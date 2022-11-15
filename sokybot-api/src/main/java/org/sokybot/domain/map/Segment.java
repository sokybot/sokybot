package org.sokybot.domain.map;

import java.util.List;

public class Segment {
	
	 private String fileName ; 
	 private short sectorX ; 
	 private short sectorY ;
	 
	 private List<NavObject> navObjects ; 
	 private List<NavCell> navCells ; 
	 private List<NavBorder> navBorders ; 
	 private List<NavCellLink> cellLinks ; 
	 
	 private float[] hightMap ; 
	 
	public short getSectorX() {
		return sectorX;
	}
	

	public void setSectorX(short sectorX) {
		this.sectorX = sectorX;
	}
	public short getSectorY() {
		return sectorY;
	}
	public void setSectorY(short sectorY) {
		this.sectorY = sectorY;
	}


	public List<NavObject> getNavObjects() {
		return navObjects;
	}


	public float[] getHightMap() {
		return hightMap;
	}


	public void setHightMap(float[] hightMap) {
		this.hightMap = hightMap;
	}


	public void setNavObjects(List<NavObject> navObjects) {
		this.navObjects = navObjects;
	}


	public List<NavCell> getNavCells() {
		return navCells;
	}


	public void setNavCells(List<NavCell> navCells) {
		this.navCells = navCells;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public List<NavBorder> getNavBorders() {
		return navBorders;
	}


	public void setNavBorders(List<NavBorder> navBorders) {
		this.navBorders = navBorders;
	}


	public List<NavCellLink> getCellLinks() {
		return cellLinks;
	}


	public void setCellLinks(List<NavCellLink> cellLinks) {
		this.cellLinks = cellLinks;
	}


	@Override
	public String toString() {
		return "Segment [fileName=" + fileName + ", sectorX=" + sectorX + ", sectorY=" + sectorY + ", navObjects="
				+ navObjects + ", navCells=" + navCells + ", navBorders=" + navBorders + ", cellLinks=" + cellLinks
				+ "]";
	}


	
	
	 
	 
	 
	 
	 
}
