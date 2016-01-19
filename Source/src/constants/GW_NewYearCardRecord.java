package constants; 

import tools.data.output.MaplePacketLittleEndianWriter; 

/** 
 * GW_NewYearCardRecord 
 *  
 * @author Eric 
 */ 
public class GW_NewYearCardRecord { 
    public int m_dwSN; 
    public int m_dwSenderID; 
    public String m_sSenderName; 
    public boolean m_bSenderDiscardCard; 
    public long m_dateSent; 
    public int m_dwReceiverID; 
    public String m_sReceiverName; 
    public boolean m_bReceiverDiscardCard; 
    public boolean m_bReceiverReceivedCard; 
    public long m_dateReceived; 
    public String m_sContent; 
     
    public void Encode(MaplePacketLittleEndianWriter mplew) { 
        mplew.writeInt(this.m_dwSN);//4300000 
        mplew.writeInt(this.m_dwSenderID); 
        mplew.writeMapleAsciiString(this.m_sSenderName); 
        mplew.write(this.m_bSenderDiscardCard ? 1 : 0); 
        mplew.writeLong(this.m_dateSent); 
        mplew.writeInt(this.m_dwReceiverID); 
        mplew.writeMapleAsciiString(this.m_sReceiverName); 
        mplew.write(this.m_bReceiverDiscardCard ? 1 : 0); 
        mplew.write(this.m_bReceiverReceivedCard ? 1 : 0); 
        mplew.writeLong(this.m_dateReceived); 
        mplew.writeMapleAsciiString(this.m_sContent); 
    } 
}  