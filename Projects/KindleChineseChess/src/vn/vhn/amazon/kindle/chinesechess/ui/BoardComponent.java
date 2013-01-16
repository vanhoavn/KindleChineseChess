// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BoardComponent.java

package vn.vhn.amazon.kindle.chinesechess.ui;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.GestureEvent;
import com.amazon.kindle.kindlet.input.Gestures;
import com.amazon.kindle.kindlet.ui.*;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import vn.vhn.amazon.kindle.chinesechess.Board.ChineseBoard;
import vn.vhn.amazon.kindle.chinesechess.Lang;
import vn.vhn.amazon.kindle.chinesechess.Main;
//import vn.vhn.amazon.kindle.chinesechess.posCollection;
import xqwlight.Position;
import xqwlight.Search;
import xqwlight.Util;


public final class BoardComponent extends JComponent
    implements KeyListener
{

    String mMouseStatus = "EMPTY";
    public BoardComponent(KindletContext context, JLabel label, ChineseBoard board)
    {
        //retractData = new byte[512];
        pos = Main.pos;
        search = new Search(pos, 12);
        normalWidth = getWidth();
        normalHeight = getHeight();
        phase = 0;
        init = false;
        imgPieces = new Image[24];
        movMode = 1;
        kMap = new char[256];
        resourceLoaded = false;
        cont = context.getRootContainer();
        infoLabel = label;
        labelFont = new Font(null, 2, 14);
        colLine = context.getUIResources().getBackgroundColor(com.amazon.kindle.kindlet.ui.KindletUIResources.KColorName.GRAY_13);
        colMLine = context.getUIResources().getBackgroundColor(com.amazon.kindle.kindlet.ui.KindletUIResources.KColorName.BLACK);
        colBg = context.getUIResources().getBackgroundColor(com.amazon.kindle.kindlet.ui.KindletUIResources.KColorName.WHITE);
        colLabel = context.getUIResources().getBackgroundColor(com.amazon.kindle.kindlet.ui.KindletUIResources.KColorName.BLACK);
        ResetSquareSize();
        enableEvents(48L);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
        onShow();
    }

    public void onShow()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run()
            {
                initResource();
                load();
                ResetSKey();
                resourceLoaded = true;
                repaint();
                redrawMe();
            }
        });
    }

    public void onNewGame()
    {
        Main.rsData[0] = 0;
        load();
        ResetSKey();
        invalidate();
        repaint();
        redrawMe(true);
    }

    public void onPosMode(String fn, String name, int posi, int posj)
    {
        try
        {
            Main.rsData[0] = 0;
            cursorX = cursorY = oldSQ = -1;
            sqSelected = mvLast = 0;
            String r = pos.fromStream(fn);
            if(r.startsWith("@English:\n\r") || r.startsWith("@English:\r\n"))
                r = r.substring(11);
            //System.arraycopy(Main.rsData, 0, retractData, 0, 512);
            Main.rsData[0] = (byte)(pos.sdPlayer + 1);
            System.arraycopy(pos.squares, 0, Main.rsData, 256, 256);
            phase = 0;
            invalidate();
            repaint();
            redrawMe(true);
            try{
                KOptionPane.showMessageDialog(this, r, Main.rLang.getString("Objective"), KOptionPane.PLAIN_MESSAGE);
            }catch(Exception e) { }
            ResetSKey();
        }
        catch(IOException e)
        {
            infoLabel.setText(e.toString());
        }
        infoLabel.invalidate();
        infoLabel.repaint();
    }

    void load()
    {
        cursorX = cursorY = oldSQ = -1;
        sqSelected = mvLast = 0;
        if(Main.rsData[0] == 0)
        {
            pos.fromFen(Position.STARTUP_FEN[Main.handicap]);
        } else
        {
            pos.clearBoard();
            for(int sq = 0; sq < 256; sq++)
            {
                int pc = Main.rsData[sq + 256];
                if(pc > 0)
                    pos.addPiece(sq, pc);
            }

            if(Main.rsData[0] == 2)
                pos.changeSide();
            pos.setIrrev();
        }
        //System.arraycopy(Main.rsData, 0, retractData, 0, 512);
        phase = 0;
        if(pos.sdPlayer != 0 ? Main.moveMode == 0 : Main.moveMode == 1)
            new Thread(){
                @Override public void run()
                {
                    while(phase == 0) 
                        try
                        {
                            Thread.sleep(100L);
                        }
                        catch(InterruptedException e) { }
                    responseMove();
                    int response = pos.inCheck() ? 7 : ((int) (pos.captured() ? 5 : 3));
                    getResult(response);
                }
            }.start();
        else{
            int response = pos.inCheck() ? 7 : ((int) (pos.captured() ? 5 : 3));
            getResult(response);
        }
    }

    boolean responseMove()
    {
        if(getResult())
            return false;
        if(Main.moveMode == 2)
        {
            return true;
        } else
        {
            int response=0;
            try {
                phase = 2;
                for(int i=0;i<pos.squares_current.length;i++)
                    pos.squares_current[i]=pos.squares[i];
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        infoLabel.setText(Main.rLang.getString("thinking"));
                        infoLabel.invalidate();
                        infoLabel.repaint();
                        repaint();
                        redrawMe();
                    }
                });
                int cMove=Main.posmode == 0 ? (1000 + 500 * Main.level) : 3000;
                mvLast = search.searchMain(cMove);
                pos.makeMove(mvLast);
                response = pos.inCheck() ? 7 : ((int) (pos.captured() ? 5 : 3));
                phase = 1;
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        repaint();
                        ResetSKey();
                        infoLabel.setText(Main.rLang.getString("yourTurn"));
                        infoLabel.invalidate();
                        infoLabel.repaint();
                        redrawMe(true);
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(BoardComponent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(BoardComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            return !getResult(response);
        }
    }

    private boolean getResult()
    {
        return getResult(Main.moveMode != 2 ? -2 : -1);
    }
    
    private void onMatchEnd(String mesg)
    {
        try
        {
            if(KOptionPane.showConfirmDialog(this,
                    mesg.concat("\n\n").concat(Main.rLang.getString("doYouWantToStartANewGame")),
                    Main.rLang.getString("gameOver"),
                    KOptionPane.CANCEL_OK_OPTION,KOptionPane.PLAIN_MESSAGE)==KOptionPane.OK_OPTION){
                if(Main.posmode==1){
                    Main.theMain.onPosSel();
                }else{
                    onNewGame();
                }
            }
        }
        catch(Exception e) {
            infoLabel.setText("::".concat(e.getMessage()));
            infoLabel.invalidate();
            infoLabel.repaint();
        }
    }

    private boolean getResult(int response)
    {
        if(pos.isMate())
        {
            message = response >= 0 ? Main.rLang.getString("youLost") : Main.rLang.getString("youWon");
            int i;
            if(Main.posmode != 0 && response < 0){
                Main.rsData[0] = 0;
                i = Main.pc.setSolved(Main.posi, Main.posj);
            }
            onMatchEnd(message);
            return true;
        }
        int vlRep = pos.repStatus(3);
        if(vlRep > 0)
        {
            vlRep = response >= 0 ? -pos.repValue(vlRep) : pos.repValue(vlRep);
            message = vlRep <= 9800 ? (vlRep >= -9800 ? Main.rLang.getString("gameDraw") : Main.rLang.getString("youWon")) : Main.rLang.getString("youLost");
            if(Main.posmode != 0 && vlRep < -9800){
                Main.rsData[0] = 0;
                Main.pc.setSolved(Main.posi, Main.posj);
            }
            onMatchEnd(message);
            return true;
        }
        Position _tmp = pos;
        if(pos.moveNum >= 2048)
        {
            message = Main.rLang.getString("gameDrawTooManyMove");
            onMatchEnd(message);
            return true;
        }
        if(response != -2)
        {
            //System.arraycopy(Main.rsData, 0, retractData, 0, 512);
            Main.rsData[0] = (byte)(pos.sdPlayer + 1);
            System.arraycopy(pos.squares, 0, Main.rsData, 256, 256);
        }
        return false;
    }

    private boolean addMove(int mv)
    {
        if(pos.legalMove(mv) && pos.makeMove(mv))
        {
            pos.rmoveNum = 0;
            sqSelected = 0;
            oldSQ=-1;
            mvLast = mv;
            return true;
        } else
        {
            return false;
        }
    }

    private void initResource()
    {
        MediaTracker mt = new MediaTracker(this);
        String dir = "res/pieces/70/";
        String ext = ".png";
        for(int i = 0; i < IMAGE_NAME.length; i++)
            if(IMAGE_NAME[i] != null)
            {
                Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource(dir.concat(IMAGE_NAME[i]).concat(ext))).getScaledInstance(squareSize - 3, squareSize - 3, 4);
                imgPieces[i] = img;
                mt.addImage(img, 0);
            } else
            {
                imgPieces[i] = null;
            }

        try
        {
            mt.waitForAll();
        }
        catch(InterruptedException e) { }
    }

    public void ResetSquareSize()
    {
        squareSize = Math.min(Main.SCREEN_SIZE.width / 10, Main.SCREEN_SIZE.height / 15);
        boardWidth = 10 * squareSize + 2;
        boardHeight = 11 * squareSize + 2;
    }

    @Override public Dimension getPreferredSize()
    {
        return new Dimension(boardWidth, boardHeight);
    }

    @Override public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    @Override public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }
    
    
    private void DrawBoardPlus(Graphics g, int x, int y, int size, int w,
            boolean drawLeft, boolean drawRight){
        if(drawLeft){
            g.drawLine(x-w-size, y+w, x-w, y+w);
            g.drawLine(x-w-size, y-w, x-w, y-w);
            g.drawLine(x-w, y-w-size, x-w, y-w);
            g.drawLine(x-w, y+w+size, x-w, y+w);
        }
        
        if(drawRight){
            g.drawLine(x+w+size, y+w, x+w, y+w);
            g.drawLine(x+w+size, y-w, x+w, y-w);
            g.drawLine(x+w, y-w-size, x+w, y-w);
            g.drawLine(x+w, y+w+size, x+w, y+w);
        }
}
    private void DrawBoard(Graphics g)
    {
        g.setColor(colLine);
        g.drawLine(squareSize, squareSize, squareSize, squareSize * 10);
        g.drawLine(squareSize * 9, squareSize, squareSize * 9, squareSize * 10);
        g.drawLine(squareSize * 4, squareSize, squareSize * 6, squareSize * 3);
        g.drawLine(squareSize * 4, squareSize * 3, squareSize * 6, squareSize);
        g.drawLine(squareSize * 4, squareSize * 8, squareSize * 6, squareSize * 10);
        g.drawLine(squareSize * 4, squareSize * 10, squareSize * 6, squareSize * 8);
        int i = 1;
        for(int p = 2 * squareSize; i < 9; p += squareSize)
        {
            g.drawLine(p, squareSize, p, squareSize * 5);
            g.drawLine(p, squareSize * 6, p, squareSize * 10);
            i++;
        }

        i = 0;
        for(int p = squareSize; i < 10; p += squareSize)
        {
            g.drawLine(squareSize, p, squareSize * 9, p);
            i++;
        }
        
        final int[] pX={1,1,7,7,0,2,4,6,8,0,2,4,6,8};
        final int[] pY={2,7,2,7,3,3,3,3,3,6,6,6,6,6};
        
        for(i=0;i<pX.length;i++){
            DrawBoardPlus(g,(pX[i]+1)*squareSize,(pY[i]+1)*squareSize,squareSize/5,squareSize/10,pX[i]>0,pX[i]<8);
        }
    }

    public void ResetSKey()
    {
//        char c = 'A';
//        if(sqSelected <= 0)
//        {
//            for(int sq = 0; sq < 256; sq++)
//                if(Position.IN_BOARD(sq))
//                {
//                    int pc = pos.squares[sq];
//                    if(pc > 0 && (Position.SIDE_TAG(pos.sdPlayer) & pc) != 0)
//                    {
//                        kMap[sq] = c++;
//                    } else
//                    {
//                        kMap[sq] = ' ';
//                    }
//                }
//
//        } else
//        {
//            for(int sq = 0; sq < 256; sq++)
//            {
//                if(!Position.IN_BOARD(sq))
//                    continue;
//                if(pos.legalMove(Position.MOVE(sqSelected, sq)))
//                {
//                    kMap[sq] = c++;
//                } else
//                {
//                    kMap[sq] = ' ';
//                }
//            }
//
//        }
        for(int sq = 0; sq < 256; sq++)
            {
                if(!Position.IN_BOARD(sq))
                    continue;
                kMap[sq] = ' ';
            }
    }

    private void drawSquare(Graphics g, Image image, int sq)
    {
        int sqFlipped = Main.moveMode != 1 ? sq : Position.SQUARE_FLIP(sq);
        int sqX = (Position.FILE_X(sqFlipped) - 3) * squareSize + squareSize / 2;
        int sqY = (Position.RANK_Y(sqFlipped) - 3) * squareSize + squareSize / 2;
        if(image != null)
        {
            g.drawImage(image, sqX, sqY, new ImageObserver() {
                @Override public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
                {
                    repaint();
                    return true;
                }
            }
);
            sqX += 5;
            sqY += squareSize - 7;
        } else
        {
            sqX += squareSize / 2 - 4;
            sqY += squareSize / 2 + 5;
        }
        if(movMode == 1 && kMap[sq] != ' ')
        {
            g.setColor(Color.BLACK);
            g.setFont(labelFont);
            for(int dx = -2; dx <= 2; dx++)
            {
                for(int dy = -2; dy <= 2; dy++)
                    g.drawString(String.valueOf(kMap[sq]), sqX + dx, sqY + dy);

            }

            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(kMap[sq]), sqX, sqY);
        }
    }

    private void drawMove(Graphics g, int sq1, int sq2)
    {
        int sqFlipped = Main.moveMode != 1 ? sq1 : Position.SQUARE_FLIP(sq1);
        int sqX1 = (Position.FILE_X(sqFlipped) - 3) * squareSize + squareSize;
        int sqY1 = (Position.RANK_Y(sqFlipped) - 3) * squareSize + squareSize;
        sqFlipped = Main.moveMode != 1 ? sq2 : Position.SQUARE_FLIP(sq2);
        int sqX2 = (Position.FILE_X(sqFlipped) - 3) * squareSize + squareSize;
        int sqY2 = (Position.RANK_Y(sqFlipped) - 3) * squareSize + squareSize;
        g.setColor(colMLine);
        //g.setXORMode(colMLine);
        for(int i = -2; i <= 2; i++)
            for(int j = -2; j <= 2; j++)
                g.drawLine(sqX1 + i, sqY1 + j, sqX2 + i, sqY2 + j);

        //g.setXORMode(Color.WHITE);
    }

    private void drawSelected(Graphics g, int sq)
    {
        int sqFlipped = Main.moveMode != 1 ? sq : Position.SQUARE_FLIP(sq);
        int sqX = (Position.FILE_X(sqFlipped) - 3) * squareSize + squareSize / 2;
        int sqY = (Position.RANK_Y(sqFlipped) - 3) * squareSize + squareSize / 2;
        g.setColor(colMLine);
        g.drawRoundRect(sqX, sqY, squareSize - 4, squareSize - 4, 10, 10);
    }

    private void drawCursor(Graphics g, int sq)
    {
        int sqFlipped = Main.moveMode != 1 ? sq : Position.SQUARE_FLIP(sq);
        int sqX = (Position.FILE_X(sqFlipped) - 3) * squareSize + squareSize / 2;
        int sqY = (Position.RANK_Y(sqFlipped) - 3) * squareSize + squareSize / 2;
        g.setColor(colMLine);
        g.drawRoundRect(sqX, sqY, squareSize - 4, squareSize - 4, 10, 10);
    }

    int oldSQ;
    @Override public void paint(Graphics g)
    {
        if(!resourceLoaded){
            g.drawString("!resourceLoaded", 30, 230);
            return;
        }
        g.setColor(colBg);
        g.fillRect(0, 0, squareSize * 10, squareSize * 11);
//        g.setColor(Color.BLACK);
//            g.drawString(mMouseStatus.concat("  ".concat(String.valueOf(sqSelected)))
//                    .concat("  ".concat(String.valueOf(mvLast))), 50, 250);
        DrawBoard(g);
        for(int sq = 0; sq < 256; sq++)
            if(Position.IN_BOARD(sq))
                drawSquare(g, imgPieces[phase==2?pos.squares_current[sq]:pos.squares[sq]], sq);

        int sqSrc = 0;
        int sqDst = 0;
        if(mvLast > 0)
        {
            sqSrc = Position.SRC(mvLast);
            sqDst = Position.DST(mvLast);
            drawMove(g, sqSrc, sqDst);
        }
        if(sqSelected > 0){
            drawSelected(g, sqSelected);
            drawCursor(g, sqSelected);
        }
        if(oldSQ>=0){
            drawCursor(g, oldSQ);
        }
    }

    private void clickSquare()
    {
        clickSquare(Position.COORD_XY(cursorX + 3, cursorY + 3));
    }
    
    private void clickSquare(int sq)
    {
        if(sq<0 || sq>255){
            oldSQ = -1;
            sqSelected = sq;
            ResetSKey();
        }else{
            if(Main.moveMode == 1)
                sq = Position.SQUARE_FLIP(sq);
            int pc = pos.squares[sq];
            if((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0)
            {
                oldSQ = -1;
                sqSelected = sq;
                ResetSKey();
            } else
            if(sqSelected > 0) {
                if(sq!=oldSQ){
                    oldSQ=sq;
                }else if(addMove(Position.MOVE(sqSelected, sq)))
                {
                    new Thread() {
                        @Override
                        public void run() {
                            if(!responseMove())
                             {
                                 Main.rsData[0] = 0;
                                 phase = 3;
                             }
                        }
                    }.start();
                    return;//don't draw itself
                } else
                {
                    sqSelected = 0;
                    infoLabel.setText(Main.rLang.getString("invalidMove"));
                    infoLabel.invalidate();
                    infoLabel.repaint();
                }
            }
        }
        repaint();
        redrawMe(false);
    }
    
    void onUndo(){
            if(pos.moveNum > 1)
            {
                pos.rmvList[pos.rmoveNum++] = pos.mvList[pos.moveNum - 1];
                pos.rmvList[pos.rmoveNum++] = pos.mvList[pos.moveNum - 2];
                pos.undoMakeMove();
                pos.undoMakeMove();
                mvLast = pos.mvList[pos.moveNum - 1];
                sqSelected = 0;
                ResetSKey();
                Main.rsData[0] = (byte)(pos.sdPlayer + 1);
                System.arraycopy(pos.squares, 0, Main.rsData, 256, 256);
            }
    }
    
    void onRedo(){
            if(pos.rmoveNum > 1)
            {
                pos.makeMove(pos.rmvList[pos.rmoveNum - 1]);
                pos.makeMove(pos.rmvList[pos.rmoveNum - 2]);
                mvLast = pos.mvList[pos.rmoveNum - 1];
                sqSelected = 0;
                pos.rmoveNum -= 2;
                int response = pos.inCheck() ? 7 : ((int) (pos.captured() ? 5 : 3));
                repaint();
                ResetSKey();
                Main.rsData[0] = (byte)(pos.sdPlayer + 1);
                System.arraycopy(pos.squares, 0, Main.rsData, 256, 256);
                getResult(response);
            }
    }
    
    @Override public synchronized void keyPressed(KeyEvent e)
    {
    }
    /*
     * @Override public synchronized void keyPressed(KeyEvent e)
    {
        if(e.isConsumed() || e.getID() == KindleKeyCodes.VK_BACK || phase == 2)
            return;
        int deltaX = 0;
        int deltaY = 0;
        if(e.getKeyChar() == 'z' || e.getKeyChar() == 'Z')
        {
            redrawMe(true);
            e.consume();
        } else
        if(e.getKeyCode() == ' ')
        {
            movMode = movMode != 0 ? 0 : 1;
            e.consume();
        } else
        if(e.getKeyCode() == KindleKeyCodes.VK_TURN_PAGE_BACK)
        {
            if(pos.moveNum > 1)
            {
                pos.rmvList[pos.rmoveNum++] = pos.mvList[pos.moveNum - 1];
                pos.rmvList[pos.rmoveNum++] = pos.mvList[pos.moveNum - 2];
                pos.undoMakeMove();
                pos.undoMakeMove();
                mvLast = pos.mvList[pos.moveNum - 1];
                sqSelected = 0;
                ResetSKey();
                Main.rsData[0] = (byte)(pos.sdPlayer + 1);
                System.arraycopy(pos.squares, 0, Main.rsData, 256, 256);
            }
            e.consume();
        } else
        if(e.getKeyCode() == KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE
                || e.getKeyCode() == KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE)
        {
            if(pos.rmoveNum > 1)
            {
                pos.makeMove(pos.rmvList[pos.rmoveNum - 1]);
                pos.makeMove(pos.rmvList[pos.rmoveNum - 2]);
                mvLast = pos.mvList[pos.rmoveNum - 1];
                sqSelected = 0;
                pos.rmoveNum -= 2;
                int response = pos.inCheck() ? 7 : ((int) (pos.captured() ? 5 : 3));
                repaint();
                ResetSKey();
                Main.rsData[0] = (byte)(pos.sdPlayer + 1);
                System.arraycopy(pos.squares, 0, Main.rsData, 256, 256);
                getResult(response);
            }
            e.consume();
        } else
        if(movMode == 0)
        {
            switch(e.getKeyCode())
            {
            case KindleKeyCodes.VK_FIVE_WAY_LEFT: // '!'
                deltaY = -1;
                e.consume();
                break;

            case KindleKeyCodes.VK_FIVE_WAY_RIGHT: // '"'
                deltaY = 1;
                e.consume();
                break;

            case KindleKeyCodes.VK_FIVE_WAY_UP: // '%'
                deltaX = -1;
                e.consume();
                break;

            case KindleKeyCodes.VK_FIVE_WAY_DOWN: // '\''
                deltaX = 1;
                e.consume();
                break;

            case KindleKeyCodes.VK_FIVE_WAY_SELECT: 
                clickSquare();
                e.consume();
                break;
            }
            cursorX = (cursorX + deltaX + 9) % 9;
            cursorY = (cursorY + deltaY + 10) % 10;
        } else
        {
            switch(e.getKeyCode())
            {
            case '\n': // '\n'
            case KindleKeyCodes.VK_FIVE_WAY_LEFT: // '!'
            case KindleKeyCodes.VK_FIVE_WAY_RIGHT: // '"'
            case KindleKeyCodes.VK_FIVE_WAY_UP: // '%'
            case KindleKeyCodes.VK_FIVE_WAY_DOWN: // '\''
            case KindleKeyCodes.VK_FIVE_WAY_SELECT: 
                sqSelected = 0;
                ResetSKey();
                e.consume();
                break;
            }
            int c = e.getKeyChar();
            if(c >= 'a' && c <= 'z')
                c -= 32;
            if(c >= 'A' && c <= 'Z')
            {
                int sq = 0;
                do
                {
                    if(sq >= 256)
                        break;
                    if(kMap[sq] == c)
                    {
                        clickSquare(sq);
                        e.consume();
                        break;
                    }
                    sq++;
                } while(true);
            }
        }
        if(e.isConsumed()){
            invalidate();
            repaint();
        }
    }*/

    @Override public void keyTyped(KeyEvent keyevent)
    {
    }

    @Override public void keyReleased(KeyEvent keyevent)
    {
    }

    private void redrawMe()
    {
        redrawMe(false);
    }

    private void redrawMe(boolean full)
    {
        cont.validate();
        KRepaintManager.getInstance().repaint(cont, full);
    }

    private static final int PHASE_LOADING = 0;
    private static final int PHASE_WAITING = 1;
    private static final int PHASE_THINKING = 2;
    private static final int PHASE_EXITTING = 3;
    private static final int COMPUTER_BLACK = 0;
    private static final int COMPUTER_RED = 1;
    private static final int COMPUTER_NONE = 2;
    private static final int RESP_HUMAN_SINGLE = -2;
    private static final int RESP_HUMAN_BOTH = -1;
    private static final int RESP_CLICK = 0;
    private static final int RESP_ILLEGAL = 1;
    private static final int RESP_MOVE = 2;
    private static final int RESP_MOVE2 = 3;
    private static final int RESP_CAPTURE = 4;
    private static final int RESP_CAPTURE2 = 5;
    private static final int RESP_CHECK = 6;
    private static final int RESP_CHECK2 = 7;
    private static final int RESP_WIN = 8;
    private static final int RESP_DRAW = 9;
    private static final int RESP_LOSS = 10;
    private int squareSize;
    private int boardWidth;
    private int boardHeight;
    private static Color colLine;
    private static Color colMLine;
    private static Color colBg;
    private static Color colLabel;
    //byte retractData[];
    private Position pos;
    private Search search;
    private String message;
    private int cursorX;
    private int cursorY;
    private int sqSelected;
    private int mvLast;
    private int normalWidth;
    private int normalHeight;
    volatile int phase;
    private boolean init;
    private static final String IMAGE_NAME[] = {
        null, null, null, null, null, null, null, null, "tuong0", "si0", 
        "voi0", "ma0", "xe0", "phao0", "tot0", null, "tuong1", "si1", "voi1", "ma1", 
        "xe1", "phao1", "tot1", null
    };
    private static final String selKey = "abcdefghijklmnopqrstuvwxyz";
    private Image imgPieces[];
    JLabel infoLabel;
    private final int MODE_DIR = 0;
    private final int MODE_QWERT = 1;
    private int movMode;
    private char kMap[];
    private Font labelFont;
    private Container cont;
    private boolean resourceLoaded;
    int oldX, oldY;
    protected void processMouseEvent(MouseEvent e)
    {
        if(e.getButton() == MouseEvent.BUTTON1){
            switch(e.getID()){
                case MouseEvent.MOUSE_PRESSED:
                    oldX=e.getX();
                    oldY=e.getY();
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    int dx=e.getX()-oldX, dy=e.getY()-oldY;
                    if(Math.abs(dx)<this.getWidth()/10 && Math.abs(dy)<this.getHeight()/10){
                        cursorX = (e.getX()-squareSize/2)/squareSize;
                        cursorY = (e.getY()-squareSize/2)/squareSize;
                        clickSquare();
                    }else{
                        if(dx<-this.getWidth()/3) onRedo();
                        else if(dx>this.getWidth()/3) onUndo();
                        this.invalidate();
                        this.repaint();
                        redrawMe(true);
                    }
                    e.consume();
                    break;
            }
        }
        if(!e.isConsumed())
            super.processMouseEvent(e);
    }


}
