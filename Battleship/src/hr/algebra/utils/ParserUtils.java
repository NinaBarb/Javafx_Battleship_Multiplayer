/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.utils;

import hr.algebra.controller.GameViewController;
import static hr.algebra.controller.GameViewController.playerBoard;
import hr.algebra.model.Board;
import hr.algebra.model.Cell;
import hr.algebra.model.Ship;
import hr.algebra.repo.Repository;

/**
 *
 * @author Nina
 */
public class ParserUtils {

    private static final String DELIMITER = ",";
    private static int shipsToPlace = 5;
    
    public ParserUtils() {
    }
    
    public static void parseMessage(String message) {
        String[] details = message.split(DELIMITER);
        int x = Integer.valueOf(details[1].trim());
        int y = Integer.valueOf(details[2].trim());
        boolean wasShot = Boolean.parseBoolean(details[3].trim());
        int shipType = Integer.valueOf(details[4].trim());
        int shipHealth = Integer.valueOf(details[5].trim());
        shipsToPlace = shipHealth;
        boolean isVertical = Boolean.parseBoolean(details[6].trim());
        boolean enemy = Boolean.parseBoolean(details[7].trim());
        boolean enemyTurn = Boolean.parseBoolean(details[9].trim());
        Repository.getInstance().setEnemyTurn(enemyTurn);

        Board board = new Board(enemy);
        Cell c = new Cell(x, y, board);
        c.setWasShot(wasShot);
        Ship ship = new Ship(shipType,isVertical);
        c.setShip(ship);
        Repository.getInstance().addCell(c);
        if (board.isEnemy() == false) {
            if (GameViewController.enemyBoard.placeShip(ship, c.getCellX(), c.getCellY())) {
                if (--shipsToPlace == 0) {
                }
            }
        }else{
            for (Cell cell : Repository.getInstance().getCells()) {
                if (cell.isWasShot()) {
                    Cell ce = playerBoard.getCell(cell.getCellX(), cell.getCellY());
                    ce.shoot();
                    System.out.println("Pogođena ćelija");
                }
            }
           
        }
        Repository.getInstance().getCells().clear();
    }
}
