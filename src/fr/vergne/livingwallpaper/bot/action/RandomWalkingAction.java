package fr.vergne.livingwallpaper.bot.action;

import java.util.Random;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.need.Need;
import fr.vergne.livingwallpaper.environment.Environment;

// TODO move more homogeneously (consider angle + slow angle motion between each step)
public class RandomWalkingAction implements Action {
	private final Random random = new Random();

	@Override
	public void execute(Bot bot) {
		Environment env = bot.getEnvironment();
		float targetX = env.getMaxX() * random.nextFloat();
		float targetY = env.getMaxY() * random.nextFloat();
		WalkingAction walkingAction = new WalkingAction(targetX, targetY);
		walkingAction.execute(bot);
	}

	@Override
	public Need getPreCondition() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Walk randomly.";
	}
}
