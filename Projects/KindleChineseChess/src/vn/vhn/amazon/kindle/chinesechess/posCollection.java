// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   posCollection.java

package vn.vhn.amazon.kindle.chinesechess;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.security.SecureStorage;

public class posCollection
{

    public posCollection(KindletContext context)
    {
        aSolved = new boolean[itemCount.length][];
        nSolved = new int[itemCount.length];
        this.context = context;
        populateSolvedArray();
    }

    private void populateSolvedArray()
    {
        for(int i = 0; i < itemCount.length; i++)
        {
            nSolved[i] = 0;
            aSolved[i] = new boolean[itemCount[i]];
            for(int j = 0; j < itemCount[i]; j++)
                if(aSolved[i][j] = localIsSolved(i, j))
                    nSolved[i]++;

        }

    }

    private boolean localIsSolved(int name, int id)
    {
        char l[] = context.getSecureStorage().getChars("pos:".concat(itemName[name].concat(String.valueOf(id))));
        return l != null && l.length == 4 && l[0] == solved[0] && l[1] == solved[1] && l[2] == solved[2] && l[3] == solved[3];
    }

    public final String getFileName(int name, int id)
    {
        return "/pos/".concat(itemName[name].concat(" ").concat(String.valueOf(id + 1))).concat(".vh");
    }
    public void setAllUnsolved(){
        Main.theMain.onClearRS();
        populateSolvedArray();
    }
    public int setSolved(int name, int id)
    {
        if(aSolved[name][id])
            return -1;
        try
        {
            context.getSecureStorage().putChars("pos:".concat(itemName[name]).concat(String.valueOf(id)), solved);
            aSolved[name][id] = true;
            nSolved[name]++;
        }
        catch(Exception exp)
        {
            return 1;
        }
        return 0;
    }

    public int setUnsolved(int name, int id)
    {
        if(!aSolved[name][id])
            return -1;
        try
        {
            aSolved[name][id] = false;
            nSolved[name]--;
            context.getSecureStorage().remove("pos:".concat(itemName[name]).concat(String.valueOf(id)));
        }
        catch(Exception e)
        {
            return 1;
        }
        return 0;
    }

    public static final int itemCount[] = {
        3, 4, 5, 6, 6, 6, 6, 5, 8, 10, 
        8, 10, 3, 3, 4, 3, 5, 5, 5, 4, 
        4, 5, 5, 8, 16, 16, 8, 16, 16, 8, 
        3, 2, 4, 2, 3, 4, 8, 3, 10, 6, 
        3, 3, 3, 3, 3, 3, 3, 3
    };
    public static final String itemName[] = {
        "Angler Horse", "Cannon Smothered", "Cannon-Pawn", "Chariot and Cannon", "Chariot and Double Cannons", "Chariot and Double Horses", "Chariot and Horse", "Chariot and Pawn", "Chariot, Cannon and Pawn", "Chariot, Horse and Cannon", 
        "Chariot, Horse and Pawn", "Chariot, Horse, Cannon and Pawn", "Chariot-Cannon Discover", "Chariot-Horse (BA-Huang Horse)", "Chariot-Horse Zugzwang", "Double Cannon", "Double Cannons", "Double Cannons and Pawn", "Double Chariots", "Double Chariots and Pawn", 
        "Double Devils Knocking at Door", "Double Horses", "Double Horses and Pawn", "Drills for Cannon-related", "Drills for Chariot and Cannon Combined", "Drills for Chariot and Horse", "Drills for Chariot Related", "Drills for Chariot, Cannon, Horse and Pawn Combined", "Drills for Horse and Cannon Combined", "Drills for Horse-Related", 
        "Elbow Horse", "Exposed Cannon", "Face-to-Face Laughing", "Fishing the Moon Under Deep Sea", "Flanking Trio", "Heavenly and Earthly Cannons", "Horse and Cannon", "Horse - Cannon", "Horse, Cannon and Pawn", "Horse-Pawn", 
        "Iron-Bolt", "Octagonal Horse", "Palcorner Horse", "Repatriation of Buddha", "Simultaneous Double", "Stalemate", "Throat-cutting", "Tiger Silhouette"
    };
    public boolean aSolved[][];
    public int nSolved[];
    private KindletContext context;
    char solved[] = {
        'v', 'h', 's', 'v'
    };
}
