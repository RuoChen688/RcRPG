package RcRPG.panel.form;

import RcRPG.Handle;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static RcRPG.RcRPGMain.i18n;

public class inlayForm {
    private final Player player;
    private final String NO_STONE;
    private final String SUCCESS_MESSAGE;
    private Stone beforeStone;
    private int beforeClick;
    private Item handItem = null;
    private Weapon weaponItem = null;
    private Armour armourItem = null;
    private LinkedList<Stone> originStones = new LinkedList<>();

    public inlayForm(Player player) {
        this.player = player;
        this.NO_STONE = i18n.tr(player.getLanguageCode(), "rcrpg.window.inlay.no_stone");
        this.SUCCESS_MESSAGE = i18n.tr(player.getLanguageCode(), "rcrpg.window.inlay.success_message");
    }

    public void makeInlayForm(Item item) {
        handItem = item;
        if (Weapon.isWeapon(item)) {
            weaponItem = RcRPGMain.loadWeapon.get(item.getNamedTag().getString("name"));
        } else if (Armour.isArmour(item)) {
            armourItem = RcRPGMain.loadArmour.get(item.getNamedTag().getString("name"));
        } else {
            return;
        }

        FormWindowSimple form = getStateWindow();
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> handleStateWindowResponse(form, player)));
        player.showFormWindow(form);
    }


    private FormWindowSimple getStateWindow() {
        String label = "";
        // 镶嵌在装备上的宝石列表
        LinkedList<Stone> stones = new LinkedList<>();
        // 装备可镶嵌的宝石类型列表
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
                i18n.tr(player.getLanguageCode(), "rcrpg.window.inlay.title", label),
                stoneCount > 0 ? i18n.tr(player.getLanguageCode(), "rcrpg.window.inlay.title.hasSlot", stoneCount, slotShow).replace("\\n", "\n") : i18n.tr(player.getLanguageCode(), "rcrpg.window.inlay.title.notSlot"),
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
            if (form_.wasClosed()) {
                // 若玩家点了 X 则返回上一级表单
                makeInlayForm(player.getInventory().getItemInHand());
                return;
            }
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
        // 装备 yamlName 名字
        String itemName = "";
        LinkedList<Stone> stones = new LinkedList<>();
        if (weaponItem != null) {
            stones = Weapon.getStones(handItem);
            itemName = weaponItem.getName();
        } else if (armourItem != null) {
            stones = Armour.getStones(handItem);
            itemName = armourItem.getName();
        }

        // 选择的宝石是 yamlName 还是 NO_STONE
        String selectedStoneName = form.getResponse().getDropdownResponse(0).getElementContent();

        // 检查手持装备是否变动
        Item item = player.getInventory().getItemInHand();
        if (item.getNamedTag() == null) return;
        if (!itemName.equals(item.getNamedTag().getString("name"))) return;

        // 先发送成功提示
        if (NO_STONE.equals(selectedStoneName) && stones.get(beforeClick) == null) {
            // ignored
        } else {
            player.sendMessage(SUCCESS_MESSAGE);
        }

        Stone newStone = NO_STONE.equals(selectedStoneName) ? null : Handle.getStoneViaName(selectedStoneName);

        // 扣除新宝石
        if (newStone != null) {
            stones.set(beforeClick, newStone);
            Handle.removeStoneViaName(player, selectedStoneName);
        } else {
            stones.set(beforeClick, null);
        }

        // 返还原有的宝石
        if (beforeStone != null) {
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