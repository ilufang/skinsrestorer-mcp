/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package skinsrestorer.shared.storage;

import java.io.File;

import javax.sql.rowset.CachedRowSet;

import skinsrestorer.shared.format.Profile;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.utils.DataFiles;
import skinsrestorer.shared.utils.MySQL;

public class SkinStorage {

	private static SkinStorage instance = new SkinStorage();
	private static DataFiles cache;
	private static MySQL mysql;

	public static SkinStorage getInstance() {
		return instance;
	}

	public static void init() {
		cache = new DataFiles("plugins" + File.separator + "SkinsRestorer" + File.separator + "", "cache");

	}

	public static void init(MySQL mysql) {
		SkinStorage.mysql = mysql;
	}

	public boolean isSkinDataForced(String name) {
		// Ummmmmmmmmm
		if (ConfigStorage.getInstance().USE_MYSQL) {
			// w/e
			return false;
		} else {
			return true;
		}
	}

	public void removeSkinData(String name) {
		if (ConfigStorage.getInstance().USE_MYSQL) {
			mysql.execute(mysql.prepareStatement(
					"delete from " + ConfigStorage.getInstance().MYSQL_TABLE + " where Nick=?", name));
		} else {
			name = name.toLowerCase();

			cache.set(name + ".value", null);
			cache.set(name + ".signature", null);
			cache.set(name + ".timestamp", null);
			cache.save();
		}

	}

	public void setSkinData(String name, SkinProfile profile) {
		if (ConfigStorage.getInstance().USE_MYSQL) {
			CachedRowSet crs = mysql.query(mysql.prepareStatement(
					"select * from " + ConfigStorage.getInstance().MYSQL_TABLE + " where Nick=?", name));

			if (crs == null)
				mysql.execute(mysql.prepareStatement(
						"insert into " + ConfigStorage.getInstance().MYSQL_TABLE
								+ " (Nick, Value, Signature, Timestamp) values (?,?,?,?)",
						name, profile.getSkinProperty().getValue(), profile.getSkinProperty().getSignature(),
						String.valueOf(System.currentTimeMillis())));
			else
				mysql.execute(mysql.prepareStatement(
						"update " + ConfigStorage.getInstance().MYSQL_TABLE
								+ " set Value=?, Signature=?, Timestamp=? where Nick=?",
						profile.getSkinProperty().getValue(), profile.getSkinProperty().getSignature(),
						String.valueOf(System.currentTimeMillis()), name));
		} else {
			name = name.toLowerCase();

			cache.set(name + ".value", profile.getSkinProperty().getValue());
			cache.set(name + ".signature", profile.getSkinProperty().getSignature());
			cache.set(name + ".timestamp", System.currentTimeMillis());
			cache.save();
		}
	}

	// Justin case
	public SkinProfile getOrCreateSkinData(String name) {
		if (ConfigStorage.getInstance().USE_MYSQL) {

			SkinProfile sp;
			if ((sp = getSkinData(name)) == null)
				return new SkinProfile(new Profile(null, name), null, 0, false);
			else
				return sp;
		} else {
			name = name.toLowerCase();

			SkinProfile emptyprofile = new SkinProfile(new Profile(null, name), null, 0, false);

			Long timestamp = System.currentTimeMillis();

			try {
				timestamp = Long.parseLong(cache.getString(name + ".timestamp"));
			} catch (Throwable e) {
			}

			SkinProfile profile = new SkinProfile(new Profile(null, name), new SkinProperty("textures",
					cache.getString(name + ".value"), cache.getString(name + ".signature")), timestamp, false);

			if (profile.getSkinProperty().getSignature() != null)
				return profile;
			else
				return emptyprofile;

		}

	}

	public SkinProfile getSkinData(String name) {
		if (ConfigStorage.getInstance().USE_MYSQL) {

			CachedRowSet crs = mysql.query(mysql.prepareStatement(
					"select * from " + ConfigStorage.getInstance().MYSQL_TABLE + " where Nick=?", name.toLowerCase()));

			if (crs == null) {
				return null;
			} else {
				try {
					String value = crs.getString("Value");
					String signature = crs.getString("Signature");
					String timestamp = crs.getString("Timestamp");

					return new SkinProfile(new Profile(null, name), new SkinProperty("textures", value, signature),
							Long.valueOf(timestamp), false);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return null;

		} else {
			name = name.toLowerCase();

			Long timestamp = System.currentTimeMillis();

			try {
				timestamp = Long.parseLong(cache.getString(name + ".timestamp"));
			} catch (Throwable e) {
			}

			SkinProfile profile = new SkinProfile(new Profile(null, name), new SkinProperty("textures",
					cache.getString(name + ".value"), cache.getString(name + ".signature")), Long.valueOf(timestamp),
					true);

			if (profile.getSkinProperty().getSignature() == null)
				return null;

			return profile;

		}
	}
}
