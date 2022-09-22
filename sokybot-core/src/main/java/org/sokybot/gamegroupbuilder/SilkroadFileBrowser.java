/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sokybot.gamegroupbuilder ; 


import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;





/**
 *
 * @author AMROO
 */

@org.springframework.stereotype.Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SilkroadFileBrowser extends JFileChooser {
	public static final byte SILKROAD_DIRECTORY = 0;

	private JTextField FileNameTextField;
	private JButton ApproveButton;



	
	
	@PostConstruct
	  void inti() {
		
			setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			setSelectedFile(new File("E:\\Amroo\\Silkroad Games\\Silkroad"));
			addChoosableFileFilter(new FileNameExtensionFilter("Silkroad Directory", "*"));
			setAcceptAllFileFilterUsed(true);
			setDialogTitle("Select Silkroad Directory");
			setApproveButtonText("Select");
			FileNameTextField = getFileNameTextField(getComponents());
			ApproveButton = getSelectButton(this);
			ApproveButton.setEnabled(false);
			FileNameTextField.setEditable(false);
			addPropertyChangeListener((PropertyChangeEvent evt) -> {

				if (evt.getPropertyName().equals("directoryChanged")) {

					onDirectoryChanged(getCurrentDirectory());
				}

			});
			if (isSilkraodDirectory(getCurrentDirectory())) {

				getSelectButton(this).setEnabled(true);
				FileNameTextField.setText(getCurrentDirectory().getAbsolutePath());
			} else {
				getSelectButton(this).setEnabled(false);
			}

//			int result = showOpenDialog(context);
//			if (result == JFileChooser.APPROVE_OPTION) {
//
//					this.handler.onUserSelectDir(getSelectedFile().getAbsolutePath());
//
//			} 

		}

	

	
	



	private JTextField getFileNameTextField(Component[] comp) {
		JTextField temp = null;
		if (comp != null) {
			for (int i = 0; i < comp.length; i++) {

				if (comp[i] instanceof JPanel) {
					if ((temp = getFileNameTextField(((JPanel) comp[i]).getComponents())) != null) {
						return temp;
					}
				} else if (comp[i] instanceof JTextField) {

					temp = ((JTextField) comp[i]);
				}

			}

		}
		return temp;
	}

	private void onDirectoryChanged(File CurrentDirectory) {

		if (isSilkraodDirectory(CurrentDirectory)) {

			ApproveButton.setEnabled(true);
		} else {
			ApproveButton.setEnabled(false);
		}

	}

	private boolean isSilkraodDirectory(File directory) {
		{
			if (directory.isDirectory()) {

				if (isContainsFile(directory, "SRO_Client.exe") && isContainsFile(directory, "Data.pk2")
						&& isContainsFile(directory, "Map.pk2") && isContainsFile(directory, "Media.pk2")
						&& isContainsFile(directory, "Music.pk2") && isContainsFile(directory, "Particles.pk2")
						&& isContainsFile(directory, "GFXFileManager.dll")) {

					return true;

				}

			}
			return false;
		}

	}

	private boolean isContainsFile(File directory, String fileName) {

		if (directory.isDirectory()) {
			File[] fileList;
			fileList = directory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return (name.equalsIgnoreCase(fileName));
				}
			});
			if (fileList != null) {
				for (File FileEntry : fileList) {
					if (!FileEntry.isDirectory()) {

						if (FileEntry.getName().equalsIgnoreCase(fileName)) {
							return true;
						}
						// res = fileName.equalsIgnoreCase(FileEntry.getName()) ;
					}
				}
			}

		}
		return false;
	}

	private JButton getSelectButton(Container c) {
		JButton temp = null;
			for (Component comp : c.getComponents()) {
				if (comp == null) {
					continue;
				}
				if (comp instanceof JButton && (temp = (JButton) comp).getText() != null
						&& temp.getText().equals("Select")) {
					return temp;
				} else if (comp instanceof Container) {
					if ((temp = getSelectButton((Container) comp)) != null) {
						return temp;
					}
				}
			}

		
		return temp;
	}

}
