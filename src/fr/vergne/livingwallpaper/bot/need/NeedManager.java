package fr.vergne.livingwallpaper.bot.need;

import java.util.LinkedList;

import fr.vergne.livingwallpaper.bot.Bot;

public class NeedManager {

	private final Bot bot;
	private final LinkedList<Need> needs = new LinkedList<Need>();

	public NeedManager(Bot bot) {
		this.bot = bot;
	}

	public Need getCurrentNeed(Bot bot) {
		return isEmpty() ? null : needs.getFirst();
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

	public boolean isEmpty() {
		// TODO cleaning here is dirty, make it proper
		cleanNeeds();
		return needs.isEmpty();
	}
}
