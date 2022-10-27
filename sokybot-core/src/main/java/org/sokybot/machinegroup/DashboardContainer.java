package org.sokybot.machinegroup;

import java.awt.CardLayout;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.sokybot.machinegroup.navigationtree.NavTreeSelectionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DashboardContainer extends JPanel {

	private CardLayout cardLayout;

	@PostConstruct
	void init() {

		this.cardLayout = new CardLayout();
		this.setLayout(this.cardLayout);
		setBorder(BorderFactory.createEtchedBorder());

	}

	public void addPage(String name, java.awt.Component component) {

		this.add(name, component);

	}

	@EventListener
	public void showDashboard(NavTreeSelectionEvent navTreeSelectionEvent) {

		String path = navTreeSelectionEvent.getSelectedPath();

		cardLayout.show(this, path);

	}

}
