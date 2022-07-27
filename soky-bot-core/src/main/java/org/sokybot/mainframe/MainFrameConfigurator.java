package org.sokybot.mainframe;

import org.noos.xing.mydoggy.*;
import org.sokybot.service.IMainFrameConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Service
public class MainFrameConfigurator implements IMainFrameConfigurator{

    @Autowired
    private ToolWindowManager toolWindowManager ;

    @Autowired
    private JMenuBar menuBar ;

    

    @Override
    public void addExtraWindow(String id , String title, Icon icon, Component comp) {

       ToolWindow toolWindow =  toolWindowManager.registerToolWindow(id , title , icon , comp ,  ToolWindowAnchor.BOTTOM) ;

        toolWindow.setAvailable(true);
       // toolWindow.setVisible(true);

        // RepresentativeAnchorDescriptor
        RepresentativeAnchorDescriptor representativeAnchorDescriptor = toolWindow.getRepresentativeAnchorDescriptor();
        representativeAnchorDescriptor.setPreviewEnabled(true);
        representativeAnchorDescriptor.setPreviewDelay(1500);
        representativeAnchorDescriptor.setPreviewTransparentRatio(0.4f);

        // DockedTypeDescriptor
        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.setAnimating(true);
        dockedTypeDescriptor.setHideRepresentativeButtonOnVisible(false);
        dockedTypeDescriptor.setDockLength(300);
        dockedTypeDescriptor.setPopupMenuEnabled(true);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);

        JMenu toolsMenu = dockedTypeDescriptor.getToolsMenu();
        toolsMenu.add(new AbstractAction("Hello World!!!") {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Hello World!!!");
            }
        });
        dockedTypeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
            public void onHideButtonClick(ToolWindow toolWindow) {
                JOptionPane.showMessageDialog(null, "Hiding...");
                toolWindow.setVisible(false);
            }
        });

        dockedTypeDescriptor.setAnimating(true);

        // SlidingTypeDescriptor
        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);
        slidingTypeDescriptor.setTransparentMode(true);
        slidingTypeDescriptor.setTransparentRatio(0.8f);
        slidingTypeDescriptor.setTransparentDelay(0);
        slidingTypeDescriptor.setAnimating(true);
        slidingTypeDescriptor.setIdVisibleOnTitleBar(false);

        // FloatingTypeDescriptor
        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setEnabled(true);
        floatingTypeDescriptor.setLocation(150,200);
        floatingTypeDescriptor.setSize(320,200);
        floatingTypeDescriptor.setModal(false);
        floatingTypeDescriptor.setTransparentMode(true);
        floatingTypeDescriptor.setTransparentRatio(0.2f);
        floatingTypeDescriptor.setTransparentDelay(1000);
        floatingTypeDescriptor.setAnimating(true);
        floatingTypeDescriptor.setIdVisibleOnTitleBar(false);

    }

    @Override
    public void addPage(String title, Component component) {

        throw new UnsupportedOperationException() ;
    }

    
}
