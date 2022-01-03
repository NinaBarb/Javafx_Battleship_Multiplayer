/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.repo;

import hr.algebra.model.Cell;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nina
 */
public class Repository implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    // eager singleton    
    private Repository() {        
    }
    
    private static final Repository INSTANCE = new Repository();

    public static Repository getInstance() {
        return INSTANCE;
    }
    
    private final List<Cell> cells = new ArrayList<>();
    private boolean enemyTurn = false;

    public boolean isEnemyTurn() {
        return enemyTurn;
    }

    public void setEnemyTurn(boolean enemyTurn) {
        this.enemyTurn = enemyTurn;
    }

    public List<Cell> getCells() {
        return cells;
    }
    
    public void addCell(Cell cell) {
        cells.add(cell);
    }
    
    // observable lists are not Serializable - we must do it manually
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(new ArrayList<>(cells));
    }
    
    // we must imitate constructor, so we create lists manually
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        List<Cell> serializedCells = (List<Cell>)ois.readObject();
        INSTANCE.cells.clear();
        INSTANCE.cells.addAll(serializedCells);
    }
    
    // Repository must be a TRUE Singleton so we must secure
    // that the instance created by deserialization is discarded
    private Object readResolve() {
    // Return the one true Repository and let the garbage collector
    // take care of the Repository impersonator.
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Cells[" + cells + "]";
    }
}
