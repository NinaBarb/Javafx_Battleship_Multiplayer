/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Nina
 */
public class Cell extends Rectangle implements Serializable{
    
        private static final long serialVersionUID = 1L;
    
        private int x, y;
        private Ship ship = null;
        private boolean wasShot = false;
        private Board board;
        private boolean enemyTurn = false;

    public boolean isEnemyTurn() {
        return enemyTurn;
    }

    public void setEnemyTurn(boolean enemyTurn) {
        this.enemyTurn = enemyTurn;
    }
        
        

        public Cell(int x, int y, Board board) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.board = board;
            setFill(Color.rgb(59, 105, 183));
            setStroke(Color.rgb(159, 191, 245));
        }
        
        public boolean shoot() {
            wasShot = true;
            setFill(Color.BLACK);

            if (ship != null) {
                ship.hit();
                setFill(Color.RED);
                if (!ship.isAlive()) {
                    board.setShips(board.getShips() - 1);
                }
                return true;
            }

            return false;
        }
        
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeInt(getCellX());
        oos.writeInt(getCellY());
        oos.writeObject(getBoard());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        setCellX(ois.readInt());
        setCellY(ois.readInt());
        setBoard((Board)ois.readObject());
    }

    public int getCellX() {
        return x;
    }

    public void setCellX(int x) {
        this.x = x;
    }

    public int getCellY() {
        return y;
    }

    public void setCellY(int y) {
        this.y = y;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public boolean isWasShot() {
        return wasShot;
    }

    public void setWasShot(boolean wasShot) {
        this.wasShot = wasShot;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return ", " + x + ", " + y + ", " + wasShot + ", " + ship + ", " + board + "," + enemyTurn + ",";
    }
}
