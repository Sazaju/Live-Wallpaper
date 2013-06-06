package fr.vergne.livingwallpaper.bot.need;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.action.Action;
import fr.vergne.livingwallpaper.bot.action.ActionFactory;

public class NeedFactory {

	private NeedFactory() {
		// private constructor to avoid new instances
	}

	private static final NeedFactory instance = new NeedFactory();

	public static NeedFactory getInstance() {
		return instance;
	}

	public Need createLocationNeed(final float targetX, final float targetY) {
		return new Need() {

			@Override
			public boolean isFulfilled(Bot bot) {
				return bot.getX() == targetX && bot.getY() == targetY;
			}

			@Override
			public String getDescription() {
				return String.format(Locale.getDefault(),
						"Location (%1$f, %2$f) reached.", targetX, targetY);
			}

			@Override
			public Collection<Action> getAlternatives() {
				ActionFactory factory = ActionFactory.getInstance();
				return Arrays.<Action> asList(factory.createWalkingAction(
						targetX, targetY));
			}
		};
	}
}
