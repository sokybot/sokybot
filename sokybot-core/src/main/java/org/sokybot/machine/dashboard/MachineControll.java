package org.sokybot.machine.dashboard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.slf4j.Logger;
import org.sokybot.machine.IMachineEvent;
import org.sokybot.machine.MachineState;
import org.sokybot.machine.StateChanged;
import org.sokybot.machine.StateEntry;
import org.sokybot.machine.StateExit;
import org.sokybot.machine.UserAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@WithStateMachine
public class MachineControll extends JPanel implements ActionListener {

	@Autowired
	private StateMachine<MachineState, IMachineEvent> machine;

	@Autowired
	Logger log;

	@Autowired
	@Qualifier("taskScheduler")
	Executor executor;

	ExecutorService ser = Executors.newCachedThreadPool();

	private JButton btnConn;
	private JButton btnCommit;
	private JButton btnLogStates;
	private JButton btnKillClient;

	public MachineControll() {

		this.btnConn = new JButton("Connect");
		this.btnCommit = new JButton("Apply");
		this.btnKillClient = new JButton("Kill Client");
		this.btnLogStates = new JButton("Log States");
		this.btnLogStates.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Machine States : {}", machine.getState().getIds());

			}
		});
	}

	@PostConstruct
	private void init() {
		setLayout(new GridBagLayout()); // to center vBox

		this.btnKillClient.setActionCommand(UserAction.KILL_CLIENT.name());
		this.btnConn.setActionCommand(UserAction.CONNECT.name());
		this.btnConn.addActionListener(this);
		this.btnCommit.addActionListener(this);
		this.btnKillClient.addActionListener(this);
		this.btnCommit.setActionCommand(UserAction.CONFIG_COMMIT.name());
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
		gbc.insets = new Insets(5, 5, 5, 5);

		this.add(this.btnConn, gbc);
		gbc.gridy = 1;
		this.add(this.btnKillClient, gbc);
		gbc.gridy = 2 ; 
		this.add(this.btnCommit , gbc) ; 
		
		gbc.gridy = 3;
		this.add(this.btnLogStates, gbc);

		// this.add(vbox , gbc);

	}

	@StateChanged(source = MachineState.CONFIG_COMMITTED, target = MachineState.CONFIG_UNCOMMITTED)
	public void enableApplyBtn() {
		this.btnCommit.setEnabled(true);
	}

	@StateChanged(source = MachineState.CONFIG_UNCOMMITTED, target = MachineState.CONFIG_COMMITTED)
	public void disableApplyBtn() {
		this.btnCommit.setEnabled(false);
	}

	@StateExit(source = MachineState.READY)
	public void onConnect() {
		this.btnConn.setActionCommand(UserAction.DISCONNECT.name());
		this.btnConn.setText("Disconnect");
	}

	@StateEntry(target = MachineState.READY)
	public void onDisconnect() {
		this.btnConn.setActionCommand(UserAction.CONNECT.name());
		this.btnConn.setText("Connect");

	}

	@StateEntry(target = MachineState.WITHOUT_CLIENT)
	public void disableKillBtn() { 
		this.btnKillClient.setEnabled(false);
	}
	
	@StateEntry(target = MachineState.WITH_CLIENT)
	public void enableKillBtn() { 
		this.btnKillClient.setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();

		UserAction event = UserAction.valueOf(action);

		if (event == UserAction.DISCONNECT) {

			int res = JOptionPane.showConfirmDialog(this.getTopLevelAncestor(),
					"Are you sure you want to disconnect ? ");

			if (res != JOptionPane.OK_OPTION)
				return;
		}

		this.executor.execute(() -> {
			this.machine.sendEvent(event);

		});
		log.info("Machine States : " + this.machine.getState().getIds());

	}

}
