/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model.networking;

import hr.algebra.controller.GameViewController;
import hr.algebra.model.Cell;
import hr.algebra.model.JNDIInfo;
import hr.algebra.repo.Repository;
import hr.algebra.utils.JndiUtils;
import hr.algebra.utils.ParserUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nina
 */
public class Server extends Thread {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private ObjectOutputStream objectWriter;
    private BufferedReader in;
    private static final String PLAYER_JOINED = "Player joined!";

    private int numOfPlayers;
    
    private final GameViewController controller;

    public Server(GameViewController controller) {
        this.controller = controller;
    }
    
    public void accept(Socket client){
        clientSocket = client;
    }

    @Override
    public void run() {
        JNDIInfo configurationInfo = JndiUtils.getConfigurationInfo();
        try {
            serverSocket = new ServerSocket(Integer.parseInt(configurationInfo.getPort()));
            System.out.println("Uspješno se kreirao server socket!");
            /*while (numOfPlayers<1) {*/
                clientSocket = serverSocket.accept();
                System.out.println(clientSocket);
                controller.taChat.setText(PLAYER_JOINED);
                /*numOfPlayers++;
                System.out.println("Prihvaćena je konekcija!");*/
                /*}
                System.out.println("2 igraca su u igri. Ulazak igraca vise nije dopusten");*/
            
            while (true) {
                objectWriter = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("Uspješno se uspostavila vezan s klijentom!");
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Spojio sam se na klijenta!");
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                List<Cell> cells = Repository.getInstance().getCells();
                for (int i = 0; i < cells.size(); i++) {
                    Cell cell = cells.get(i);
                    out.println(cell);
                }
                
                controller.enableStartGameButton();
                
                String greeting = "";
                while ((greeting = in.readLine()) != null) {
                    System.out.println("Pročitao sam poruku: " + greeting);
                    ParserUtils.parseMessage(greeting);
                    if (Repository.getInstance().isEnemyTurn()) {
                        controller.beginServerTurn();
                        controller.createEnemyBoard();
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
