package org.sokybot.machine.page;

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public abstract class IMachinePage extends JPanel implements Ordered {

	protected final String LOG_NAME = "Logs";

	
	protected final int FIRST_ORDER = 0;
	protected final int LOG_ORDER = FIRST_ORDER ;
	protected final int LAST_ORDER = LOG_ORDER + 1;

	
	
	@Autowired
	private ResourceLoader resourceLoader ;
	
	public abstract String getName();

	public abstract String getIcon();

	@Override
	public int getOrder() {

		String name = getName();

		switch (name) {

		case LOG_NAME:
			return LOG_ORDER;
		default:
			return LAST_ORDER;

		}

	}
	public final Icon getRepresentativeIcon() {
 
		
		Resource r = this.resourceLoader.getResource(getIcon());

		try {
			return new FlatSVGIcon(r.getInputStream()).derive(30, 30);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
