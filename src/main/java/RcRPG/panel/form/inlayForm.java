package RcRPG.panel.form;

import RcRPG.Handle;
import RcRPG.RcRPGMain;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class inlayForm {
    private static final String NO_STONE = "无";
    private static final String SUCCESS_MESSAGE = "宝石操作成功！";
    private Stone beforeStone;
    private int beforeClick;
    private Item handItem = null;
    private Weapon weaponItem = null;
    private Armour armourItem = null;
    private LinkedList<Stone> originStones = new LinkedList<>();

    public void makeInlayForm(Player player, Item item) {
        handItem = item;
        //Object state;
        if (Weapon.isWeapon(item)) {
            weaponItem = RcRPGMain.loadWeapon.get(item.getNamedTag().getString("name"));
            //state = weaponItem;
        } else if (Armour.isArmour(item)) {
            armourItem = RcRPGMain.loadArmour.get(item.getNamedTag().getString("name"));
            //state = armourItem;
        } else {
            return;
        }

        FormWindowSimple form = getStateWindow();
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> handleStateWindowResponse(form, player)));
        player.showFormWindow(form);
    }


    private FormWindowSimple getStateWindow() {
        String label = "";
        LinkedList<Stone> stones = new LinkedList<>();
        ArrayList<String> stoneSlots = new ArrayList<>();
        if (weaponItem != null) {
            stones = Weapon.getStones(handItem);
            label = weaponItem.getLabel();
            stoneSlots = weaponItem.getStoneList();
        } else if (armourItem != null) {
            stones = Armour.getStones(handItem);
            label = armourItem.getLabel();
            stoneSlots = armourItem.getStoneList();
        }

        int stoneCount = stones.size();
        String slotShow = "";
        if (!stoneSlots.isEmpty()) {
            // 构建特定格式的字符串
            StringBuilder stringBuilder = new StringBuilder();
            for (String stone : stoneSlots) {
                stringBuilder.append("- ").append(stone).append(" -\n");
            }
            slotShow = stringBuilder.toString();
        }
        this.originStones = stones;
        return new FormWindowSimple(
                "§f"+label + "宝石列表",
                stoneCount > 0 ? "装备拥有 " + stoneCount + " 个宝石槽：\n"+slotShow : "本装备没有宝石槽",
                stones.stream()
                        .map(stone -> new ElementButton(stone == null ? NO_STONE : stone.getShowName()))
                        .collect(Collectors.toList()));
    }

    private void handleStateWindowResponse(FormWindowSimple form, Player player) {
        if (form.wasClosed()) {
            return;
        }
        FormResponseSimple response = form.getResponse();
        int clicked = response.getClickedButtonId();
        String yamlName;
        if (this.originStones.get(clicked) == null) {
            yamlName = NO_STONE;
        } else {
            yamlName = this.originStones.get(clicked).getName();
        }

        beforeClick = response.getClickedButtonId();
        beforeStone = NO_STONE.equals(yamlName) ? null : Handle.getStoneViaName(yamlName);

        String type = null;
        if (weaponItem != null) {
            type = weaponItem.getStoneList().get(response.getClickedButtonId());
        } else if (armourItem != null) {
            type = armourItem.getStoneList().get(response.getClickedButtonId());
        }

        LinkedList<String> playerStones = Stone.getStonesViaType(player, type);
        if (!playerStones.contains(NO_STONE)) playerStones.addLast(NO_STONE);

        FormWindowCustom form_ = getStoneWindow(yamlName, playerStones);
        form_.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form_.wasClosed()) return;
            handleStoneWindowResponse(form_, player);
        }));
        player.showFormWindow(form_);
    }

    private FormWindowCustom getStoneWindow(String yamlName, LinkedList<String> stones) {
        ElementDropdown dropdown = new ElementDropdown("", stones);
        dropdown.setDefaultOptionIndex(0);
        return new FormWindowCustom(yamlName, Collections.singletonList(dropdown));
    }

    private void handleStoneWindowResponse(FormWindowCustom form, Player player) {
        String itemName = "";
        LinkedList<Stone> stones = new LinkedList<>();
        if (weaponItem != null) {
            stones = Weapon.getStones(handItem);
            itemName = weaponItem.getName();
        } else if (armourItem != null) {
            stones = Armour.getStones(handItem);
            itemName = armourItem.getName();
        }

        FormResponseCustom response = form.getResponse();
        FormResponseData responseData = response.getDropdownResponse(0);
        // 选择的是 yamlName 还是 NO_STONE
        String responseContent = responseData.getElementContent();

        Item item = player.getInventory().getItemInHand();
        if (item.getNamedTag() == null) return;
        if (!itemName.equals(item.getNamedTag().getString("name"))) return;
        player.sendMessage(SUCCESS_MESSAGE);

        int clickedButtonId = beforeClick;
        Stone newStone = NO_STONE.equals(responseContent) ? null : Handle.getStoneViaName(responseContent);

        for (int i = 0; i < stones.size(); i++) {
            if (i == clickedButtonId) {
                if (newStone != null) {
                    stones.set(i, newStone);
                } else {
                    stones.remove(i);
                }
                break;
            }
        }

        if (newStone != null) {
            Handle.removeStoneViaName(player, responseContent);
        }
        if (beforeStone != null) {// 过去的宝石
            Stone.giveStone(player, beforeStone.getName(), 1);
        }

        if (weaponItem != null) {
            Weapon.setStone(player, handItem, stones);
        } else if (armourItem != null) {
            Armour.setStone(player, handItem, stones);
        }
        //makeInlayForm(player, player.getInventory().getItemInHand());
    }

}