/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model.networking;

import hr.algebra.model.ChatMessage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class MessengerServiceImpl implements MessengerService {

    private  List<ChatMessage> messageList = new ArrayList<>();
    
    @Override
    public void sendMessage(ChatMessage message) throws RemoteException {
        messageList.add(message);
    }

    @Override
    public List<ChatMessage> getAllMessages() throws RemoteException {
        return messageList;
    }

    @Override
    public ChatMessage getlastChatMessage() throws RemoteException {
        return messageList.get(messageList.size() - 1);
    }
    
}
