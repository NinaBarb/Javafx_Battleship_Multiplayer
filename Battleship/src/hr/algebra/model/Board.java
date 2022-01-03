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
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Nina
 */
public class Board extends Parent implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private final int ROWNMBR = 10;
    private final int COLUMNNMBR = 10;
    
    private transient VBox rows = new VBox();
    private boolean enemy = false;
    private int ships = 5;
    
    public Board(boolean enemy) {
        this.enemy = enemy;
        for (int y = 0; y < ROWNMBR; y++) {
            HBox row = new HBox();
            for (int x = 0; x < COLUMNNMBR; x++) {
                Cell c = new Cell(x, y, this);
                row.getChildren().add(c);
            }
            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }

    public Board(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;
        for (int y = 0; y < ROWNMBR; y++) {
            HBox row = new HBox();
            for (int x = 0; x < COLUMNNMBR; x++) {
                Cell c = new Cell(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }
            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }
    
    /*public boolean placeShip(Ship ship, int x, int y) {
    int length = ship.getType();
    
    if (canPlaceShip(ship, x, y)) {
    
    if (ship.isVertical()) {
    for (int i = y; i < y + length; i++) {
    Cell cell = getCell(x, i);
    cell.setShip(ship);
    if (!enemy) {
    cell.setFill(Color.rgb(254, 199, 150));
    cell.setStroke(Color.rgb(161, 121, 108));
    }
    
    }
    } else {
    for (int i = x; i < x + length; i++) {
    Cell cell = getCell(i, y);
    cell.setShip(ship);
    if (!enemy) {
    cell.setFill(Color.rgb(254, 199, 150));
    cell.setStroke(Color.rgb(161, 121, 108));
    }
    }
    }
    return true;
    }
    return false;
    }*/
    
    /*private boolean canPlaceShip(Ship ship, int x, int y) {
    int length = ship.getType();
    
    if (ship.isVertical()) {
    for (int i = y; i < y + length; i++) {
    if (!isValidPoint(x, i))
    return false;
    
    Cell cell = getCell(x, i);
    if (cell.getShip() != null)
    return false;
    }
    }
    else {
    for (int i = x; i < x + length; i++) {
    if (!isValidPoint(i, y))
    return false;
    
    Cell cell = getCell(i, y);
    if (cell.getShip() != null)
    return false;
    }
    }
    return true;
    }
    
    public Cell getCell(int x, int y) {
    return (Cell)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }
    
    private boolean isValidPoint(double x, double y) {
    return x >= 0 && x  < 10 && y >= 0 && y < 10;
    }*/
    
     public boolean placeShip(Ship ship, int x, int y) {
        if (canPlaceShip(ship, x, y)) {
            int length = ship.getType();

            if (ship.isVertical()) {
                for (int i = y; i < y + length; i++) {
                    Cell cell = getCell(x, i);
                    cell.setShip(ship);
                    /*if (!enemy) {*/
                        cell.setFill(Color.WHITE);
                        cell.setStroke(Color.GREEN);
                        /*}*/
                }
            }
            else {
                for (int i = x; i < x + length; i++) {
                    Cell cell = getCell(i, y);
                    cell.setShip(ship);
                    /*if (!enemy) {*/
                        cell.setFill(Color.WHITE);
                        cell.setStroke(Color.GREEN);
                        /*}*/
                }
            }

            return true;
        }

        return false;
    }
    
    public Cell getCell(int x, int y) {
        return (Cell)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    private Cell[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Cell> neighbors = new ArrayList<Cell>();

        for (Point2D p : points) {
            if (isValidPoint(p)) {
                neighbors.add(getCell((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Cell[0]);
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.getType();

        if (ship.isVertical()) {
            for (int i = y; i < y + length; i++) {
                if (!isValidPoint(x, i))
                    return false;

                Cell cell = getCell(x, i);
                if (cell.getShip() != null)
                    return false;

                for (Cell neighbor : getNeighbors(x, i)) {
                    if (!isValidPoint(x, i))
                        return false;

                    if (neighbor.getShip() != null)
                        return false;
                }
            }
        }
        else {
            for (int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y))
                    return false;

                Cell cell = getCell(i, y);
                if (cell.getShip() != null)
                    return false;

                for (Cell neighbor : getNeighbors(i, y)) {
                    if (!isValidPoint(i, y))
                        return false;

                    if (neighbor.getShip() != null)
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }
    
    
    
    
    

    public VBox getRows() {
        return rows;
    }

    public void setRows(VBox rows) {
        this.rows = rows;
    }

    public boolean isEnemy() {
        return enemy;
    }

    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }

    public int getShips() {
        return ships;
    }

    public void setShips(int ships) {
        this.ships = ships;
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeBoolean(isEnemy());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        setEnemy(ois.readBoolean());
    }

    @Override
    public String toString() {
        return enemy + ", ";
    }
    
    
}
