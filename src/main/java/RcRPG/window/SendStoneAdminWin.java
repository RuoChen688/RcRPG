package RcRPG.window;

import RcRPG.RcRPGMain;
import RcRPG.RPG.Stone;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.lang.LangCode;

public class SendStoneAdminWin implements Listener { //一般实际开发中不在这个类中写监听器

    public SendStoneAdminWin(Player player) {
        LangCode langCode = player.getLanguageCode();
        FormWindowSimple form = new FormWindowSimple(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.stone.title"), RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.select_config_manage"));
        for (String key : RcRPGMain.loadStone.keySet()) {
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

    public void SendStoneOptionsWin(Player player, String stoneKey) {
        Stone stone = RcRPGMain.loadStone.get(stoneKey);

        FormWindowSimple form = new FormWindowSimple("RcRPG管理 - " + stoneKey,
                "显示名称: " + stone.getShowName() +
                        "\n§r标签: " + stone.getLabel() +
                        "\n§r物品ID: " + stone.getConfig().get("物品ID") +
                        "\n§r介绍: " + stone.getMessage());
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
                    if (Stone.giveStone(player, stoneKey, 1)) {
                        player.sendMessage("给予成功");
                    } else {
                        player.sendMessage("给予失败");
                    }
                }
                case 1 -> {
                    player.sendMessage("还没完成这个");
                }
                case 2 -> {
                    new SendStoneAdminWin(player);
                }
            }
        }));
        player.showFormWindow(form);
    }
}