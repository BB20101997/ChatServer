package bb.chat.gui;

import bb.chat.interfaces.IChatActor;

/**
 * @author BB20101997
 */
public class ServerActor implements IChatActor
{

	private final String	name;

	/**
	 * @param Name
	 *            the name the ServerActor will have
	 */
	public ServerActor(String Name)
	{

		name = Name;

	}

	@Override
	public String getActorName()
	{

		return name;
	}

	@Override
	/**
	 * @param s will be ignored due to the fact that the name is not changeable
	 */
	public void setActorName(String s)
	{

	}

}
