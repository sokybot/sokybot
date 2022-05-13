package org.soky.bot.pk2;

public interface IPk2File {
	public boolean open()  ; 
	public boolean isOpened();
	public IPk2Reader getReader();
	public void close() ;
}
