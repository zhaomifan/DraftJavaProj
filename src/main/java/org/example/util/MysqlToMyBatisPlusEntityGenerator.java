package org.example.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlToMyBatisPlusEntityGenerator {
    // MySQL数据类型到Java数据类型的映射
    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
    
    static {
        TYPE_MAPPING.put("int", "Integer");
        TYPE_MAPPING.put("integer", "Integer");
        TYPE_MAPPING.put("bigint", "Long");
        TYPE_MAPPING.put("varchar", "String");
        TYPE_MAPPING.put("char", "String");
        TYPE_MAPPING.put("text", "String");
        TYPE_MAPPING.put("datetime", "LocalDateTime");
        TYPE_MAPPING.put("date", "LocalDate");
        TYPE_MAPPING.put("time", "LocalTime");
        TYPE_MAPPING.put("timestamp", "LocalDateTime");
        TYPE_MAPPING.put("float", "Float");
        TYPE_MAPPING.put("double", "Double");
        TYPE_MAPPING.put("decimal", "BigDecimal");
        TYPE_MAPPING.put("boolean", "Boolean");
        TYPE_MAPPING.put("tinyint", "Integer");
    }
    
    public static void main(String[] args) {
        // 示例MySQL建表语句
        String createTableSql = "-- auto-generated definition\n" +
                "create table crs_msg_rule_user_group\n" +
                "(\n" +
                "    id             bigint unsigned auto_increment comment '主键ID'\n" +
                "        primary key,\n" +
                "    group_code     varchar(90)                 not null comment '分组编码',\n" +
                "    group_name     varchar(120)                not null comment '分组名称',\n" +
                "    description    varchar(4000)               null comment '描述',\n" +
                "    enable_flag    varchar(3)                  not null comment '启用标识',\n" +
                "    create_time    datetime                    null comment '创建时间',\n" +
                "    create_user_id varchar(32)                 not null comment '创建人',\n" +
                "    update_time    datetime                    not null comment '最后更新时间',\n" +
                "    update_user_id varchar(32)                 not null comment '最后更新人',\n" +
                "    deleted        bigint unsigned default '0' not null comment '逻辑删除标识：0-未删除，1-已删除',\n" +
                "    version        int unsigned    default '0' not null comment '版本号',\n" +
                "    app_code       varchar(32)                 null comment '所属应用',\n" +
                "    tenant_code    varchar(64)                 null comment '租户编码',\n" +
                "    org_code       varchar(60)                 null comment '组织编码',\n" +
                "    constraint QEM_MSG_RULE_USER_GROUP_U1\n" +
                "        unique (group_code)\n" +
                ")\n" +
                "    comment '消息接收人分组表';\n" +
                "\n";
        
        // 生成Entity类代码
        String entityCode = generateEntity(createTableSql);
        System.out.println(entityCode);
    }
    
    /**
     * 根据MySQL建表语句生成MyBatis-Plus风格的Entity类代码
     */
    public static String generateEntity(String createTableSql) {
        // 解析表名
        String tableName = parseTableName(createTableSql);
        String className = camelCase(tableName, true);
        
        // 解析表注释
        String tableComment = parseTableComment(createTableSql);
        
        // 解析字段信息
        List<ColumnInfo> columns = parseColumns(createTableSql);
        
        // 构建Entity类代码
        StringBuilder sb = new StringBuilder();
        
        // 添加包名和导入
        sb.append("package com.example.entity;\n\n");
        sb.append("import com.baomidou.mybatisplus.annotation.*;\n");
        sb.append("import lombok.Data;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.time.LocalDate;\n");
        sb.append("import java.time.LocalTime;\n");
        sb.append("import java.math.BigDecimal;\n\n");
        
        // 添加类注释
        if (tableComment != null && !tableComment.isEmpty()) {
            sb.append("/**\n");
            sb.append(" * ").append(tableComment).append("\n");
            sb.append(" */\n");
        }
        
        // 添加Lombok注解
        sb.append("@Data\n");
        
        // 添加MyBatis-Plus表注解
        sb.append("@TableName(\"").append(tableName).append("\")\n");
        
        // 添加类定义
        sb.append("public class ").append(className).append(" {\n\n");
        
        // 生成字段
        for (ColumnInfo column : columns) {
            // 字段注释
            if (column.getComment() != null && !column.getComment().isEmpty()) {
                sb.append("    /**\n");
                sb.append("     * ").append(column.getComment()).append("\n");
                sb.append("     */\n");
            }
            
            // MyBatis-Plus主键注解
            if (column.isPrimaryKey()) {
                sb.append("    @TableId");
                if (column.isAutoIncrement()) {
                    sb.append("(type = IdType.AUTO)");
                }
                sb.append("\n");
            } else {
                // 非主键字段注解
                sb.append("    @TableField(\"").append(column.getName()).append("\")\n");
            }
            
            // 逻辑删除字段识别（如果字段名是deleted）
            if ("deleted".equals(column.getName())) {
                sb.append("    @TableLogic\n");
            }
            
            // 自动填充创建时间（如果字段名是create_time）
            if ("create_time".equals(column.getName())) {
                sb.append("    @TableField(fill = FieldFill.INSERT)\n");
            }
            
            // 自动填充更新时间（如果字段名是update_time）
            if ("update_time".equals(column.getName())) {
                sb.append("    @TableField(fill = FieldFill.INSERT_UPDATE)\n");
            }
            
            // 字段定义
            sb.append("    private ").append(column.getJavaType()).append(" ").append(column.getJavaName()).append(";\n\n");
        }
        
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 解析表名
     */
    private static String parseTableName(String sql) {
        Pattern pattern = Pattern.compile("CREATE TABLE `(.*?)`");
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "UnknownTable";
    }
    
    /**
     * 解析表注释
     */
    private static String parseTableComment(String sql) {
        Pattern pattern = Pattern.compile("COMMENT='(.*?)'");
        Matcher matcher = pattern.matcher(sql);
        // 查找最后一个匹配（表注释）
        String comment = null;
        while (matcher.find()) {
            comment = matcher.group(1);
        }
        return comment;
    }
    
    /**
     * 解析字段信息
     */
    private static List<ColumnInfo> parseColumns(String sql) {
        List<ColumnInfo> columns = new ArrayList<>();
        
        // 提取表结构部分
        Pattern pattern = Pattern.compile("\\((.*?)\\) ENGINE=", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            return columns;
        }
        
        String tableStructure = matcher.group(1);
        
        // 分割字段
        String[] columnLines = tableStructure.split(",\\s*`");
        
        // 解析主键
        String primaryKey = parsePrimaryKey(tableStructure);
        
        // 处理每个字段
        for (String line : columnLines) {
            // 处理第一个字段（前面没有`）
            if (!line.startsWith("`")) {
                line = "`" + line;
            }
            
            ColumnInfo column = parseColumn(line, primaryKey);
            if (column != null) {
                columns.add(column);
            }
        }
        
        return columns;
    }
    
    /**
     * 解析单个字段信息
     */
    private static ColumnInfo parseColumn(String line, String primaryKey) {
        // 跳过键定义
        if (line.startsWith("`PRIMARY KEY") || line.startsWith("`UNIQUE KEY") || line.startsWith("`KEY")) {
            return null;
        }
        
        ColumnInfo column = new ColumnInfo();
        
        // 解析字段名
        Pattern namePattern = Pattern.compile("`(.*?)`");
        Matcher nameMatcher = namePattern.matcher(line);
        if (nameMatcher.find()) {
            column.setName(nameMatcher.group(1));
            column.setJavaName(camelCase(column.getName(), false));
        }
        
        // 判断是否为主键
        if (column.getName() != null && column.getName().equals(primaryKey)) {
            column.setPrimaryKey(true);
        }
        
        // 解析数据类型
        Pattern typePattern = Pattern.compile("` (.*?) ");
        Matcher typeMatcher = typePattern.matcher(line);
        if (typeMatcher.find()) {
            String dbType = typeMatcher.group(1).split("\\(")[0].toLowerCase();
            column.setDbType(dbType);
            column.setJavaType(getJavaType(dbType));
        }
        
        // 解析是否非空
        column.setNotNull(line.contains("NOT NULL"));
        
        // 解析是否自增
        column.setAutoIncrement(line.contains("AUTO_INCREMENT"));
        
        // 解析注释
        Pattern commentPattern = Pattern.compile("COMMENT '(.*?)'");
        Matcher commentMatcher = commentPattern.matcher(line);
        if (commentMatcher.find()) {
            column.setComment(commentMatcher.group(1));
        }
        
        return column;
    }
    
    /**
     * 解析主键字段名
     */
    private static String parsePrimaryKey(String tableStructure) {
        Pattern pattern = Pattern.compile("PRIMARY KEY \\(`(.*?)`\\)");
        Matcher matcher = pattern.matcher(tableStructure);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * 将下划线命名转换为驼峰命名
     */
    private static String camelCase(String name, boolean capitalizeFirstLetter) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = capitalizeFirstLetter;
        
        for (char c : name.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 获取对应的Java数据类型
     */
    private static String getJavaType(String dbType) {
        return TYPE_MAPPING.getOrDefault(dbType, "Object");
    }
    
    /**
     * 字段信息封装类
     */
    private static class ColumnInfo {
        private String name;          // 数据库字段名
        private String dbType;        // 数据库类型
        private String javaType;      // Java类型
        private String javaName;      // Java属性名
        private String comment;       // 注释
        private boolean isPrimaryKey; // 是否为主键
        private boolean isNotNull;    // 是否非空
        private boolean isAutoIncrement; // 是否自增
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDbType() { return dbType; }
        public void setDbType(String dbType) { this.dbType = dbType; }
        
        public String getJavaType() { return javaType; }
        public void setJavaType(String javaType) { this.javaType = javaType; }
        
        public String getJavaName() { return javaName; }
        public void setJavaName(String javaName) { this.javaName = javaName; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public boolean isPrimaryKey() { return isPrimaryKey; }
        public void setPrimaryKey(boolean primaryKey) { isPrimaryKey = primaryKey; }
        
        public boolean isNotNull() { return isNotNull; }
        public void setNotNull(boolean notNull) { isNotNull = notNull; }
        
        public boolean isAutoIncrement() { return isAutoIncrement; }
        public void setAutoIncrement(boolean autoIncrement) { isAutoIncrement = autoIncrement; }
    }
}
    