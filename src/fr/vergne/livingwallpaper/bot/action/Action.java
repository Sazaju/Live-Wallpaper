package fr.vergne.livingwallpaper.bot.action;

import fr.vergne.livingwallpaper.bot.Bot;

public interface Action {

	/**
	 * <p>
	 * Core of the action (what should be done). It should be as quick as
	 * possible in order to avoid the monopolization of the running time and
	 * allow a better management of the actions. The method should be re-called
	 * as long as it returns {@link ActionStatus#RUNNING}. Consider to do no
	 * more than one step further each time it is called and return this value
	 * as long as you need more steps. If no more steps are necessary, you
	 * should return {@link ActionStatus#FINISHED}, after what the method will
	 * not be called again. You can also return {@link ActionStatus#FACULTATIVE}
	 * to say that there is still things to do but it can be postponed or even
	 * terminated without any problem.
	 * </p>
	 * <p>
	 * Notice that the next call can be done a long time after this method has
	 * returned {@link ActionStatus#RUNNING} or {@link ActionStatus#FACULTATIVE}
	 * . The method must take care of the context evolution if necessary. It is
	 * also possible that the action is forced to stop in particularly
	 * problematic situations, although the method has returned
	 * {@link ActionStatus#RUNNING}.
	 * </p>
	 * 
	 * @return TRUE if the action need to be resumed, FALSE otherwise
	 */
	public ActionStatus execute(Bot bot);

}
