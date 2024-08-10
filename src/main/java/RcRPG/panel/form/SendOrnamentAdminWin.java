package RcRPG.panel.form;

import RcRPG.RcRPGMain;
import RcRPG.RPG.Ornament;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.lang.LangCode;

public class SendOrnamentAdminWin implements Listener {

    public SendOrnamentAdminWin(Player player) {
        LangCode langCode = player.getLanguageCode();
        FormWindowSimple form = new FormWindowSimple(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.ornament.title"), RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.select_config_manage"));
        for (String key : RcRPGMain.loadOrnament.keySet()) {
            form.addButton(new ElementButton(key));
        }
        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) {
                return;
            }
            FormResponseSimple response = form.getResponse();
            String key = response.getClickedButton().getText();
            SendStoneOptionsWin(player, key);
        }));
        player.showFormWindow(form);
    }

    public void SendStoneOptionsWin(Player player, String ornamentKey) {
        Ornament ornament = RcRPGMain.loadOrnament.get(ornamentKey);

        FormWindowSimple form = new FormWindowSimple("RcRPG管理 - " + ornamentKey,
                "显示名称: " + ornament.getShowName() +
                        "\n§r标签: " + ornament.getLabel() +
                        "\n§r物品ID: " + ornament.getConfig().get("物品ID") +
                        "\n§r介绍: " + ornament.getMessage());
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
                    if (Ornament.giveOrnament(player, ornamentKey, 1)) {
                        player.sendMessage("给予成功");
                    } else {
                        player.sendMessage("给予失败");
                    }
                }
                case 1 -> {
                    player.sendMessage("还没完成这个");
                }
                case 2 -> {
                    new SendOrnamentAdminWin(player);
                }
            }
        }));
        player.showFormWindow(form);
    }
}