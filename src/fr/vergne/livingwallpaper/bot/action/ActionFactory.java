package fr.vergne.livingwallpaper.bot.action;

import java.util.Random;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.environment.Environment;

public class ActionFactory {

	public Action createEmptyAction() {
		return new Action() {

			@Override
			public ActionStatus execute(Bot bot) {
				return ActionStatus.FACULTATIVE;
			}

		};
	}

	public Action createWalkingAction(final float targetX, final float targetY) {
		return new Action() {
			private long updateTimestamp = 0;

			@Override
			public ActionStatus execute(Bot bot) {
				float x = bot.getX();
				float y = bot.getY();
				float deltaX = targetX - x;
				float deltaY = targetY - y;
				if (Math.abs(deltaX) > 0.1 && Math.abs(deltaY) > 0.1) {
					long updateDelay = System.currentTimeMillis()
							- updateTimestamp;
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

					updateTimestamp = System.currentTimeMillis();
					return ActionStatus.RUNNING;
				} else {
					bot.setX(targetX);
					bot.setY(targetY);
					return ActionStatus.FINISHED;
				}
			}
		};
	}

	public Action createRandomWalkingAction() {
		return new Action() {
			private Action walkingAction = null;
			private final Random random = new Random();

			@Override
			public ActionStatus execute(Bot bot) {
				if (walkingAction == null) {
					Environment env = bot.getEnvironment();
					float targetX = env.getMaxX() * random.nextFloat();
					float targetY = env.getMaxY() * random.nextFloat();
					walkingAction = createWalkingAction(targetX, targetY);
				} else {
					ActionStatus status = walkingAction.execute(bot);
					walkingAction = status == ActionStatus.FINISHED ? null
							: walkingAction;
				}
				return ActionStatus.FACULTATIVE;
			}
		};
	}

}
