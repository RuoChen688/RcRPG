package RcRPG.Form;

import RcRPG.Handle;
import RcRPG.Main;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import cn.nukkit.Player;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class inlayForm{

    public static LinkedHashMap<Player,Stone> playerStone = new LinkedHashMap<>();
    public static LinkedHashMap<Player,Integer> playerClick = new LinkedHashMap<>();

    public static void makeForm_one_weapon(Player player,Item item){
        Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
        LinkedList<Stone> list = Weapon.getStones(item);
        List<ElementButton> elementButtons = new LinkedList<>();
        for(Stone stone : list){
            if(stone == null){
                elementButtons.add(new ElementButton("无"));
            }else{
                elementButtons.add(new ElementButton(stone.getLabel()));
            }
        }
        FormWindowSimple form = new FormWindowSimple(weapon.getLabel()+"宝石列表","",elementButtons);
        player.showFormWindow(form,10086);
    }

    public static void makeForm_one_armour(Player player,Item item){
        Armour armour = Main.loadArmour.get(item.getNamedTag().getString("name"));
        LinkedList<Stone> list = Armour.getStones(item);
        List<ElementButton> elementButtons = new LinkedList<>();
        for(Stone stone : list){
            if(stone == null){
                elementButtons.add(new ElementButton("无"));
            }else{
                elementButtons.add(new ElementButton(stone.getLabel()));
            }
        }
        FormWindowSimple form = new FormWindowSimple(armour.getLabel()+"宝石列表","",elementButtons);
        player.showFormWindow(form,10087);
    }

    public static void makeForm_two_weapon(Player player, Item item, FormResponseSimple response){
        LinkedList<String> list = Stone.getStones(player,response.getClickedButton().getText());
        list.add(0,response.getClickedButton().getText());
        if(!list.contains("无")) list.add("无");
        ElementDropdown dropdown = new ElementDropdown("",list);
        dropdown.setDefaultOptionIndex(0);
        List<Element> elementList = new LinkedList<>();
        elementList.add(dropdown);
        FormWindowCustom form = new FormWindowCustom(response.getClickedButton().getText(),elementList);
        player.showFormWindow(form,10010);
        inlayForm.playerClick.put(player, response.getClickedButtonId());
        if(response.getClickedButton().getText().equals("无")){
            inlayForm.playerStone.put(player, null);
        }else{
            inlayForm.playerStone.put(player, Handle.getStoneByLabel(response.getClickedButton().getText()));
        }
    }

    public static void makeForm_two_armour(Player player, Item item, FormResponseSimple response){
        LinkedList<String> list = Stone.getStones(player,response.getClickedButton().getText());
        list.add(0,response.getClickedButton().getText());
        if(!list.contains("无")) list.add("无");
        ElementDropdown dropdown = new ElementDropdown("",list);
        dropdown.setDefaultOptionIndex(0);
        List<Element> elementList = new LinkedList<>();
        elementList.add(dropdown);
        FormWindowCustom form = new FormWindowCustom(response.getClickedButton().getText(),elementList);
        player.showFormWindow(form,10011);
        inlayForm.playerClick.put(player, response.getClickedButtonId());
        if(response.getClickedButton().getText().equals("无")){
            inlayForm.playerStone.put(player, null);
        }else{
            inlayForm.playerStone.put(player, Handle.getStoneByLabel(response.getClickedButton().getText()));
        }
    }

}
