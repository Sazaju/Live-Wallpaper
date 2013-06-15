package fr.vergne.livingwallpaper.bot.need;

import fr.vergne.livingwallpaper.bot.Bot;

//TODO needs to be to a specific location are not compatible between each other, so only one can be fulfilled at a time (so fulfill one before to fulfill another).
//TODO consider the need decomposition, so one complex need can be decomposed in more basic ones
public interface Need {

	public boolean isFulfilled(Bot bot);

	public String getDescription();
}
