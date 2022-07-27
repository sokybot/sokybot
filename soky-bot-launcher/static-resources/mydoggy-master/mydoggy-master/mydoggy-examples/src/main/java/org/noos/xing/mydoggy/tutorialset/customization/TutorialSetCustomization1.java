package org.noos.xing.mydoggy.tutorialset.customization;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowRepresentativeAnchorUI;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TutorialSetCustomization1 {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;


    protected void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");

        debugTool.setAvailable(true);


        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("TutorialSet...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ((JPanel)this.frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(5 , 5 , 5 , 5 ));
        // Create a simple JMenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);

        // Set a layout manager. I love TableLayout. It's powerful.
        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;
        customize();

        // Register a Tool.
        toolWindowManager.registerToolWindow("Debug",                      // Id
                                             "Debug Tool",                 // Title
                                             new FlatSearchIcon(),                         // Icon
                                             new JButton("Debug Tool"),    // Component
                                             ToolWindowAnchor.BOTTOM);       // Anchor

        toolWindowManager.registerToolWindow("Console",                      // Id
                "Console Tool",                 // Title
                new FlatSearchIcon(),                         // Icon
                new JButton("Console Tool"),    // Component
                ToolWindowAnchor.BOTTOM);       // Anchor


        toolWindowManager.registerToolWindow("Log",                      // Id
                "Log Tool",                 // Title
                new FlatSearchIcon(),                         // Icon
                new JButton("Log Tool"),    // Component
                ToolWindowAnchor.BOTTOM);       // Anchor


        toolWindowManager.registerToolWindow("Script",                      // Id
                "Script Tool",                 // Title
                new FlatSearchIcon(),                         // Icon
                new JButton("Script Tool"),    // Component
                ToolWindowAnchor.BOTTOM);       // Anchor

        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }

    protected void customize() {
        // Customize the toolwindow representative anchor
        // The releated UI is ToolWindowRepresentativeAnchorUI




        // Change colors
      //  UIManager.put(MyDoggyKeySpace.TWRA_MOUSE_IN_BORDER, Color.RED);
      //  UIManager.put(MyDoggyKeySpace.TWRA_MOUSE_OUT_BORDER, Color.GREEN);
       // UIManager.put(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE, UIManager.getColor("Viewport.background"));
       // UIManager.put(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START, Color.BLACK);
       // UIManager.put(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_END, Color.BLACK);
       // UIManager.put(MyDoggyKeySpace.TWRA_FOREGROUND,Color.WHITE);
       // UIManager.put(MyDoggyKeySpace.TWRA_FOREGROUND_UNAVAILABLE, Color.BLUE);

        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START , UIManager.getColor("Viewport.background")) ;
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END , UIManager.getColor("Viewport.background")) ;
       // UIManager.put(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ACTIVE , )
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START , UIManager.getColor("TitlePane.inactiveBackground")) ;
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END , UIManager.getColor("TitlePane.inactiveBackground")) ;
        UIManager.put(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE , UIManager.getColor("TitlePane.inactiveForeground")) ;


        // Modify the UI class
        UIManager.put("ToolWindowRepresentativeAnchorUI", "org.noos.xing.mydoggy.tutorialset.customization.TutorialSetCustomization1$CustomizedToolWindowRepresentativeAnchorUI");

       Object obj =  UIManager.get("ToolWindowRepresentativeAnchorUI") ;
       System.out.println(obj) ;
       // FlatDarkLaf.
    }


    public static void main(String[] args) {

        FlatDarkLaf.setup() ;

        TutorialSetCustomization1 test = new TutorialSetCustomization1();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class CustomizedToolWindowRepresentativeAnchorUI extends ToolWindowRepresentativeAnchorUI {

        public static ComponentUI createUI(JComponent c) {

            return new CustomizedToolWindowRepresentativeAnchorUI();
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);


            // FlatLaf.installColorsAndFont(c , "black" , "black" , "Segoe UI 12");
            System.out.println("New border installed") ;

        }


        private Border b = BorderFactory.createEmptyBorder(10 , 5 , 10 , 5) ;
        private Color selectedBgColor = UIManager.getColor("ToggleButton.toolbar.selectedBackground") ;
        private Color unselectedBgColor =UIManager.getColor("Viewport.background");

        private Color selectedFgColor = UIManager.getColor("ToggleButton.selectedForeground") ;

        private Color unSelectedFgColor = UIManager.getColor("ToggleButton.foreground") ;
        @Override
        protected void updateAnchor(Graphics g, JComponent c, Color backgroundStart, Color backgroundEnd, boolean active, boolean flashing) {

            Rectangle r = c.getBounds();
            r.x = r.y = 0;


               // System.out.println("On Update Anchor btn") ;
            if (flashing || active) {
                GraphicsUtil.fillRect(g,
                                      r,
                                   //   backgroundStart,
                                     // backgroundEnd,
                        selectedBgColor ,
                        selectedBgColor ,
                        null,
                                      GraphicsUtil.FROM_CENTRE_GRADIENT_ON_Y);
                c.setForeground(selectedFgColor);
            } else {
             //   g.setColor(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
                g.setColor(unselectedBgColor);
                g.fillRect(0, 0, r.width+5, r.height+5);
                c.setForeground(unSelectedFgColor);
            }

            c.setBorder(b);
        }
    }
}