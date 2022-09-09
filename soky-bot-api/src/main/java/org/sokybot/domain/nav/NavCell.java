package org.sokybot.domain.nav;

import java.util.List;

public class NavCell {

	private Position min ; 
	private Position max ; 
	
	private List<Short> navObjectIndex ;  
	private List<Short> navBorderIndex ; 
	private List<Short> navCellLinkIndex ;
	
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
	public List<Short> getNavObjectIndex() {
		return navObjectIndex;
	}
	public void setNavObjectIndex(List<Short> navObjectIndex) {
		this.navObjectIndex = navObjectIndex;
	}
	public List<Short> getNavBorderIndex() {
		return navBorderIndex;
	}
	public void setNavBorderIndex(List<Short> navBorderIndex) {
		this.navBorderIndex = navBorderIndex;
	}
	public List<Short> getNavCellLinkIndex() {
		return navCellLinkIndex;
	}
	public void setNavCellLinkIndex(List<Short> navCellLinkIndex) {
		this.navCellLinkIndex = navCellLinkIndex;
	}
	@Override
	public String toString() {
		return "NavCell [min=" + min + ", max=" + max + ", navObjectIndex=" + navObjectIndex + ", navBorderIndex="
				+ navBorderIndex + ", navCellLinkIndex=" + navCellLinkIndex + "]";
	} 
	
	
	
	
}
