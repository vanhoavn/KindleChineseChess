// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PlayingScreen.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.*;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vn.vhn.amazon.kindle.chinesechess.Board.ChineseBoard;
import vn.vhn.amazon.kindle.chinesechess.Main;
import vn.vhn.amazon.kindle.chinesechess.posCollection;

// Referenced classes of package vn.vhn.amazon.kindle.chinesechess.ui:
//            BoardComponent

public class PlayingScreen extends JPanel
{

    public PlayingScreen(KindletContext context, ChineseBoard board)
    {
        super(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        titleLabel = new JLabel();
        infoLabel = new JLabel();
        titleLabel.setFont(new Font(null, 1, 17));
        gc.gridx = 0;
        gc.gridy = 0;
        if(Main.posmode == 0)
        {
            titleLabel.setText(Main.rLang.getString("youvs").concat(Main.levelName[Main.level]));
        } else
        {
            posCollection _tmp = Main.pc;
            titleLabel.setText(posCollection.itemName[Main.posi].concat(" ".concat(String.valueOf(Main.posj + 1))));
        }
        add(titleLabel, gc);
        boardComp = new BoardComponent(context, infoLabel, board);
        gc.gridy = 1;
        add(boardComp, gc);
        gc.gridy = 2;
        infoLabel.setText("- vhnvn 2013 -");
        add(infoLabel, gc);
        enableEvents(48L);
        addKeyListener(boardComp);
    }

    public void onNewGame()
    {
        if(Main.posmode == 0)
        {
            titleLabel.setText("You vs ".concat(Main.levelName[Main.level]));
        } else
        {
            posCollection _tmp = Main.pc;
            titleLabel.setText(posCollection.itemName[Main.posi].concat(" ".concat(String.valueOf(Main.posj + 1))));
        }
        boardComp.onNewGame();
    }

    public void onPosMode(String fn, String name, int posi, int posj)
    {
        posCollection _tmp = Main.pc;
        titleLabel.setText(posCollection.itemName[Main.posi].concat(" ".concat(String.valueOf(Main.posj + 1))));
        boardComp.onPosMode(fn, name, posi, posj);
    }

    JLabel titleLabel;
    JLabel infoLabel;
    BoardComponent boardComp;
}
