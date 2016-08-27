/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login.handler;

/**
 * Response of MapleClient.Login
 * @author Tasi
 */
public enum LoginResponse {
    
    LOGIN_SUCCESS(0x0),
    NOTHING(0x1),
    ONLINE(0x2),
    ACCOUNT_BLOCKED(0x3),
    WRONG_PASSWORD(0x4),
    NOT_REGISTERED(0x5),
    ALREADY_LOGININ(0x7),
    SYSTEM_ERROR(0x8),
    SYSYEM_ERROR2(0x9);
    
    private final int value;
    
    private LoginResponse(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return this.value;
    }
}
