package RcRPG.panel.form;

import RcRPG.RcRPGMain;
import RcRPG.RPG.Armour;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.lang.LangCode;

public class SendArmourAdminWin implements Listener {

    public SendArmourAdminWin(Player player) {
        LangCode langCode = player.getLanguageCode();
        FormWindowSimple form = new FormWindowSimple(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.armour.title"), RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.select_config_manage"));
        for (String key : RcRPGMain.loadArmour.keySet()) {
            form.addButton(new ElementButton(key));
        }
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) {
                return;
            }
            FormResponseSimple response = form.getResponse();
            String key = response.getClickedButton().getText();
            SendArmourOptionsWin(player, key);
        }));
        player.showFormWindow(form);
    }

    public void SendArmourOptionsWin(Player player, String armourKey) {
        Armour armour = RcRPGMain.loadArmour.get(armourKey);

        FormWindowSimple form = new FormWindowSimple("RcRPG管理 - " + armourKey,
                "显示名称: " + armour.getShowName() +
                        "\n§r标签: " + armour.getLabel() +
                        "\n§r物品ID: " + armour.getConfig().get("物品ID") +
                        "\n§r介绍: " + armour.getMessage());
        form.addButton(new ElementButton("获取"));
        form.addButton(new ElementButton("修改 (未完成)"));
        form.addButton(new ElementButton("返回"));
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) {
                return;
            }
            FormResponseSimple response = form.getResponse();
            int key = response.getClickedButtonId();
            switch (key) {
                case 0 -> {
                    if (Armour.giveArmour(player, armourKey, 1)) {
                        player.sendMessage("给予成功");
                    } else {
                        player.sendMessage("给予失败");
                    }
                }
                case 1 -> {
                    player.sendMessage("还没完成这个");
                }
                case 2 -> {
                    new SendArmourAdminWin(player);
                }
            }
        }));
        player.showFormWindow(form);
    }
}