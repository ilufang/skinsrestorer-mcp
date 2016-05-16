package skinsrestorer.shared.api;

import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.ConfigStorage;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.DataFiles;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

import net.minecraft.util.ChatComponentText;
import net.minecraft.entity.player.EntityPlayer;

public class SkinsRestorerAPI {

	/**
	 * This method is used to set player's skin.
	 * <p>
	 * Keep in mind it just sets the skin, you have to apply it using another
	 * method!
	 * <p>
	 * Method will not do anything if it fails to get the skin from MojangAPI or
	 * database!
	 */
	public static void setSkin(final EntityPlayer player, final String skinName) {
		final String playerName = player.getName();

		new Thread(new Runnable() {

			@Override
			public void run() {

				SkinProfile skinprofile = null;

				try {
					skinprofile = SkinFetchUtils.fetchSkinProfile(skinName, null);
					SkinStorage.getInstance().setSkinData(playerName, skinprofile);
					skinprofile.attemptUpdate();

					player.addChatMessage(new ChatComponentText("Skin changed. Please relog."));

				} catch (SkinFetchFailedException e) {
					player.addChatMessage(new ChatComponentText("Skin fetch failed: "+e));

					skinprofile = SkinStorage.getInstance().getSkinData(skinName);

					if (skinprofile == null) {
						return;
					}

					SkinStorage.getInstance().setSkinData(playerName, skinprofile);

					player.addChatMessage(new ChatComponentText("Found offline profile, using that. Please relog."));
				}

			}

		}).start(); // R U sure this should be run() ?
	}

	/**
	 * This method is used to check if player has saved skin data. If player
	 * skin data equals null, the method will return false. Else if player has
	 * saved data, it will return true.
	 */
	public static boolean hasSkin(String playerName) {
		if (SkinStorage.getInstance().getSkinData(playerName) == null) {
			return false;
		}
		return true;
	}

	/**
	 * This method is used to get player's skin name. If player doesn't have
	 * skin, the method will return null. Else it will return player's skin
	 * name.
	 */
	public static String getSkinName(String playerName) {

		SkinProfile data = SkinStorage.getInstance().getSkinData(playerName);
		if (data == null) {
			return null;
		}
		return data.getName();

	}

	// Skin application methods removed for MCP version

	/**
	 * Used to get the SkinsRestorer config if needed for
	 * external plugins which are depending on SkinsRestorer
	 */
	public static DataFiles getConfig(){
		return ConfigStorage.getInstance().config;
	}

}
