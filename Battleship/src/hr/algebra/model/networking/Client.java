/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model.networking;

import hr.algebra.model.Cell;
import hr.algebra.repo.Repository;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nina
 */
public class Client extends Thread{

    private Socket clientSocket;
    
    @Override
    public void run() {
        
            OutputStream os = null;
        try {
            os = clientSocket.getOutputStream();
            PrintStream printStream = new PrintStream(os);
            List<Cell> cells = Repository.getInstance().getCells();
            for (int i = 0; i < cells.size(); i++) {
                Cell cell = cells.get(i);
                printStream.println(cell);
                System.out.println(cell);
            }
            System.out.println("[BATTLESHIP SERVER 1] sending message to Opposing player");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
