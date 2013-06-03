package fr.vergne.livingwallpaper.bot;

import java.util.Collection;
import java.util.LinkedHashSet;

import fr.vergne.livingwallpaper.bot.action.Action;

public class InterestSpace {

	private final Collection<WishDimension> dimensions = new LinkedHashSet<WishDimension>();

	public void addDimension(WishDimension dimension) {
		dimensions.add(dimension);
	}

	public void removeDimension(WishDimension dimension) {
		dimensions.remove(dimension);
	}

	public Collection<WishDimension> getDimensions() {
		return dimensions;
	}

	public double evaluateInterest(Action action) {
		double interest = Double.NEGATIVE_INFINITY;
		for (WishDimension dimension : dimensions) {
			interest = dimension.getImportance()
					* dimension.evaluateFullfilment(action);
		}
		return interest / dimensions.size();
	}
}
