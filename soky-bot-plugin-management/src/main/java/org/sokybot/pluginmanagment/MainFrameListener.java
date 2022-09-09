package org.sokybot.pluginmanagment;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component
public class MainFrameListener {

	

	private JMenuBar menuBar ;
	
	
	
	
	public MainFrameListener() { 
		System.out.println("MainFrameListener Constructed ");
	}

	@Reference(cardinality = ReferenceCardinality.MANDATORY , policy = ReferencePolicy.STATIC)
	void setPlgunMenuItem(JMenuBar menuBar) { 
		System.out.println("onBind JMenuBar") ; 
		this.menuBar = menuBar ; 
	}
	@Activate
	 void onActivate() { 
		System.out.println("onActivate MainFrameListener") ; 
		if(menuBar == null) { 
			System.out.println("MenuBar not exists") ; 
		}else { 
		JMenuItem helpMenu = 	this.menuBar.getHelpMenu() ; 
		
		if(helpMenu != null) { 
			helpMenu.add(pluginMenuItem()) ; 
			System.out.println("MenuBar exists") ; 
		}
	}
		
	}
	
	@Deactivate
	 void deActive() { 
		System.out.println("onDeActivate MainFrameListner  ");  
		this.menuBar = null ; 
		
	}
	
	
	
	
//    public void registerPluginMenuItem(JMenuBar menuBar) {
//
//		JOptionPane.showMessageDialog(null, "Hello World!!!");
////        menuBar.getHelpMenu().add(pluginMenuItem()) ;
//
//    }
//	
	
	
    private JMenuItem pluginMenuItem() {
        JMenuItem  pluginMenuItem = new JMenuItem("plugin list") ;
        pluginMenuItem.addActionListener((ev)-> {

           // JXDialog dialog = new JXDialog() ;
           // dialog.setVisible(true);

        });
        return pluginMenuItem ;
    }


    
}

