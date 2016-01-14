package com.example.battleship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.Serializable;

import com.example.battleship.ships.Ship;

public class Field implements Externalizable {

	private int x;
    private int y;
    private FieldState state = FieldState.EMPTY_STATE;
    private SeaObject obj = null;

    public Field() {
	}
    
    public Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Field(int x, int y, SeaObject obj) {
        this(x,y);
        setObj(obj);
    }

    public boolean shotOnField(Ship ship) {
    	switch (state) {
		case CHECKED_FIELD_STATE:
		case KILLED_MINE_STATE:
			return false;
		case PADDED_SHIP_STATE:
		case KILLED_SHIP_STATE:
			return true;
		default:
	    	if (obj != null) {
		    	if (obj instanceof Ship) {
		    		state = FieldState.PADDED_SHIP_STATE;
		    		obj.shotOnObject(ship);
		    		return true;
		    	} 
		    	if (obj instanceof Mine){
		    		state = FieldState.KILLED_MINE_STATE;
		    		obj.shotOnObject(ship);
		    	} 
	    	} else {
	    		state = FieldState.CHECKED_FIELD_STATE;		
	    	}
	    	return false;
    	}
    }

    public FieldState getState(boolean isEnemy) {
    	if (isEnemy) {
    		switch(state) {
    		case EMPTY_STATE:
    		case MINE_STATE:
    		case SHIP_STATE:
    			return FieldState.HIDDEN_STATE;
    		default:
    			return state;
    		}
    	} else {
    		return state;
    	}
    }
    
    public void setState(FieldState state) {
		this.state = state;
	}

    public void removeObj() {
        this.obj = null;
        this.state = FieldState.EMPTY_STATE;
    }

    public void setObj(SeaObject obj) {
        this.obj = obj;
        if (obj instanceof Mine) {
            this.state = FieldState.MINE_STATE;
        } else {
            this.state = FieldState.SHIP_STATE;
        }
    }

    public int[] getCoordinats() {
        int[] xy = {x, y};
        return xy;
    }
    
    public int getX() {
		return x;
	}
    
    public int getY() {
		return y;
	}
    
    public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
    
    @Override
    public String toString() {
    	return state.toString();
    }
    
    public String forEnemy() {
    	switch(state) {
    		case EMPTY_STATE:
    		case MINE_STATE:
    		case SHIP_STATE:
    			return FieldState.HIDDEN_STATE.toString();
    		default:
    			return state.toString();
    	}
    }
    
    public SeaObject getObj() {
		return obj;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeObject(state);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		x = in.readInt();
		y = in.readInt();
		state = (FieldState) in.readObject();
	}

}
