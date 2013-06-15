package fr.vergne.livingwallpaper.bot.need;

import java.util.Locale;

import fr.vergne.livingwallpaper.bot.Bot;

public class LocationNeed implements Need {

	private final float targetX;
	private final float targetY;

	public LocationNeed(float targetX, float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
	}

	@Override
	public boolean isFulfilled(Bot bot) {
		return bot.getX() == getTargetX() && bot.getY() == getTargetY();
	}

	@Override
	public String getDescription() {
		return String.format(Locale.getDefault(),
				"Location (%1$f, %2$f) reached.", getTargetX(), getTargetY());
	}

	public float getTargetX() {
		return targetX;
	}

	public float getTargetY() {
		return targetY;
	}

}
