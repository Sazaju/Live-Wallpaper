package fr.vergne.livingwallpaper.bot;

import fr.vergne.livingwallpaper.bot.action.Action;
import fr.vergne.livingwallpaper.bot.need.Need;
import fr.vergne.livingwallpaper.bot.strategy.StrategyManager;
import fr.vergne.livingwallpaper.environment.Environment;

public class Bot {
	private float x = 0;
	private float y = 0;
	private int pixelsPerSeconds = 0;
	private long lastExecutionTimestamp = System.currentTimeMillis();
	private final StrategyManager stratege = new StrategyManager(this);
	private final Environment environment = new Environment();

	public Bot() {
	}

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

	public int getPixelsPerSecond() {
		return pixelsPerSeconds;
	}

	public void setPixelsPerSecond(int pixelsPerSecond) {
		this.pixelsPerSeconds = pixelsPerSecond;
	}

	public void addNeed(Need need) {
		stratege.add(need);
	}

	public void executeAction() {
		Action action = stratege.getNextAction();
		if (action == null) {
			// do not execute anything
		} else {
			action.execute(this);
		}
		lastExecutionTimestamp = System.currentTimeMillis();
	}

	public Environment getEnvironment() {
		return environment;
	}

	public long getLastExecutionDelayInMs() {
		return System.currentTimeMillis() - lastExecutionTimestamp;
	}
}
