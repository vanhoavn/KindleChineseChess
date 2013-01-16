// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AboutScreen.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.ui.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import vn.vhn.amazon.kindle.chinesechess.Main;

public final class AboutScreen extends JPanel
    implements KeyListener
{

    public AboutScreen(Main main)
    {
        super(new GridBagLayout());
        titleFont = new Font(null, 1, 20);
        gc = new GridBagConstraints();
        this.main = main;
        gc.gridx = gc.gridy = 0;
        gc.ipady = 10;
        gc.fill = 2;
        addTitle(Main.rLang.getString("Abouttheprogram"));
        addContent("KindleChineseChess v ".concat(Main.ver).concat(", ")
                .concat(Main.date).concat("\n")
                .concat(Main.rLang.getString("AboutCredit")));
        addTitle(Main.rLang.getString("AboutthisGame"));
        addContent(Main.rLang.getString("AboutGame"));
        addTitle(Main.rLang.getString("Support"));
        addContent(Main.rLang.getString("SupportDetail"));
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

    @Override public void keyPressed(KeyEvent e)
    {
        e.consume();
        main.setActivePanel(0);
    }

    @Override public void keyTyped(KeyEvent keyevent)
    {
    }

    @Override public void keyReleased(KeyEvent keyevent)
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
