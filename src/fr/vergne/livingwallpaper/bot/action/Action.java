package fr.vergne.livingwallpaper.bot.action;

import java.util.Collection;

import fr.vergne.livingwallpaper.bot.Bot;
import fr.vergne.livingwallpaper.bot.need.Need;

public interface Action {

	public void execute(Bot bot);

	public String getDescription();

	public Collection<Need> getPreConditions();
}
