package org.example.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
@TableName("user_with_json")
public class User {
    @TableId
    Long id;
    String name;
    String password;
    JsonNode userInfo;
}
