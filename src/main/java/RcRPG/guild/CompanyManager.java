package RcRPG.guild;

import RcRPG.RcRPGMain;
import cn.nukkit.Player;

import java.util.HashMap;

public class CompanyManager {
    private HashMap<String, Company> companies = new HashMap<>();

    public boolean createCompany(String companyName, Player founder) {
        if (companies.containsKey(companyName)) {
            RcRPGMain.getInstance().getLogger().info("Company " + companyName + " already exists.");
            return false;
        }
        // 默认公司初始资金
        int initialFunds = 100000;

        // 创建公司
        Company company = new Company(companyName, initialFunds, 10);
        company.addShareholder(founder, 8);  // 初始分配给公司创始人的股权

        // 公司信息保存
        companies.put(companyName, company);
        return true;
    }

    public boolean assignShares(String companyName, Player shareholder, Player recipient, int shares) {
        Company company = companies.get(companyName);

        if (company == null) {
            RcRPGMain.getInstance().getLogger().info("Company " + companyName + " does not exist.");
            return false;
        }
        int shareholderShares = company.getShareholder(shareholder);

        if (shareholderShares < shares) {
            RcRPGMain.getInstance().getLogger().info(shareholder.getName() + " does not have enough shares to transfer.");
            return false;
        }

        // 转让股权
        return company.transferShareholder(shareholder, recipient, shares);
    }

    public Company getCompany(String companyName) {
        return companies.get(companyName);
    }
}