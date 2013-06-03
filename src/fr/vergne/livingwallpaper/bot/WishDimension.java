package fr.vergne.livingwallpaper.bot;

import fr.vergne.livingwallpaper.bot.action.Action;

public interface WishDimension {

	/**
	 * 
	 * @return 0 if do not care, positive value if want to fulfill it, negative
	 *         value if want to not fulfill it.
	 */
	public double getImportance();

	/**
	 * Evaluate the interest of a specific action regarding the wish.
	 * 
	 * @param action
	 *            action to evaluate
	 * @return 0 if no effect on the wish, positive value if allow to fulfill
	 *         the wish, negative value if go against the wish.
	 */
	public double evaluateFullfilment(Action action);
}
