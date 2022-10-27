package org.sokybot.app.machinebuilder;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.sing_group.gc4s.dialog.AbstractInputJDialog;
import org.sokybot.app.Constants;
import org.sokybot.app.service.IMachineGroupService;
import org.sokybot.domain.Division;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.domain.SkillEntity;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.service.IGameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.formdev.flatlaf.FlatDarkLaf;

@Component
@Scope("prototype")
public class MachineBuilderDialog extends AbstractInputJDialog implements ItemListener {

	@Autowired
	private GameDataPanel gameDataPanel;

	@Autowired
	private MachineDataPanel machineDataPanel;

	@Autowired
	private IMachineGroupService machineGroupService;

	private Set<String> groupNames  ; 
	
	private JComboBox<String> cmbGroups;

	private Box page;

	public MachineBuilderDialog(JFrame parrent) {

		super(parrent);
		this.cmbGroups = new JComboBox<>();

	}

	@PostConstruct
	private void init() {

		this.groupNames = this.machineGroupService.listGroups() ; 
		
		super.okButton.setText("Create");
		
		super.okButton.setEnabled(!this.groupNames.isEmpty());
		
		getDescriptionPane().setBackground(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.gameDataPanel.setBorder(BorderFactory.createTitledBorder("Game Data"));
		this.machineDataPanel.setBorder(BorderFactory.createTitledBorder("Bot Data"));
		Box groupField = Box.createHorizontalBox();
		groupField.add(new JLabel("Group(s)"));
		groupField.add(Box.createHorizontalStrut(5));
		groupField.add(this.cmbGroups);
		groupField.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 3));

		this.cmbGroups.addItemListener(this);
		
		this.machineGroupService.listGroups().forEach((g)->{
		  this.cmbGroups.addItem(g) ; 	
		});
		
		this.page.add(groupField);
		this.page.add(this.gameDataPanel);
		this.page.add(Box.createVerticalStrut(5));
		this.page.add(this.machineDataPanel);

		pack();

	}

	@Override
	protected java.awt.Component getInputComponentsPane() {

		if (this.page == null) {
			this.page = Box.createVerticalBox();

			this.page.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		return page;
	}

	@Override
	protected String getDescription() {

		return "Create new bot ";
	}

	@Override
	protected String getDialogTitle() {

		return "Machine Builder";
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getSource() == this.cmbGroups) {

			if (e.getStateChange() == ItemEvent.SELECTED) {
				String selectedGroup =(String) this.cmbGroups.getSelectedItem() ; 
				IGameDAO targetGame = this.machineGroupService.getGameDAO(selectedGroup) ; 
				this.gameDataPanel.setVersion(targetGame.getVersion());
				this.gameDataPanel.setPort(targetGame.getPort());
				SilkroadType type = targetGame.getGameType() ; 
				
				this.gameDataPanel.setLanguage(type.getLanguage());
				this.gameDataPanel.setCountry(type.getCountry());
				this.gameDataPanel.setDivisionInfo(targetGame.getDivisionInfo());
			}

		}

	}

	
	@Override
	protected void onOkButtonEvent(ActionEvent event) {
	  
		
		String selectedGroup =(String) this.cmbGroups.getSelectedItem() ; 
		String trainer = this.machineDataPanel.getTrainerName() ; 
		String targetHost = this.gameDataPanel.getSelectedHost() ; 
		
		if(!trainer.isBlank()) { 
			String[]  opts = {"--" + Constants.MACHINE_TARGET_HOST + "=" +targetHost} ; 
			this.machineGroupService.createMachine(selectedGroup, trainer  , opts);
			
			super.onOkButtonEvent(event);
		}

	}
	public static void main(String args[]) {
		FlatDarkLaf.setup();

		MachineBuilderDialog b = new MachineBuilderDialog(null);
		b.machineGroupService = new MockMachineGroupService() ; 
		GameDataPanel gdp = new GameDataPanel();
		gdp.init();
		AccountInfoPanel accountInfoPanel = new AccountInfoPanel();
		accountInfoPanel.init();

		MachineDataPanel mdb = new MachineDataPanel(accountInfoPanel);
		mdb.init();

		b.gameDataPanel = gdp;
		b.machineDataPanel = mdb;
		b.init();
		b.setVisible(true);
	}

	private static class MockMachineGroupService implements IMachineGroupService {

		
		
		@Override
		public void createMachine(String parentGroup, String trainerName, String... options) {
		 
			
		}
		@Override
		public void createMachineGroup(String name, String gamePath) {
			// TODO Auto-generated method stub

		}

		@Override
		public IGameDAO getGameDAO(String groupName) {
			MockGameDAO gameDao = new MockGameDAO() ; 
			
			if(groupName.equals("Game1")) { 
					gameDao.port = 6000 ; 
					gameDao.version = 220 ; 
					gameDao.div.local = 22 ; 
					Division div = new Division() ; 
					div.name = "DIV1" ; 
					div.addHost("HOST1");
					div.addHost("HOST2");
					
					gameDao.div.addDivision(div);
			}else if(groupName.equals("Game2")) { 
				
				gameDao.port = 7000 ; 
				gameDao.version = 230 ; 
				gameDao.div.local = 23 ; 
				Division div = new Division() ; 
				div.name = "DIV1" ; 
				div.addHost("DIV1-HOST1");
				div.addHost("DIV1-HOST2");
				
				gameDao.div.addDivision(div);
				div = new Division() ;
				div.name = "DIV2" ; 
				div.addHost("DIV2-HOST1");
				div.addHost("DIV2-HOST2");
				gameDao.div.addDivision(div);
				
			}else if(groupName.equals("Game3")) { 
				gameDao.port = 8000 ; 
				gameDao.version = 240 ; 
				gameDao.div.local = 24 ; 
				Division div = new Division() ; 
				div.name = "DIV3" ; 
				div.addHost("DIV3-HOST1");
				div.addHost("DIV3-HOST2");
				
				gameDao.div.addDivision(div);
				div = new Division() ;
				div.name = "DIV2" ; 
				div.addHost("DIV3-HOST1");
				div.addHost("DIV3-HOST2");
				gameDao.div.addDivision(div);
				
			}
			return gameDao ; 
		}

		@Override
		public Set<String> listGroups() {
			return Set.of("Game1" , "Game2" , "Game3") ; 
		}

	}

	private static class MockGameDAO implements IGameDAO {
  
		private Integer port = 320 ; 
		private Integer version  = 5000; 
		private DivisionInfo div = new DivisionInfo()  ; 
		
		@Override
		public Integer getVersion() {
			return this.port;
		}

		@Override
		public Integer getPort() {
			return  this.version;
		}

		@Override
		public String getRndHost() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DivisionInfo getDivisionInfo() {
			return this.div; 
		}

		@Override
		public ItemEntity getItem(int refId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SkillEntity getSkill(int refId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SilkroadType getGameType() {
		 
			return new SilkroadType(Map.of("Language", "Arabic" , "Country" , "Egypt"));
		}

	}
}
