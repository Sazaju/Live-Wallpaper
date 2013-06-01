package fr.vergne.livingwallpaper;

import java.util.Random;

public class BotLocation {
	private float x = 0;
	private float y = 0;
	private float targetX = x;
	private float targetY = y;
	private float maxX = 0;
	private float maxY = 0;
	private long waitingTimestamp;
	private long updateTimestamp;
	private int pixelsPerSeconds;
	private final Random random = new Random();

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getTargetX() {
		return targetX;
	}

	public void setTargetX(float x) {
		this.targetX = x;
	}

	public float getTargetY() {
		return targetY;
	}

	public void setTargetY(float y) {
		this.targetY = y;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}

	public void setTarget(float x, float y) {
		setTargetX(x);
		setTargetY(y);
	}

	public void resetTarget() {
		setTarget(getX(), getY());
	}

	public void moveToTarget() {
		if (!isTargetReached()) {
			double deltaX = targetX - x;
			double deltaY = targetY - y;

			// switch to polar coordinates
			double rho = Math.hypot(deltaX, deltaY);
			double theta = 2 * Math.atan(deltaY / (deltaX + rho));

			// control the speed
			rho = Math.min(rho, getMaxWalkLength());

			// switch back to cartesian coordinates
			deltaX = rho * Math.cos(theta);
			deltaY = rho * Math.sin(theta);

			x += deltaX;
			y += deltaY;

			waitingTimestamp = System.currentTimeMillis();
		} else {
			if (getWaitingDelay() > 4000) {
				setTarget(getMaxX() * random.nextFloat(),
						getMaxY() * random.nextFloat());
			} else {
				// just wait without moving
			}
		}
		updateTimestamp = System.currentTimeMillis();
	}

	private long getWaitingDelay() {
		return System.currentTimeMillis() - waitingTimestamp;
	}

	private long getUpdateDelay() {
		return System.currentTimeMillis() - updateTimestamp;
	}

	private long getMaxWalkLength() {
		return getPixelsPerSecond() * getUpdateDelay() / 1000;
	}

	public boolean isTargetReached() {
		return Math.abs(targetX - x) < 0.1 && Math.abs(targetY - y) < 0.1;
	}

	public int getPixelsPerSecond() {
		return pixelsPerSeconds;
	}

	public void setPixelsPerSecond(int pixelsPerSecond) {
		this.pixelsPerSeconds = pixelsPerSecond;
	}

}
