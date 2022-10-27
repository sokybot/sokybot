package org.sokybot.machine.dashboard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.sokybot.machine.MachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.StateChanged;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@WithStateMachine
public class MachineControll extends JPanel implements ActionListener {

	@Autowired
	private StateMachine<MachineState, MachineEvent> machine;
	private JButton btnConn;
	private JButton btnCommit;

	public MachineControll() {

		this.btnConn = new JButton("Connect");
		this.btnCommit = new JButton("Apply");

	}

	@PostConstruct
	private void init() {
		setLayout(new GridBagLayout()); // to center vBox

		this.btnConn.setActionCommand(MachineEvent.CONNECT.name());
		this.btnConn.addActionListener(this);
		this.btnCommit.addActionListener(this);
		this.btnCommit.setActionCommand(MachineEvent.CONFIG_COMMIT.name());
		this.btnCommit.setEnabled(false);
//		Box vbox = Box.createVerticalBox();
//		vbox.add(Box.createVerticalStrut(5));
//		vbox.add(this.btnConn);
//		vbox.add(Box.createVerticalStrut(5));
//		vbox.add(this.btnCommit);
//		vbox.add(Box.createVerticalStrut(5));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5) ; 
		
		this.add(this.btnConn, gbc);
		gbc.gridy = 1;
		this.add(this.btnCommit, gbc);

		// this.add(vbox , gbc);

	}

	
	@StateChanged(source = MachineState.CONFIG_COMMITTED , target = MachineState.CONFIG_UNCOMMITTED)
	public void enableApplyBtn() { 
		this.btnCommit.setEnabled(true);
	}
	
	@StateChanged(source = MachineState.CONFIG_UNCOMMITTED , target = MachineState.CONFIG_COMMITTED)
	public void disableApplyBtn() { 
		this.btnCommit.setEnabled(false);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.btnConn) {
			String action = this.btnConn.getActionCommand();

			if (action.equals(MachineEvent.CONNECT.name())) {

				if (this.machine.sendEvent(MachineEvent.CONNECT)) {
					this.btnConn.setActionCommand(MachineEvent.DISCONNECT.name());
					this.btnConn.setText("Disconnect");
				}
			} else if (action.equals(MachineEvent.DISCONNECT.name())) {
				int res = JOptionPane.showConfirmDialog(this.getTopLevelAncestor(),
						"Are you sure you want to disconnect ? ");

				if (res == JOptionPane.OK_OPTION) {
					if (this.machine.sendEvent(MachineEvent.DISCONNECT)) {
						this.btnConn.setActionCommand(MachineEvent.CONNECT.name());
						this.btnConn.setText("Connect");
					}
				}

			}

		} else if (e.getSource() == this.btnCommit) {
			if(!this.machine.sendEvent(MachineEvent.CONFIG_COMMIT)) {
				System.out.println("commit not accepted") ; 
			}
		}

	}
}
