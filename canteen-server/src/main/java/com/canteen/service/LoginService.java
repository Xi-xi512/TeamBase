package com.canteen.service;


import com.canteen.dao.UserDAO;
import com.canteen.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务类
 * 负责用户身份验证和角色管理
 *
 * 重构说明：
 * - 移除硬编码的 USERS Map
 * - 通过 UserDAO 接口访问用户数据
 * - 支持依赖注入，便于测试
 */
@Service
public class LoginService {
    @Autowired
    private UserDAO userDAO;

    /**
     * 用户登录验证
     * @param username 账号
     * @param password 密码
     * @return 登录成功的用户对象，失败返回 null
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ 账号不能为空！");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ 密码不能为空！");
            return null;
        }

        User user = userDAO.findByUsername(username);

        if (user == null) {
            System.out.println("❌ 账号不存在！");
            return null;
        }

        if (!user.getPassword().equals(password.trim())) {
            System.out.println("❌ 密码错误！");
            return null;
        }

        System.out.println("✅ 登录成功！欢迎，" + user.getName());
        return user;
    }

    /**
     * 获取测试账号列表（从文件读取）
     * @return 账号信息字符串
     */
    public String getTestAccounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== 测试账号 ==========\n");

        java.util.List<User> users = userDAO.findAll();

        if (users.isEmpty()) {
            sb.append("暂无用户数据，请先创建用户文件\n");
        } else {
            for (User user : users) {
                sb.append(String.format("  账号：%s, 密码：%s (%s) - 角色：%s\n",
                        user.getUsername(),
                        user.getPassword(),
                        user.getName(),
                        user.getRole()));
            }
        }

        sb.append("==============================\n");
        return sb.toString();
    }
}

