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

/**
 *
 * @author Nina
 */
public class Ship implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private  int type;
    private int health;
    private boolean vertical = true;

    public Ship(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        health = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }
    

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeInt(getType());
        oos.writeBoolean(isVertical());
        oos.writeInt(getHealth());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        setType(ois.readInt());
        setVertical(ois.readBoolean());
        setHealth(ois.readInt());
    }

    @Override
    public String toString() {
        return type + ", " + health + ", " + vertical;
    }
    
    
}
