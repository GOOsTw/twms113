package constants;

public class MapConstants {
    
    public static boolean isBlockFM(final int mapid) {
        if(isEventMap(mapid))
            return true;
        if( mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100))
            return true;
        if ( mapid / 10000 == 92502 )
            return true;
        switch(mapid) {
            case 702060000:
                return true;
            default:
                return false;
        }
    }

    public static boolean isCar(final int mapid) {
        switch (mapid) {
            case 980000000:
            case 980030000:
                return true;
        }
        return false;
    }
    
    public static boolean isStartingEventMap(final int mapid) {
        switch (mapid) {
            case 109010000:
            case 109020001:
            case 109030001:
            case 109030101:
            case 109030201:
            case 109030301:
            case 109030401:
            case 109040000:
            case 109060001:
            case 109060002:
            case 109060003:
            case 109060004:
            case 109060005:
            case 109060006:
            case 109080000:
            case 109080001:
            case 109080002:
            case 109080003:
                return true;
        }
        return false;
    }

    public static boolean isEventMap(final int mapid) {
        return mapid >= 109010000 && mapid < 109050000 || mapid > 109050001 && mapid < 109090000;
    }
}
