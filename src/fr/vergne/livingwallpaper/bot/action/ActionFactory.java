package fr.vergne.livingwallpaper.bot.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Random;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.need.Need;
import fr.vergne.livingwallpaper.bot.need.NeedFactory;
import fr.vergne.livingwallpaper.environment.Environment;

public class ActionFactory {

	private ActionFactory() {
		// private constructor to avoid new instances
	}

	private static final ActionFactory instance = new ActionFactory();

	public static ActionFactory getInstance() {
		return instance;
	}

	public Action createWalkingAction(final float targetX, final float targetY) {
		return new Action() {
			private final static double THRESHOLD = 0.1;

			@Override
			public void execute(Bot bot) {
				float x = bot.getX();
				float y = bot.getY();
				float deltaX = targetX - x;
				float deltaY = targetY - y;
				if (Math.abs(deltaX) > THRESHOLD
						&& Math.abs(deltaY) > THRESHOLD) {
					long updateDelay = bot.getLastExecutionDelayInMs();
					if (updateDelay > 500) {
						/*
						 * consider that the bot has done something else since
						 * the last execution, so we restart the action
						 */
					} else {
						// switch to polar coordinates
						double rho = Math.hypot(deltaX, deltaY);
						double theta = 2 * Math.atan(deltaY / (deltaX + rho));

						// control the speed
						rho = Math.min(rho, bot.getPixelsPerSecond()
								* updateDelay / 1000);

						// switch back to cartesian coordinates
						deltaX = (float) (rho * Math.cos(theta));
						deltaY = (float) (rho * Math.sin(theta));

						bot.setX(x + deltaX);
						bot.setY(y + deltaY);
					}
				} else {
					bot.setX(targetX);
					bot.setY(targetY);
				}
			}

			@Override
			public Collection<Need> getPreConditions() {
				/*
				 * TODO Only need: be able to move to the position. This
				 * includes to be able to move, and the position to be reachable
				 * (nothing on the path to block us). When such need will be
				 * available, use it.
				 */
				return Arrays.asList();
			}

			@Override
			public String getDescription() {
				return String.format(Locale.getDefault(),
						"Go to (%1$f, %2$f).", targetX, targetY);
			}
		};
	}

	public Action createRandomWalkingAction() {
		return new Action() {
			private Need walkingTarget = null;
			private final Random random = new Random();

			@Override
			public void execute(Bot bot) {
				if (walkingTarget == null) {
					walkingTarget = renewTarget(bot);
				} else {
					// try to fulfill the current need
				}
				Action action = getAction();
				action.execute(bot);
				walkingTarget = walkingTarget.isFulfilled(bot) ? renewTarget(bot)
						: walkingTarget;
			}

			private Need renewTarget(Bot bot) {
				Environment env = bot.getEnvironment();
				float targetX = env.getMaxX() * random.nextFloat();
				float targetY = env.getMaxY() * random.nextFloat();
				return NeedFactory.getInstance().createLocationNeed(targetX,
						targetY);
			}

			@Override
			public Collection<Need> getPreConditions() {
				return getAction().getPreConditions();
			}

			private Action getAction() {
				return walkingTarget.getAlternatives().iterator().next();
			}

			@Override
			public String getDescription() {
				return "Walk randomly.";
			}
		};
	}

}
