package de.jeff_media.pluginname.config;

import de.jeff_media.pluginname.PluginName;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class ConfigUpdater {

    private static PluginName main = PluginName.getInstance();

    private static final String[] NODES_NEEDING_DOUBLE_QUOTES = {"message-"};
    private static final String[] NODES_NEEDING_SINGLE_QUOTES = {"test-"};
    private static final String[] LINES_CONTAINING_STRING_LISTS = {"disabled-worlds:"};
    private static final String[] LINES_IGNORED = {"config-version:", "plugin-version:"};
    private static final String[] LINES_CONTAINING_NEWLINES = {}; // TODO: ADD THIS

    private static final boolean debug = false;

    public static void updateConfig() {
        Logger logger = main.getLogger();

        logger.info("===========================================");
        logger.info("You are using an outdated config file.");
        logger.info("Your config file will now be updated to the");
        logger.info("newest version. You changes will be kept.");
        logger.info("===========================================");

        backupCurrentConfig();
        main.saveDefaultConfig();

        Set<String> oldConfigNodes = main.getConfig().getKeys(false);
        ArrayList<String> newConfig = new ArrayList<>();

        for(String defaultLine : getNewConfigAsArrayList()) {

            String updatedLine = defaultLine;

            //noinspection StatementWithEmptyBody
            if(defaultLine.startsWith("-") || defaultLine.startsWith(" -") || defaultLine.startsWith("  -")) {

            }
            else //noinspection StatementWithEmptyBody
                if(lineContainsIgnoredNode(defaultLine)) {

            }
            else if(lineIsStringList(defaultLine)) {
                updatedLine = null;
                newConfig.add(defaultLine);
                String node = defaultLine.split(":")[0];
                for(String entry : main.getConfig().getStringList(node)) {
                    newConfig.add("- " + entry);
                }
            }
            else {
                for(String node : oldConfigNodes) {
                    if (defaultLine.startsWith(node+":")) {
                        String quotes = getQuotes(node);
                        updatedLine = node + ": " + quotes + main.getConfig().get(node).toString() + quotes;
                    }
                }
            }

            if(updatedLine != null) {
                newConfig.add(updatedLine);
            }
        }

        saveArrayListToConfig(newConfig);
    }

    private static String getQuotes(String line) {
        for(String test : NODES_NEEDING_DOUBLE_QUOTES) {
            if (line.startsWith(test)) {
                return "\"";
            }
        }
        for(String test : NODES_NEEDING_SINGLE_QUOTES) {
            if(line.startsWith(test)) {
                return "'";
            }
        }
        return "";
    }

    private static boolean lineIsStringList(String line) {
        for(String test : LINES_CONTAINING_STRING_LISTS) {
            if(line.startsWith(test)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineContainsIgnoredNode(String line) {
        for(String test : LINES_IGNORED) {
            if(line.startsWith(test)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> getNewConfigAsArrayList() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(getFilePath("config.yml")), StandardCharsets.UTF_8);
            return lines;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    private static void saveArrayListToConfig(List<String> lines) {
        try {
            FileWriter writer = new FileWriter(getFilePath("config.yml"));
            for(String line : lines) {
                writer.write(line + System.lineSeparator());
            }
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static String getFilePath(String fileName) {
        return main.getDataFolder() + File.separator + fileName;
    }

    private static long getNewConfigVersion(PluginName main) {
        InputStream in = main.getClass().getResourceAsStream("/config-version.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            return Long.parseLong(reader.readLine());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return 0;
        }

    }

    private static void backupCurrentConfig() {
        File oldFile = new File(getFilePath("config.yml"));
        File newFile = new File(getFilePath("config-backup-"+main.getConfig().getString(Config.CONFIG_PLUGIN_VERSION)+".yml"));
        if(newFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            newFile.delete();
        }

        //noinspection ResultOfMethodCallIgnored
        oldFile.getAbsoluteFile().renameTo(newFile.getAbsoluteFile());
    }
}
