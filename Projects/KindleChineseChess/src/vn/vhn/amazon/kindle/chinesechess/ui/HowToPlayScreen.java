// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HowToPlayScreen.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.ui.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import vn.vhn.amazon.kindle.chinesechess.Main;

public class HowToPlayScreen extends JPanel
    implements KeyListener
{
    public HowToPlayScreen(Main main)
    {
        super(new GridBagLayout());
        this.main = main;
        titleFont = new Font(null, 1, 30);
        gc = new GridBagConstraints();
        this.main = main;
        gc.gridx = gc.gridy = 0;
        gc.ipady = 10;
        gc.fill = 2;
        addTitle(Main.rLang.getString("How2Play"));
        addContent(Main.rLang.getString("How2PlayDetail"));
        enableEvents(48L);
        addKeyListener(this);
    }

    void addTitle(String s)
    {
        JLabel item = new JLabel(s, 0);
        item.setFont(titleFont);
        add(item, gc);
        gc.gridy++;
    }

    void addContent(String s)
    {
        JTextArea item = new JTextArea(s);
        item.setMaximumSize(new Dimension(main.SCREEN_SIZE.width*8/10,main.SCREEN_SIZE.height*7/10));
        item.setLineWrap(true);
        item.setWrapStyleWord(true);
        item.setEnabled(false);
        add(item, gc);
        gc.gridy++;
    }

    public void keyPressed(KeyEvent e)
    {
        main.setActivePanel(0);
        e.consume();
    }

    public void keyTyped(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    protected void processMouseEvent(MouseEvent e){
        main.setActivePanel(0);
        e.consume();
    }
    
    Font titleFont;
    GridBagConstraints gc;
    Main main;
    
}
