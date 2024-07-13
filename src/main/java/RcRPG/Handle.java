package RcRPG;

import RcRPG.RPG.Stone;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Handle {

    public static Config getPlayerConfig(String name) {
        File file = new File(RcRPGMain.getInstance().getDataFolder() + "/Players/" + name + ".yml");
        if (file.exists()) {
            return new Config(file, Config.YAML);
        }
        return null;
    }

    public static String[] getDefaultFiles(String fileName) {
        List<String> names = new ArrayList<>();
        File files = new File(RcRPGMain.getInstance().getDataFolder() + File.separator + fileName);
        if (files.isDirectory()) {
            File[] filesArray = files.listFiles();
            if (filesArray != null) {
                for (File file : filesArray) {
                    names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }
        return names.toArray(new String[0]);
    }

    public static List<String> getGuilds() {
        List<String> names = new ArrayList<>();
        File files = new File(RcRPGMain.getInstance().getDataFolder() + "/Guild");
        if (files.isDirectory()) {
            File[] filesArray = files.listFiles();
            if (filesArray != null) {
                for (File file : filesArray) {
                    names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }
        return names;
    }

    public static Effect StringToEffect(String s) {
        String[] ss = s.split(":");
        return Effect.getEffect(Integer.parseInt(ss[0])).setAmplifier(Integer.parseInt(ss[1]) - 1).setDuration(Integer.parseInt(ss[2]) * 20);
    }

    public static Stone getStoneViaName(String yamlName) {
        if (RcRPGMain.loadStone.containsKey(yamlName)) {
            return RcRPGMain.loadStone.get(yamlName);
        }
        return null;
    }

    public static void removeStoneViaName(Player player, String yamlName) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            Item item = player.getInventory().getItem(i);
            if (Stone.isStone(item)) {
                if (item.getNamedTag().getString("name").equals(yamlName)) {
                    if (item.getCount() == 1) {
                        player.getInventory().remove(item);
                    } else {
                        item.setCount(item.getCount() - 1);
                        player.getInventory().setItem(i, item);
                    }
                    break;
                }
            }
        }
    }

    public static boolean canRemove(Player player, String[] s) {
        String type = s[0];
        String name = s[1];
        int count = Integer.parseInt(s[2]);
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            Item item = player.getInventory().getItem(i);
            if (item.getNamedTag() != null && item.getNamedTag().contains("name") && item.getNamedTag().contains("type")) {
                if (item.getNamedTag().getString("type").equals(type) && item.getNamedTag().getString("name").equals(name)) {
                    if (count != 0) {
                        if (item.getCount() >= count) {
                            return true;
                        } else {
                            count -= item.getCount();
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return count == 0;
    }

    public static void remove(Player player, String[] s) {
        String type = s[0];
        String name = s[1];
        int count = Integer.parseInt(s[2]);
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            Item item = player.getInventory().getItem(i);
            if (item.getNamedTag() != null && item.getNamedTag().contains("name") && item.getNamedTag().contains("type")) {
                if (item.getNamedTag().getString("type").equals(type) && item.getNamedTag().getString("name").equals(name)) {
                    if (count != 0) {
                        if (item.getCount() >= count) {
                            item.setCount(item.getCount() - count);
                            player.getInventory().setItem(i, item);
                            break;
                        } else {
                            count -= item.getCount();
                            player.getInventory().remove(item);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public static int random(int a, int b) {
        return new Random().nextInt(b - a + 1) + a;
    }

    public static boolean getProbabilisticResults(double value) {
        if (Double.isNaN(value)) {
            return false;
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.#####");
            value = Double.parseDouble(decimalFormat.format(value));
        }

        int length = 0;
        String strValue = String.valueOf(value);
        if (value <= 0) {
            return false;
        } else if (value < 1) {
            length = strValue.length() - 2;
            value = value * Math.pow(10, length);
        } else if (value >= 1) {
            return true;
        }

        int randomValue = Integer.parseInt((String.valueOf(Math.random())).substring(3, length + 3));
        return randomValue + 1 <= value;
    }

    /**
     * 检查类是否存在
     */
    public static boolean classExists(final String classPath) {
        try {
            Class.forName(classPath);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}

