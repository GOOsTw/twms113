/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.mina;

import client.MapleClient;
import handling.MapleServerHandler;
import handling.RecvPacketOpcode;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import tools.MapleAESOFB;


import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import tools.FilePrinter;
import tools.HexTool;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;

public class MaplePacketDecoder extends CumulativeProtocolDecoder {

    private static final String DECODER_STATE_KEY = MaplePacketDecoder.class.getName() + ".STATE";

    private static class DecoderState {

        public int packetlength = -1;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);
        if (decoderState == null) {
            decoderState = new DecoderState();
            session.setAttribute(DECODER_STATE_KEY, decoderState);
        }
        if (in.remaining() >= 4 && decoderState.packetlength == -1) {
            int packetHeader = in.getInt();
            if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                session.close(true);
                return false;
            }
            decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
        } else if (in.remaining() < 4 && decoderState.packetlength == -1) {
            return false;
        }
        if (in.remaining() >= decoderState.packetlength) {
            byte decryptedPacket[] = new byte[decoderState.packetlength];
            in.get(decryptedPacket, 0, decoderState.packetlength);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            //MapleCustomEncryption.decryptData(decryptedPacket);
            out.write(decryptedPacket);
            
            //封包輸出
            int packetLen = decryptedPacket.length;
            short pHeader = new LittleEndianAccessor(new ByteArrayByteStream(decryptedPacket)).readShort();
            String op = RecvPacketOpcode.nameOf(pHeader);
            if (MapleServerHandler.isLogPackets && !RecvPacketOpcode.isSpamHeader(RecvPacketOpcode.valueOf(op))) {
                String tab = "";
                for (int i = 4; i > op.length() / 8; i--) {
                    tab += "\t";
                }
                String t = packetLen >= 10 ? packetLen >= 100 ? packetLen >= 1000 ? "" : " " : "  " : "   ";
                final StringBuilder sb = new StringBuilder("[接收]");
                if(client.getAccountName() != null) {
                    if(!client.getAccountName().equals(""))
                        sb.append("帳號 :").append(client.getAccountName());
                    if(client.getPlayer()!= null)
                        sb.append("角色 :").append(client.getPlayer().getName());
                }
                sb.append("\t").append(op).append(tab).append("\t包頭:").append(HexTool.getOpcodeToString(pHeader)).append(t).append("[").append(packetLen).append("字元]");
                System.out.println(sb.toString());
                sb.append("\r\n\r\n").append(HexTool.toString(decryptedPacket)).append("\r\n").append(HexTool.toStringFromAscii(decryptedPacket));
                FilePrinter.print("PacketRecv.txt", "\r\n\r\n" + sb.toString() + "\r\n\r\n");
            }
            
            
            
            return true;
        }
        return false;
    }
}
