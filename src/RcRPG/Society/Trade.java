package RcRPG.Society;

import RcRPG.Panel.DisplayPanel;
import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Trade {

    public static ArrayList<Player> playerList = new ArrayList<>();

    public static LinkedHashMap<Player,Player> playerTrade = new LinkedHashMap<>();

    public static LinkedHashMap<Player,Integer> player = new LinkedHashMap<>();

    public static LinkedHashMap<Player,ArrayList<Item>> playItems = new LinkedHashMap<>();

    public static LinkedHashMap<Player, DisplayPanel> playerPanel = new LinkedHashMap<>();

    public static void setTrade(Player player){
        if(Trade.playerList.contains(player)){
            Trade.playerList.remove(player);
        }else{
            Trade.playerList.add(player);
        }
    }

    public static void addTrade(Player player,Player to){
        Trade.playerTrade.put(player,to);
        Trade.player.put(player,1);
        Trade.playerTrade.put(to,player);
        Trade.player.put(to,2);
    }

    public static void removeTrade(Player player){
        Trade.playerTrade.remove(Trade.playerTrade.get(player));
        Trade.player.remove(Trade.playerTrade.get(player));
        Trade.playerTrade.remove(player);
        Trade.player.remove(player);
    }

    public static void addItem(Player player,Item item){
        ArrayList<Item> list;
        if(Trade.playItems.containsKey(player)){
            list = Trade.playItems.get(player);
        }else{
            list = new ArrayList<>();
        }
        list.add(item);
        Trade.playItems.put(player,list);
    }

    public static void removeItem(Player player,Item item){
        ArrayList<Item> list = Trade.playItems.get(player);
        list.remove(item);
        if(list.isEmpty()){
            Trade.playItems.remove(player);
        }else{
            Trade.playItems.put(player,list);
        }
    }

    public static void PyFail(Player player){
        Player to = Trade.playerTrade.get(player);
        for(Item item : Trade.playItems.get(to)){
            to.getInventory().addItem(item);
        }
        Trade.playItems.remove(to);
        for(Item item : Trade.playItems.get(player)){
            to.getInventory().addItem(item);
        }
        Trade.playItems.remove(player);
    }

    public static void PySuc(Player player){
        Player to = Trade.playerTrade.get(player);
        for(Item item : Trade.playItems.get(player)){
            to.getInventory().addItem(item);
        }
        Trade.playItems.remove(to);
        for(Item item : Trade.playItems.get(to)){
            to.getInventory().addItem(item);
        }
        Trade.playItems.remove(player);
    }

    public static boolean isTrade(Player player){
        return Trade.playerList.contains(player);
    }

}
