/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model.networking;

import hr.algebra.model.ChatMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Nina
 */
public interface MessengerService extends Remote{
    
    void sendMessage(ChatMessage message) throws RemoteException;
    List<ChatMessage> getAllMessages() throws RemoteException;
    ChatMessage getlastChatMessage() throws RemoteException;
}
