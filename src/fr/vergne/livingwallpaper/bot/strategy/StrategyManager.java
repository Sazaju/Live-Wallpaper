package fr.vergne.livingwallpaper.bot.strategy;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.action.Action;
import fr.vergne.livingwallpaper.bot.action.WalkingAction;
import fr.vergne.livingwallpaper.bot.need.LocationNeed;
import fr.vergne.livingwallpaper.bot.need.Need;

public class StrategyManager {
	private final Bot bot;
	private final LinkedList<Need> needs = new LinkedList<Need>();

	public StrategyManager(Bot bot) {
		this.bot = bot;
	}

	private void cleanNeeds() {
		while (!needs.isEmpty()) {
			Need need = needs.getFirst();
			// TODO consider other strategies (e.g. continuous needs)
			if (need.isFulfilled(bot)) {
				needs.remove(need);
			} else {
				return;
			}
		}
	}

	public void add(Need need) {
		needs.addLast(need);
	}

	public void remove(Need need) {
		needs.remove(need);
	}

	public Action getNextAction() {
		cleanNeeds();
		// TODO consider recent research on planning management
		Need need = needs.isEmpty() ? null : needs.getFirst();
		if (need instanceof LocationNeed) {
			LocationNeed need2 = (LocationNeed) need;
			// TODO make smarter decision + check pre-conditions
			Collection<Action> alternatives = Arrays
					.<Action> asList(new WalkingAction(need2.getTargetX(),
							need2.getTargetY()));
			for (Action action : alternatives) {
				Need preCondition = action.getPreCondition();
				if (preCondition == null || preCondition.isFulfilled(bot)) {
					return action;
				} else {
					continue;
				}
			}
			return null;
		} else {
			return null;
		}
	}

}
