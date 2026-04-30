package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试json
 *
 * @author zhaokf
 * @date 17:26 2025/9/15
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;


    @RequestMapping("/save")
    public String save(@RequestBody User user) {
        userService.save(user);
        return "保存成功！";
    }

    @RequestMapping("/list")
    public List<User> list(@RequestBody User user) {
        return userService.lambdaQuery().lt(User::getName, user.getName()).list();
    }
}
