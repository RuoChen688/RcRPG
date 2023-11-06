package RcRPG.Form;

import RcRPG.Handle;
import RcRPG.Main;
import RcRPG.Society.Guild;
import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;

import java.util.ArrayList;

public class guildForm {

    public static void make_one(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        if(config.getString("公会").equals(Main.instance.config.getString("初始公会"))){
            FormWindowSimple form = new FormWindowSimple("公会系统","你当前并未加入公会\n点击按钮加入/创建公会吧");
            form.addButton(new ElementButton("加入公会"));
            form.addButton(new ElementButton("创建公会"));
            player.showFormWindow(form,20000);
        }else{
            String guild = Guild.getGuild(player);
            Config guildConfig = Guild.getGuildConfig(guild);
            String text = "公会名称: " + guild + "\n公会等级: " + guildConfig.getString("公会等级") + "\n公会资金: " + Guild.getMoney(player) + "\n公会人数: " + Guild.getSize(guild) + "/" + guildConfig.get("公会人数") + "\n我的职务: " + Guild.getStatus(player);
            FormWindowSimple form = new FormWindowSimple("公会系统",text);
            form.addButton(new ElementButton("公会贡献"));
            form.addButton(new ElementButton("公会成员"));
            form.addButton(new ElementButton("传送至公会基地"));
            if(Guild.isMaster(player) || Guild.isAssistantMaster(player)){
                form.addButton(new ElementButton("公会升级"));
                form.addButton(new ElementButton("公会申请"));
                if(Guild.isMaster(player)){
                    form.addButton(new ElementButton("设置副会长"));
                    form.addButton(new ElementButton("设置公会基地"));
                    form.addButton(new ElementButton("解散公会"));
                }
            }
            player.showFormWindow(form,20004);
        }
    }

    public static void make_join(Player player){
        ElementDropdown dropdown = new ElementDropdown("选择一个公会加入吧", Handle.getGuilds());
        ArrayList<Element> list = new ArrayList<>();
        list.add(dropdown);
        FormWindowCustom form = new FormWindowCustom("公会系统",list);
        player.showFormWindow(form,20001);
    }

    public static void make_create(Player player){
        ElementLabel elementLabel = new ElementLabel("创建公会需要消耗"+Main.instance.config.getString("公会创建初始资金"));
        ElementInput elementInput = new ElementInput("输入你的公会名字");
        ArrayList<Element> list = new ArrayList<>();
        list.add(elementLabel);
        list.add(elementInput);
        FormWindowCustom form = new FormWindowCustom("公会系统",list);
        player.showFormWindow(form,20002);
    }

    public static void create_failed(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","公会创建失败\n请检查你的公会名称或者金币数量");
        ElementButton button = new ElementButton("返回");
        form.addButton(button);
        player.showFormWindow(form,20003);
    }

    public static void make_offer(Player player){
        ElementSlider slider = new ElementSlider("贡献资金",0,20000,500);
        ArrayList<Element> list = new ArrayList<>();
        list.add(slider);
        FormWindowCustom form = new FormWindowCustom("公会系统",list);
        player.showFormWindow(form,20005);
    }

    public static void make_member(Player player){
        String guild = Guild.getGuild(player);
        Config config = Guild.getGuildConfig(guild);
        String assistantMaster = config.get("副会长") == null ? "暂无" : config.getString("副会长");
        ArrayList<String> list = (ArrayList<String>) config.getStringList("成员");
        FormWindowSimple form;
        if(Guild.isMaster(player)){
            if(!assistantMaster.equals("暂无")) list.add(0,assistantMaster);
            list.add(0,player.getName());
            form = new FormWindowSimple("公会系统","");
            for(String member : list){
                form.addButton(new ElementButton(member));
            }
        }else{
            String text = "会长: " + config.getString("会长") + "\n副会长: " + assistantMaster + "\n成员: \n";
            for(String member : list){
                text += member+"\n";
            }
            form = new FormWindowSimple("公会系统",text);
        }
        player.showFormWindow(form,20006);
    }

    public static void make_app(Player player){
        String guild = Guild.getGuild(player);
        FormWindowSimple form = new FormWindowSimple("公会系统","");
        if(Guild.getApp(guild).isEmpty()){
            form.setContent("当前没有人要加入公会哦");
        }else{
            for(String app : Guild.getApp(guild)){
                form.addButton(new ElementButton(app));
            }
        }
        player.showFormWindow(form,20007);
    }

    public static void make_dismiss(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","你确定要解散公会吗？");
        form.addButton(new ElementButton("确定"));
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20008);
    }

    public static void offer_s(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","捐献成功，感谢你为公会的建设贡献一份力量");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20009);
    }

    public static void offer_f(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","捐献失败，金币不足！！！");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20010);
    }

    public static void member_s(Player player,String name){
        FormWindowSimple form = new FormWindowSimple("公会系统","玩家名: " + name);
        form.addButton(new ElementButton("踢出"));
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20011);
    }

    public static void member_f(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","你没有权限操作该成员");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20012);
    }

    public static void member_m(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","你确定要退出公会吗？");
        form.addButton(new ElementButton("确定"));
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20013);
    }

    public static void app_s(Player player,String name){
        FormWindowSimple form = new FormWindowSimple("公会系统","玩家名: " + name);
        form.addButton(new ElementButton("同意"));
        form.addButton(new ElementButton("拒绝"));
        player.showFormWindow(form,20014);
    }

    public static void a_s(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","成功发送申请");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20015);
    }

    public static void a_f(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","公会人数已满");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20016);
    }

    public static void make_update(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","公会升级消耗金币: " + Guild.getUpdateMoney(player) + "\n是否升级？");
        form.addButton(new ElementButton("确定"));
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20017);
    }

    public static void update_s(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","升级成功");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20018);
    }

    public static void update_f(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","升级失败");
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20019);
    }

    public static void make_assistant(Player player){
        FormWindowSimple form = new FormWindowSimple("公会系统","");
        ArrayList<String> list = Guild.getAllMember(player);
        list.remove(Guild.getMaster(player));
        if(Guild.getAssistantMaster(player) != null) list.remove(Guild.getAssistantMaster(player));
        ArrayList<ElementButton> buttons = new ArrayList<>();
        for(String member : list){
            buttons.add(new ElementButton(member));
        }
        for(ElementButton button : buttons){
            form.addButton(button);
        }
        player.showFormWindow(form,20020);
    }

    public static void as_s(Player player,String name){
        FormWindowSimple form = new FormWindowSimple("公会系统","设置成功,副会长设置为: " + name);
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form,20021);
    }

}
