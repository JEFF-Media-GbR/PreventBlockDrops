package de.jeff_media.pluginname.config;

import de.jeff_media.pluginname.PluginName;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.Locale;

public class Config {

    private final PluginName main = PluginName.getInstance();
    private final FileConfiguration conf = main.getConfig();
    private final Metrics metrics = new Metrics(main,main.getBstatsId());

    public static final String CHECK_FOR_UPDATES = "check-for-updates";
    public static final String CHECK_FOR_UPDATES_INTERVAL = "update-check-interval";
    public static final String CONFIG_VERSION = "config-version";
    public static final String CONFIG_PLUGIN_VERSION = "plugin-version";
    public static final String DEBUG = "debug";

    public Config() {
        addDefault(CHECK_FOR_UPDATES, "true");
        addDefault(CHECK_FOR_UPDATES_INTERVAL, 4);
        addDefault(DEBUG, false);

        addAllMetrics();
    }

    /**
     * Adds a default value to a config node
     * @param node Config node name
     * @param defaultValue Default value
     */
    private void addDefault(String node, Object defaultValue) {
        conf.addDefault(node, defaultValue);
    }

    /**
     * Registers a config node to bStats' Metrics. The value is automatically fetched from the config.
     * @param node Config node name
     */
    private void addMetric(String node) {
        metrics.addCustomChart(new Metrics.SimplePie(node.toLowerCase(Locale.ROOT).replace("-","_"),() ->String.valueOf(conf.get(node))));
    }

    /**
     * Using Reflection to register all config nodes to bStats.
     * Performance doesn't matter, it's only called in onEnable() and getting some String fields
     * really doesn't hurt anyone.
     */
    private void addAllMetrics() {
        Class<? extends Config> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            if(field.getType().equals(String.class)) {
                try {
                    String key = (String) field.get(this);
                    String value = String.valueOf(conf.get(key));
                    System.out.println("Registering metrics \"" + key+"\" = \"" + value + "\"");
                    addMetric(key);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
