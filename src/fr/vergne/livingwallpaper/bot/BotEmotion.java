package fr.vergne.livingwallpaper.bot;


public class BotEmotion {

	private State state = State.FREE;
	private long lastInterruptionTimestamp = 0;

	public boolean isBored() {
		return state == State.FREE;
	}

	public boolean isQuestioning() {
		return System.currentTimeMillis() - lastInterruptionTimestamp < 2000;
	}

	public void interrupt() {
		lastInterruptionTimestamp = System.currentTimeMillis();
	}
}
