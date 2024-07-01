package RcRPG.window;

import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.lang.LangCode;

public class RcRPGAdminWin implements Listener {

    public RcRPGAdminWin(Player player) {
        LangCode langCode = player.getLanguageCode();
        FormWindowSimple form = new FormWindowSimple(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.main.title"), RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.select_config_manage"));
        form.addButton(new ElementButton(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.main.button1"), new ElementButtonImageData("path", "textures/items/iron_sword")));
        form.addButton(new ElementButton(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.main.button2"), new ElementButtonImageData("path", "textures/items/chainmail_chestplate")));
        form.addButton(new ElementButton(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.main.button3"), new ElementButtonImageData("path", "textures/items/amethyst_shard")));
        form.addButton(new ElementButton(RcRPGMain.getI18n().tr(langCode, "rcrpg.manage.window.main.button4"), new ElementButtonImageData("path", "textures/items/ender_eye")));

        form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
            if (form.wasClosed()) {
                return;
            }
            FormResponseSimple response = form.getResponse();
            int key = response.getClickedButtonId();
            switch (key) {
                case 0 -> {
                    new SendWeaponAdminWin(player);
                }
                case 1 -> {
                    new SendArmourAdminWin(player);
                }
                case 2 -> {
                    new SendStoneAdminWin(player);
                }
                case 3 -> {
                    new SendOrnamentAdminWin(player);
                }
            }
        }));
        player.showFormWindow(form);
    }
}
