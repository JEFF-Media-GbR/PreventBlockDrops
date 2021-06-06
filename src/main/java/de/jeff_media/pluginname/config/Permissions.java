package de.jeff_media.pluginname.config;

import de.jeff_media.pluginname.PluginName;

import java.util.Locale;

public class Permissions {

    private static final String PREFIX = PluginName.getInstance().getName().toLowerCase(Locale.ROOT) + ".";

    public static final String RELOAD = PREFIX + "reload";

}
