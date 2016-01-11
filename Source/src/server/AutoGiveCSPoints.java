/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.MapleCharacter;
import java.lang.ref.WeakReference;

/**
 *
 * @author ms890110
 */
public class AutoGiveCSPoints {
    
    private long lasttime = 0;
    private final long period = 1000* 60 * 60;
    private WeakReference<MapleCharacter> chr = null;
    
    public AutoGiveCSPoints(MapleCharacter chr) {
       this.chr = new WeakReference<>(chr);
    }
    
    public boolean checkTime() {
        return (System.currentTimeMillis() - lasttime) > period;
    }
    
    public void checkGivePoints() {
        long current = System.currentTimeMillis();
        if(lasttime == 0)
            lasttime = current;
        else {
            if ( current - lasttime > period) {
                if( chr.get() != null )
                    chr.get().modifyCSPoints(1,  Randomizer.nextInt() % 10 + 1);
                lasttime = current;
            }
        }
        
       
    }
    
}
