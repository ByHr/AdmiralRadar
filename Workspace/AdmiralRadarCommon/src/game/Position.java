package game;

import java.awt.Point;
import java.io.*;

import net.MyPacketable;
import pref.GamePreferences;

public class Position implements Serializable, MyPacketable {

	private int	x;
	private int	y;

	private static final long serialVersionUID = 1L;

	public Position(int x, int y) {
		this.setX( x );
		this.setY( y );
	}

	public Position() {
		x = -1;
		y = -1;
	}

	public Position(Point p) {
		x = p.x;
		y = p.y;
	}

	public Point getPoint() {
		return new Point( getX() , getY() );
	}

	public void setPosition(int a, int b) {
		x = a;
		y = b;
	}

	public Position getPosition() {
		return this;
	}

	public boolean isValid() {
		if (( getX() >= 0 ) && ( getX() < GamePreferences.SEG )) {
			if (( getY() >= 0 ) && ( getY() < GamePreferences.SEG )) { return true; }
		}
		return false;
	}

	public boolean isAdjacent(Position p) {
		if (( x + 1 == p.getX() + 1 && y == p.getY() ) || ( x + 1 == p.getX() + 1 && y + 1 == p.getY() + 1 )
				|| ( x == p.getX() && y + 1 == p.getY() + 1 ) || ( x - 1 == p.getX() - 1 && y + 1 == p.getY() + 1 )
				|| ( x - 1 == p.getX() - 1 && y == p.getY() ) || ( x - 1 == p.getX() - 1 && y - 1 == p.getY() - 1 )
				|| ( x == p.getX() && y - 1 == p.getY() - 1 )
				|| ( x + 1 == p.getX() + 1 && y - 1 == p.getY() - 1 )) { return true; }
		return false;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public String toString() {
		return "(" + getX() + ":" + getY() + ")";
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Position getNeighbor(Direction d) {
		switch (d){
			case EAST:
				return new Position(x + 1 , y);
			case NORTH:
				return new Position(x , y - 1);
			case SOUTH:
				return new Position(x , y + 1);
			case WEST:
				return new Position(x - 1 , y);
			case STOP:
			default: return getPosition();
			
		}
	}
}