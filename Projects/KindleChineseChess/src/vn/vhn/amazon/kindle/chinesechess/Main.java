// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Main.java

package vn.vhn.amazon.kindle.chinesechess;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.security.SecureStorage;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import vn.vhn.amazon.kindle.chinesechess.Board.ChineseBoard;
import vn.vhn.amazon.kindle.chinesechess.ui.AboutScreen;
import vn.vhn.amazon.kindle.chinesechess.ui.HowToPlayScreen;
import vn.vhn.amazon.kindle.chinesechess.ui.LoadingScreen;
import vn.vhn.amazon.kindle.chinesechess.ui.Menu;
import vn.vhn.amazon.kindle.chinesechess.ui.PlayingScreen;
import vn.vhn.amazon.kindle.chinesechess.ui.PostureSelectScreen;
import xqwlight.Position;
import xqwlight.Util;

// Referenced classes of package vn.vhn.amazon.kindle.chinesechess:
//            posCollection

public class Main extends AbstractKindlet
{
    public static Main theMain;
    public static final String STORE_NAME = "KindleChineseChess";
    public static final String ver = "0.0.9";
    public static final String date= "20110505";
    public static final int PANEL_NONE = -1;
    public static final int PANEL_PLAYING = 0;
    public static final int PANEL_ABOUT = 1;
    public static final int PANEL_HOW2PLAY = 2;
    public static final int PANEL_POSSEL = 3;
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int RS_DATA_LEN = 512;
    public static String levelName[] = {
        "lvlBeginner", "lvlEasy", "lvlMedium", "lvlHard", "lvlKingofChess"
    };
    public static byte[] rsData;
    public static int moveMode;
    public static int handicap;
    public static int level;
    public static int posmode;
    public static int posi;
    public static int posj;
    int currPanel;
    private PlayingScreen playingScr;
    private AboutScreen aboutScr;
    private HowToPlayScreen how2playScr;
    private PostureSelectScreen psScreen;
    private LoadingScreen loadScr;
    private ChineseBoard board;
    private KindletContext context;
    private Menu menu;
    boolean init;
    public static posCollection pc;
    public static final Position pos=new Position();
    public static List avaliableLangs;
    public static String currLang;
    public static ResourceBundle rLang;
    public Main()
    {

        avaliableLangs = new ArrayList();
        avaliableLangs.add("vi");
        avaliableLangs.add("en");
        
        playingScr = null;
        aboutScr = null;
        how2playScr = null;
        psScreen = null;
        init = false;
    }

    @Override public void create(final KindletContext context)
    {
        final Main me = this;
        theMain = this;
        loadScr = new LoadingScreen(this);
        context.getRootContainer().add(loadScr);
        currPanel = -1;
        this.context = context;
        rsData=null;
        try
        {
            SecureStorage rs = context.getSecureStorage();
            byte z[] = rs.getBytes("KindleChineseChess");
            if(z != null && z.length == 512)
                rsData = z;
            char[] oLang=rs.getChars("lang");
            if(oLang!=null){
                currLang = String.valueOf(oLang);
            }else{
                currLang = "en";
            }
        }
        catch(Exception e) {
        }
        if(!avaliableLangs.contains(currLang))
            currLang="en";
        rLang=ResourceBundle.getBundle(
                "vn/vhn/amazon/kindle/chinesechess/lang/lang",
                new Locale(currLang));
        for(int ii=0;ii<levelName.length;ii++)
            levelName[ii]=rLang.getString(levelName[ii]);
        if(rsData==null){
            rsData = new byte[512];
            for(int i = 0; i < 512; i++)
                rsData[i] = 0;
        }
        moveMode = Util.MIN_MAX(0, rsData[16], 2);
        handicap = Util.MIN_MAX(0, rsData[17], 3);
        level = Util.MIN_MAX(0, rsData[18], 4);
        posmode = rsData[19] == 0 ? 0 : 1;
        posi = rsData[20];
        posj = rsData[21];
        context.setMenu(menu = new Menu(this));
        new Thread(){
            @Override public void run(){
                try{
                    while(!(context.getRootContainer().isValid() && context.getRootContainer().isVisible())){
                        Thread.sleep(200);
                    }
                    Thread.sleep(1000);
                }catch(Exception e){}
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        Main.pc = new posCollection(context);
                        board = new ChineseBoard();
                        playingScr = new PlayingScreen(context, board);
                        aboutScr = new AboutScreen(me);
                        how2playScr = new HowToPlayScreen(me);
                        psScreen = PostureSelectScreen.Create(me, context);
                        /*pane = new KTextOptionPane();
                        KTextOptionListMenu langsList = new KTextOptionListMenu(rLang.getString("language"));
                        ListIterator langIterator = avaliableLangs.listIterator(avaliableLangs.size());
                        while (langIterator.hasPrevious())
                        {
                                String s = (String) langIterator.previous();
                                KTextOptionMenuItem item = new KTextOptionMenuItem(s);
                                langsList.add(item);
                                if(s.equals(currLang))
                                        langsList.setSelected(item);
                        }
                        langsList.addItemListener(new ItemListener(){
                                public void itemStateChanged(ItemEvent arg0) {
                                        if(arg0.getStateChange()==ItemEvent.SELECTED)
                                                context.getSecureStorage().putChars("lang", arg0.getItem().toString().toCharArray());
                                }
                        });
                        pane.addListMenu(langsList);
                        context.setTextOptionPane(pane);*/
                        setActivePanel(0);
                    }
                });
            }
        }.start();
    }

    @Override public void start()
    {
    }

    @Override public void destroy()
    {
        rsData[16] = (byte)moveMode;
        rsData[17] = (byte)handicap;
        rsData[18] = (byte)level;
        rsData[19] = (byte)posmode;
        rsData[20] = (byte)posi;
        rsData[21] = (byte)posj;
        SecureStorage rs;
        try
        {
            rs = context.getSecureStorage();
        }
        catch(Exception e)
        {
            return;
        }
        try
        {
            rs.remove("KindleChineseChess");
        }
        catch(Exception e) { }
        try
        {
            if(rsData[0]>0)
                rs.putBytes("KindleChineseChess", rsData);
        }
        catch(Exception e) { }
    }

    public void setActivePanel(int panel)
    {
        context.setMenu(null);
        switch(panel)
        {
        case PANEL_PLAYING: // '\0'
            if(context.getRootContainer().getComponent(0) != playingScr)
            {
                context.getRootContainer().remove(0);
                context.getRootContainer().add(playingScr);
                currPanel = panel;
                context.setMenu(menu);
                context.getRootContainer().requestFocus();
                playingScr.requestFocus();
            }
            break;

        case PANEL_ABOUT: // '\001'
            if(context.getRootContainer().getComponent(0) != aboutScr)
            {
                context.getRootContainer().remove(0);
                context.getRootContainer().add(aboutScr);
                context.getRootContainer().requestFocus();
                aboutScr.requestFocus();
                currPanel = panel;
            }
            break;

        case PANEL_HOW2PLAY: // '\002'
            if(context.getRootContainer().getComponent(0) != how2playScr)
            {
                context.getRootContainer().remove(0);
                context.getRootContainer().add(how2playScr);
                context.getRootContainer().requestFocus();
                how2playScr.requestFocus();
                currPanel = panel;
            }
            break;

        case PANEL_POSSEL: // '\003'
            if(context.getRootContainer().getComponent(0) != psScreen)
            {
                context.getRootContainer().remove(0);
                context.getRootContainer().add(psScreen);
                psScreen.onShow();
                context.getRootContainer().requestFocus();
                psScreen.requestFocus();
                psScreen.repaint();
                currPanel = panel;
            }
            break;
        }
        context.getRootContainer().validate();
        context.getRootContainer().invalidate();
        context.getRootContainer().repaint();
    }

    public void onDifficultChanging(int newLevel)
    {
        onDifficultChanging(newLevel, true);
    }

    public void onDifficultChanging(int newLevel, boolean newgame)
    {
        level = newLevel;
        posmode = 0;
        playingScr.onNewGame();
    }

    public void onHowToPlay()
    {
        setActivePanel(PANEL_HOW2PLAY);
    }

    public void onAbout()
    {
        setActivePanel(PANEL_ABOUT);
    }

    public void onPosSel()
    {
        setActivePanel(PANEL_POSSEL);
    }

    public void onPos(String fn, String name, int posi, int posj)
    {
        Main.posmode = 1;
        Main.posi = posi;
        Main.posj = posj;
        setActivePanel(0);
        playingScr.onPosMode(fn, name, posi, posj);
    }
    
    public void onClearRS(){
        SecureStorage rs = context.getSecureStorage();
        rs.clear();
        rs.putChars("lang", currLang.toCharArray());
    }
}
