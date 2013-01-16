// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PostureSelectScreen.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.KindletContext;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vn.vhn.amazon.kindle.chinesechess.Main;
import vn.vhn.amazon.kindle.chinesechess.posCollection;
import xqwlight.Util;

public class PostureSelectScreen extends JPanel
    implements KeyListener
{
    int itemPerPage = 16;
    
    public static PostureSelectScreen Create(Main main, KindletContext cont)
    {
        return new PostureSelectScreen(main, cont, new GridBagLayout());
    }

    private PostureSelectScreen(Main main, KindletContext cont, GridBagLayout g)
    {
        super(g);
        mLabel = new JLabel[3][itemPerPage];
        titleSelected = -1;
        dimMin = new Dimension((Main.SCREEN_SIZE.width * 7) / 10, 0);
        dimMax = new Dimension((Main.SCREEN_SIZE.width * 7) / 10, 10000);
        this.main = main;
        pc = Main.pc;
        pageNo = 0;
        GridBagConstraints gc = new GridBagConstraints();
        titleLabel = new JLabel();
        titleLabel.setFont(new Font(null, Font.BOLD, 9));
        Font fontSolved = new Font(null, Font.ITALIC, 6);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = 2;
        gc.gridwidth = 2;
        gc.anchor = 11;
        gc.ipady = 20;
        add(titleLabel, gc);
        gc.gridwidth = 1;
        gc.ipady = 0;
        mLabel[0] = new JLabel[itemPerPage];
        mLabel[1] = new JLabel[itemPerPage];
        mLabel[2] = new JLabel[itemPerPage];
        for(int i = 0; i < itemPerPage; i++)
        {
            gc.gridy++;
            gc.gridx = 0;
            mLabel[0][i] = new JLabel();
            add(mLabel[0][i], gc);
            gc.gridx = 1;
            gc.weightx = 1.0D;
            mLabel[1][i] = new JLabel();
            add(mLabel[1][i], gc);
            gc.gridy++;
            mLabel[2][i] = new JLabel();
            mLabel[2][i].setFont(fontSolved);
            add(mLabel[2][i], gc);
            gc.weightx = 0.0D;
        }

        lower = new JLabel();
        lower.setForeground(Color.DARK_GRAY);
        gc.gridwidth = 2;
        gc.gridx = 0;
        gc.gridy++;
        gc.anchor = 15;
        add(lower, gc);
        onShow();
        enableEvents(48L);
        addKeyListener(this);
    }

    public void onShow()
    {
        titleSelected = -1;
        pageNo = 0;
        pageNItems = 0;
        onPageChanging();
    }

    public void onPageChanging()
    {
        if(titleSelected < 0)
        {
            titleLabel.setText("     ".concat(Main.rLang.getString("choosePuzzle")));
            pageNo = Util.MIN_MAX(0, pageNo, (posCollection.itemCount.length - 1) / itemPerPage);
            lower.setText("    ".concat(Main.rLang.getString("Page")).concat(" ")
                    .concat(String.valueOf(pageNo + 1))
                    .concat(" / ")
                    .concat(String.valueOf((posCollection.itemCount.length - 1) / itemPerPage + 1))
                    .concat("   ")
                    .concat(Main.rLang.getString("SwipeToTurn")));
            pageNItems = 0;
            int i = 0;
            for(int j = pageNo * itemPerPage; i < itemPerPage; j++)
            {
                posCollection _tmp = pc;
                if(j < posCollection.itemCount.length)
                {
                    mLabel[0][i].setText("   ".concat(String.valueOf((int)(1 + j)).concat(".")));
                    posCollection _tmp1 = pc;
                    mLabel[1][i].setText(posCollection.itemName[j]);
                    mLabel[2][i].setText("     ".concat(Main.rLang.getString("Solved")).concat(" ").concat(String.valueOf(pc.nSolved[j])).concat("/").concat(String.valueOf(posCollection.itemCount[j])));
                    pageNItems = j;
                } else
                {
                    mLabel[0][i].setText(" ");
                    mLabel[1][i].setText(" ");
                    mLabel[2][i].setText(" ");
                }
                i++;
            }

        } else
        {
            posCollection _tmp2 = pc;
            String toDisplay = posCollection.itemName[titleSelected];
            if(toDisplay.length() > 24)
                toDisplay = toDisplay.substring(0, 20).concat("...");
            titleLabel.setText("   ".concat(toDisplay));
            lower.setText("");
            pageNItems = 0;
            for(int i = 0; i < itemPerPage; i++)
            {
                posCollection _tmp3 = pc;
                if(i < posCollection.itemCount[titleSelected])
                {
                    mLabel[0][i].setText("   ".concat(String.valueOf((int)(1 + i)).concat(".")));
                    mLabel[1][i].setText(Main.rLang.getString("Puzzle").concat(" #").concat(String.valueOf(i+1)));
                    if(pc.aSolved[titleSelected][i])
                        mLabel[2][i].setText(Main.rLang.getString("Solved").concat("!"));
                    else
                        mLabel[2][i].setText("---");
                    pageNItems = i;
                } else
                {
                    mLabel[0][i].setText(" ");
                    mLabel[1][i].setText(" ");
                    mLabel[2][i].setText(" ");
                }
            }

        }
    }
    public synchronized void keyPressed(KeyEvent e){}

    /*public synchronized void keyPressed(KeyEvent e)
    {
        if(e.isConsumed())
            return;
        if(e.getKeyCode() == KindleKeyCodes.VK_BACK || e.getKeyChar() == 'z')
        {
            e.consume();
            if(titleSelected < 0)
            {
                main.setActivePanel(0);
            } else
            {
                titleSelected = -1;
                onPageChanging();
                repaint();
            }
            return;
        }
        if(titleSelected < 0)
        {
            switch(e.getKeyCode())
            {
            case KindleKeyCodes.VK_TURN_PAGE_BACK: 
                pageNo -= 2;
                // fall through

            case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE: 
            case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE: 
                pageNo++;
                onPageChanging();
                repaint();
                e.consume();
                break;
            }
            if(e.isConsumed())
                return;
        }
        char c = e.getKeyChar();
        if(c >= 'a' && c <= 'z')
            c -= ' ';
        if(c >= 'A' && c <= 'Z')
        {
            c -= 'A';
            if(c <= pageNItems)
            {
                if(titleSelected >= 0)
                {
                    posCollection _tmp = pc;
                    main.onPos(pc.getFileName(titleSelected, c), posCollection.itemName[titleSelected], titleSelected, c);
                } else
                {
                    titleSelected = pageNo * 16 + c;
                    onPageChanging();
                    repaint();
                }
                e.consume();
            }
        }
    }*/

    public void keyTyped(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    private final Main main;
    int pageNo;
    JLabel mLabel[][];
    JLabel titleLabel;
    JLabel lower;
    posCollection pc;
    int titleSelected;
    int pageNItems;
    Dimension dimMin;
    Dimension dimMax;
    
    int oldX, oldY;
    
    protected void processMouseEvent(MouseEvent e){
        if(e.getButton()==MouseEvent.BUTTON1){
            switch(e.getID()){
                case MouseEvent.MOUSE_PRESSED:
                    oldX=e.getX();
                    oldY=e.getY();
                    break;
                case MouseEvent.MOUSE_RELEASED:
                {
                    int delta = e.getX()-oldX;
                    if(delta<-main.SCREEN_SIZE.width/2){
                        pageNo++;
                        onPageChanging();
                    }
                    else if(delta>main.SCREEN_SIZE.width/2){
                        if(pageNo>0){
                            pageNo--;
                            onPageChanging();
                        }else{
                            if(titleSelected>=0){
                                titleSelected=-1;
                                onPageChanging();
                                return;
                            }else{
                                main.setActivePanel(Main.PANEL_PLAYING);
                                return;
                            }
                        }
                    }
                    else if(Math.abs(delta)<main.SCREEN_SIZE.width/10
                            && Math.abs(oldY-e.getY())<main.SCREEN_SIZE.height/10){
                        int title = -1;
                        Rectangle r=new Rectangle();
                        Rectangle r2=new Rectangle();
                        for(int i=0;i<itemPerPage;i++){
                            mLabel[0][i].getBounds(r);
                            mLabel[2][i].getBounds(r2);
                            if(e.getY()>=r.y
                                &&e.getY()<=r2.y+r2.height){
                                title = i;
                                break;
                            }
                        }
                        if(titleSelected>=0){
                            if(title>=0){
                                posCollection _tmp = pc;
                                main.onPos(pc.getFileName(titleSelected, title), posCollection.itemName[titleSelected], titleSelected, title);
                            }
                        }else{
                            titleSelected=title+pageNo*itemPerPage;
                            onPageChanging();
                        }
                    }
                }
                case MouseEvent.MOUSE_EXITED:
                    oldX=oldY=-1;
                    repaint();
                    break;
            }
        }
        e.consume();
    }
}
