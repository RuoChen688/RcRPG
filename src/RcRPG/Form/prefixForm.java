package RcRPG.Form;

import RcRPG.Handle;
import RcRPG.Society.Prefix;
import cn.nukkit.Player;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class prefixForm {

    public static void makeForm(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        ArrayList<String> list = Prefix.getPrefix(player);
        ElementDropdown dropdown = new ElementDropdown("当前称号为"+config.getString("称号"),list);
        List<Element> elementList = new ArrayList<>();
        elementList.add(dropdown);
        FormWindowCustom form = new FormWindowCustom("称号系统",elementList);
        player.showFormWindow(form,10000);
    }

}
