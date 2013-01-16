// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Menu.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vn.vhn.amazon.kindle.chinesechess.Main;

public class Menu extends KMenu
{
    private class MenuDifficultItem extends KMenuItem
    {


        public MenuDifficultItem(String name, final int level)
        {
            super(Main.rLang.getString("newGame").concat(Main.rLang.getString("youvs")).concat(name));
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    main.onDifficultChanging(level, true);
                }
            }
            );
        }
    }


    public Menu(final Main main)
    {
        this.main = main;
        for(int i=0;i < Main.levelName.length;i++)
            add(new MenuDifficultItem(Main.levelName[i], i));
        KMenuItem posselMenuItem = new KMenuItem(Main.rLang.getString("SolvePuzzle"));
        posselMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                main.onPosSel();
            }

        }
        );
        add(posselMenuItem);
        KMenuItem resetMenuItem = new KMenuItem(Main.rLang.getString("resetPuzzle"));
        resetMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    main.pc.setAllUnsolved();
                }
            }
        );
        add(resetMenuItem);
        KMenuItem how2playMenuItem = new KMenuItem(Main.rLang.getString("How2Play"));
        how2playMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                main.onHowToPlay();
            }
        }
);
        add(how2playMenuItem);
        KMenuItem aboutMenuItem = new KMenuItem(Main.rLang.getString("About"));
        aboutMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                main.onAbout();
            }
        }
);
        add(aboutMenuItem);
    }

    private final Main main;

}
