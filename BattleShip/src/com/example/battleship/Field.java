package com.example.battleship;

import com.example.battleship.ships.Ship;

public class Field {
    /*
	public final static int HIDDEN_STATE        = 0;
    public final static int EMPTY_STATE         = 1;
    public final static int CHECKED_FIELD_STATE = 2;
    public final static int MINE_STATE          = 3;
    public final static int KILLED_MINE_STATE   = 4;
    public final static int SHIP_STATE          = 5;
    public final static int PADDED_SHIP_STATE   = 6;
    public final static int KILLED_SHIP_STATE   = 7;
*/
    private int x;
    private int y;
    private FieldState state = FieldState.EMPTY_STATE;
    private SeaObject obj = null;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Field(int x, int y, SeaObject obj) {
        this(x,y);
        setObj(obj);
    }

    public boolean shotOnField() {
    	if (obj != null && obj instanceof Ship) {
    		state = FieldState.PADDED_SHIP_STATE;
    		obj.shotOnObject(this);
    		return true;
    	} else if (obj instanceof Mine){
    		state = FieldState.KILLED_MINE_STATE;
    		obj.shotOnObject(this);
    	} else {
    		state = FieldState.CHECKED_FIELD_STATE;		
    	}
    	return false;
    }

    public FieldState getState() {
        return state;
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
    
    public String toEnemy() {
    	switch(state) {
    		case EMPTY_STATE:
    		case MINE_STATE:
    		case SHIP_STATE:
    			return FieldState.HIDDEN_STATE.toString();
    		default:
    			return state.toString();
    	}
    }
    
}
