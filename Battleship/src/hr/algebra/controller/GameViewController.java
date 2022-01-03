/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.model.Board;
import hr.algebra.model.Cell;
import hr.algebra.model.ChatMessage;
import hr.algebra.model.JNDIInfo;
import hr.algebra.model.Ship;
import hr.algebra.model.networking.MessengerService;
import hr.algebra.model.networking.MessengerServiceImpl;
import hr.algebra.model.networking.Server;
import hr.algebra.repo.Repository;
import hr.algebra.utils.JndiUtils;
import hr.algebra.utils.ParserUtils;
import hr.algebra.utils.ReflectionUtils;
import hr.algebra.utils.SerializationUtils;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Nina
 */
public class GameViewController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Button btnPlay;
    
    private Socket clientSocket;
    private final int SERVER_PORT = 12345;
    
    private Server serverThread;
    private PrintWriter out;
    private ObjectOutputStream objectWriter;
    private BufferedReader in;
    private boolean isHost;
    private OutputStream os;
    private PrintStream printStream;
    private MessengerService server;
    private MessengerService stub;

    public boolean enemyTurn = false;
    List<Cell> enemyCells = new ArrayList<>();
    public static Board enemyBoard, playerBoard;
    private int shipsToPlace = 5;

    private final String packageLocation = ".\\src";
    private final String subPackageLocation = "";
    private final String FILE_NAME = "game.ser";
    
    private JNDIInfo configurationInfo;
    
    private static final String PLAYER_JOINED = "Player joined!";
    
    @FXML
    private Button btnHostGame;
    @FXML
    private Button btnConnect;
    @FXML
    public TextArea taChat;
    @FXML
    private TextArea taInput;
    @FXML
    private Button btnSend;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createBoards();
        configurationInfo = JndiUtils.getConfigurationInfo();
    }
 
    private void createBoards() {
            btnPlay.setDisable(true);
            btnHostGame.setDisable(true);
            btnConnect.setDisable(true);
            createPlayerBoard();
            createEnemyBoard();
            setBoardsToScene();
            enemyBoard.setDisable(true);
    }

    public void createEnemyBoard() {
        
        enemyBoard = new Board(true, event -> {
                    try {
                        clientSocket = new Socket(configurationInfo.getName(), Integer.parseInt(configurationInfo.getPort()));
                        System.out.println(clientSocket);
                        
                    } catch (IOException ex) {
                        Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                Cell cell = (Cell) event.getSource();
                Repository.getInstance().getCells().clear();
                if (cell.getShip() != null) {
                    Repository.getInstance().addCell(cell);
                }
                
                if (cell.isWasShot()) {
                    return;
                }
                
                enemyTurn = !cell.shoot();
                
                if (enemyBoard.getShips() == 0) {
                    System.out.println("YOU WIN");
                    System.exit(0);
                }
                
                if (enemyTurn) {
                    Cell emptyCell = (Cell) event.getSource();
                    Ship ship = new Ship(-1, true);
                    emptyCell.setShip(ship);
                    emptyCell.setEnemyTurn(true);
                    Repository.getInstance().addCell(emptyCell);
                    enemyBoard.setDisable(true);
                }
                if (isHost) {
                    new Thread(() -> {
                        try {
                            System.out.println(clientSocket);
                            objectWriter = new ObjectOutputStream(clientSocket.getOutputStream());
                            System.out.println("Uspješno se uspostavila vezan s klijentom!");
                            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            System.out.println("Spojio sam se na klijenta!" + clientSocket);
                            out = new PrintWriter(clientSocket.getOutputStream(), true);
                            List<Cell> cells = Repository.getInstance().getCells();
                            for (int i = 0; i < cells.size(); i++) {
                                Cell c = cells.get(i);
                                out.println(c);
                                System.out.println("Poslao klijentu pogođenog" + c + " - " + clientSocket);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }).start();
                }
                if (!isHost) {
                    new Thread(() -> {
                        List<Cell> cells = Repository.getInstance().getCells();
                        for (int i = 0; i < cells.size(); i++) {
                            Cell c = cells.get(i);
                            printStream.println(c);
                            System.out.println("Poruka je poslana serveru " + c);
                        }
                    }).start();
                }
        });
    }
    
    private void createPlayerBoard() {
        playerBoard = new Board(false, event -> {
            placeShips(event);
        });

    }

    private void setBoardsToScene() {
        VBox vbox = new VBox(100, enemyBoard, playerBoard);
        vbox.getStyleClass().add("container");
        vbox.setAlignment(Pos.TOP_LEFT);
        borderPane.setLeft(vbox);
    }

    @FXML
    private void btnPlay_Clicked(){
            btnPlay.setDisable(true);
            /*loadEnemyBoard();*/
    }

    @FXML
    private void saveGame_Click(ActionEvent event) {
        try {
            SerializationUtils.write(Repository.getInstance(), FILE_NAME);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Successfully saved!");
            alert.setContentText("The game has been successfully saved to your computer.");

            alert.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadGame_Click(ActionEvent event) {
        try {
            SerializationUtils.read(FILE_NAME);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        createEnemyBoard();
        loadPlayerBoard();
        setBoardsToScene();
    }

    private void loadPlayerBoard() {
        playerBoard = new Board(false, e -> {

            for (Cell cell : Repository.getInstance().getCells()) {
                if (!cell.getBoard().isEnemy()) {
                    if (cell.getShip()!=null) {
                        if (playerBoard.placeShip(cell.getShip(), cell.getCellX(), cell.getCellY())) {
                            shipsToPlace = (cell.getShip().getType()) - 1;
                        }
                    }
                    if(cell.isWasShot()) {
                        Cell c = playerBoard.getCell(cell.getCellX(), cell.getCellY());
                        c.shoot();
                    }
                } else {
                    enemyCells.add(cell);
                }
            }
            if (Repository.getInstance().getCells().size() == 1
                    || Repository.getInstance().getCells().size() == 2
                    || Repository.getInstance().getCells().size() == 3
                    || Repository.getInstance().getCells().size() == 4) {
                placeShips(e);
            } else {
                loadEnemyBoard();
            }
        });
    }

    private void placeShips(MouseEvent e) {
        Cell c = (Cell) e.getSource();
        Ship ship = new Ship(shipsToPlace, e.getButton() == MouseButton.PRIMARY);
        if (playerBoard.placeShip(ship, c.getCellX(), c.getCellY())) {
            if (--shipsToPlace == 0) {
                btnConnect.setDisable(false);
                btnHostGame.setDisable(false);
            }
            c.setShip(ship);
            Repository.getInstance().addCell(c);
        }
    }

    private void loadEnemyBoard() {
        /*enemyCells.forEach(cell -> {
        if (cell.isWasShot()) {
        Cell c = enemyBoard.getCell(cell.getCellX(), cell.getCellY());
        c.shoot();
        } else {
        if (enemyBoard.placeShip(cell.getShip(), cell.getCellX(), cell.getCellY())) {
        }
        }
        });*/
    }

    @FXML
    private void documentation_Click(ActionEvent event) {

        StringBuilder sb = new StringBuilder();
        ReflectionUtils.readAllFromSourcePackage(packageLocation, subPackageLocation, sb);

        try (FileWriter zapisivac = new FileWriter("dokumentacija.html")) {
            zapisivac.write(sb.toString());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspješno generiranje dokumentacije");
            alert.setHeaderText(
                    "Dokumentacija s popisom klasa je uspješno generirana!");
            alert.setContentText(
                    "Datoteka \"dokumentacija.html\" je uspješno generirana!");

            alert.showAndWait();

        } catch (IOException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neuspješno generiranje dokumentacije");
            alert.setHeaderText(
                    "Dogodila se pogreška tijekom generiranja datoteke"
                    + " s dokumentacijom!");
            alert.setContentText(
                    "Datoteka \"dokumentacija.html\" nije uspješno "
                    + "generirana!");

            alert.showAndWait();
        }
    }

    @FXML
    private void btnHostGame_Clicked() {
        try {
            MessengerService messengerService = new MessengerServiceImpl();
            stub = (MessengerService) UnicastRemoteObject
                    .exportObject((MessengerService) messengerService, 0);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(configurationInfo.getRegistry()));
            registry.rebind("MessengerService", stub);
            
            btnConnect.setDisable(true);
            btnHostGame.setDisable(true);
            isHost = true;
            serverThread = new Server(this);
            serverThread.setDaemon(true);
            serverThread.start();
        } catch (RemoteException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btnConnect_Clicked() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            server = (MessengerService) registry
                    .lookup("MessengerService");
            
            isHost = false;
            clientSocket = new Socket(configurationInfo.getName(), Integer.parseInt(configurationInfo.getPort()));
            System.out.println(clientSocket);
            new Thread(() -> {
                try {
                    os = clientSocket.getOutputStream();
                    printStream = new PrintStream(os);
                    
                    btnConnect.setDisable(true);
                    btnHostGame.setDisable(true);
                    enemyBoard.setDisable(false);
                    taInput.setDisable(false);
                    taChat.setText(PLAYER_JOINED);
                    
                    List<Cell> cells = Repository.getInstance().getCells();
                    for (int i = 0; i < cells.size(); i++) {
                        Cell cell = cells.get(i);
                        printStream.println(cell);
                    }
                    System.out.println("Poruka je poslana serveru;");

                    System.out.println("Primam poruku od servera");
                    btnPlay.setDisable(false);
                    String greeting = "";
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    while ((greeting = in.readLine()) != null) {
                        System.out.println("Pročitao sam poruku: " + greeting);
                        ParserUtils.parseMessage(greeting);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();

        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enableStartGameButton() {
        btnPlay.setDisable(false);
        taInput.setDisable(false);
    }

    public void beginServerTurn() {
        enemyBoard.setDisable(false);
    }

    @FXML
    private void btnSendMessage(ActionEvent event) {
        List<ChatMessage> allMessages = new ArrayList();
        if (isHost) {
            try {
                ChatMessage chatMessage = new ChatMessage("Player 1", taInput.getText().trim(), LocalDateTime.now());
                stub.sendMessage(chatMessage);
                allMessages = stub.getAllMessages();
            } catch (RemoteException ex) {
                Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            try {
                ChatMessage chatMessage = new ChatMessage("Player 2", taInput.getText().trim(), LocalDateTime.now());
                server.sendMessage(chatMessage);
                allMessages = server.getAllMessages();
            } catch (RemoteException ex) {
                Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : allMessages) {
            sb.append(msg)
              .append(System.getProperty("line.separator"))
              .append(System.getProperty("line.separator"));
        }
        
        taChat.setText(sb.toString());
        taInput.clear();
        
    }

    @FXML
    private void keyEntered() {
        if (taInput.getText().trim().equals("")) {
            btnSend.setDisable(true);
        }else{
            btnSend.setDisable(false);
        }
    }
}
