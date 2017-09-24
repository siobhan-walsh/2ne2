package database;

public class Tuple {
	private Object x;
	private Object y;

	public Tuple(Object x, Object y) {
		this.setX(x);
		this.setY(y);
	}

	public Object getX() {
		return x;
	}

	public void setX(Object x) {
		this.x = x;
	}

	public Object getY() {
		return y;
	}

	public void setY(Object y) {
		this.y = y;
	}
}
