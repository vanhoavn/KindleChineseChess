// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LoadingScreen.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.input.Gestures;
import com.apple.eawt.event.GestureAdapter;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vn.vhn.amazon.kindle.chinesechess.Main;

public class LoadingScreen extends JPanel
{
    public JLabel lbl;
    public LoadingScreen(Main main)
    {
        super(new GridBagLayout());
        lbl = new JLabel("Chinese Chess v".concat(Main.ver).concat("\nvhn.vn 2013\n\nNow loading..."), 0);
        add(lbl);
    }
}
