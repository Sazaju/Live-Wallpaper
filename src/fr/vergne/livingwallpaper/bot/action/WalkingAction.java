package fr.vergne.livingwallpaper.bot.action;

import java.util.Locale;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.need.Need;

public class WalkingAction implements Action {

	private final float targetX;
	private final float targetY;

	public WalkingAction(float targetX, float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
	}

	private final static double THRESHOLD = 0.1;

	@Override
	public void execute(Bot bot) {
		float x = bot.getX();
		float y = bot.getY();
		float deltaX = getTargetX() - x;
		float deltaY = getTargetY() - y;
		if (Math.abs(deltaX) > THRESHOLD && Math.abs(deltaY) > THRESHOLD) {
			long updateDelay = bot.getLastExecutionDelayInMs();
			if (updateDelay > 500) {
				/*
				 * consider that the bot has done something else since the last
				 * execution, so we restart the action
				 */
			} else {
				// switch to polar coordinates
				double rho = Math.hypot(deltaX, deltaY);
				double theta = 2 * Math.atan(deltaY / (deltaX + rho));

				// control the speed
				rho = Math.min(rho, bot.getPixelsPerSecond() * updateDelay
						/ 1000);

				// switch back to cartesian coordinates
				deltaX = (float) (rho * Math.cos(theta));
				deltaY = (float) (rho * Math.sin(theta));

				bot.setX(x + deltaX);
				bot.setY(y + deltaY);
			}
		} else {
			bot.setX(getTargetX());
			bot.setY(getTargetY());
		}
	}

	@Override
	public Need getPreCondition() {
		/*
		 * TODO Only need: be able to move to the position. This includes to be
		 * able to move, and the position to be reachable (nothing on the path
		 * to block us). When such need will be available, use it.
		 */
		return null;
	}

	@Override
	public String getDescription() {
		return String.format(Locale.getDefault(), "Go to (%1$f, %2$f).",
				getTargetX(), getTargetY());
	}

	public float getTargetX() {
		return targetX;
	}

	public float getTargetY() {
		return targetY;
	}
}
