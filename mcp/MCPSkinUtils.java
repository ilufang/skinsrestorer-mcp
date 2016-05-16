package skinsrestorer.mcp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.storage.LocaleStorage;

public class MCPSkinUtils {
	public static void modifyProfile(GameProfile eplayer) {
		SkinProfile skinprofile = SkinStorage.getInstance().getOrCreateSkinData(eplayer.getName().toLowerCase());
		SkinProperty property = skinprofile.getSkinProperty();

		if (property == null) {
			return;
		}

		Property prop = new Property(property.getName(), property.getValue(), property.getSignature());

		eplayer.getProperties().get("textures").clear();
		eplayer.getProperties().put(prop.getName(), prop);

	}

	public static void onload() {
		// Not customizing configs
		// ConfigStorage.getInstance().init(cfgInputStream, false);
		LocaleStorage.init();
		SkinStorage.init();
	}
}
