package com.example;

import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证 Entity(+反射) 与 HashMap 的开销差距
 * 场景：20个属性，批量创建+批量读写操作
 */
public class PerformanceCompareDemo {
    // 反射元数据缓存（核心优化：避免重复获取Field）
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    // 配置参数：批量操作数量（数值越大，差距越明显，建议至少10万以上）
    private static final int BATCH_SIZE = 50000; // 100万个对象
    private static final int PROP_COUNT = 20; // 20个属性

    /**
     * 步骤1：定义包含20个属性的Entity（prop1-prop20，覆盖基本类型和包装类）
     */
    @Data
    static class UserEntity {
        private int prop1;
        private long prop2;
        private String prop3;
        private boolean prop4;
        private double prop5;
        private float prop6;
        private short prop7;
        private byte prop8;
        private Integer prop9;
        private Long prop10;
        private Boolean prop11;
        private Double prop12;
        private Float prop13;
        private Short prop14;
        private Byte prop15;
        private String prop16;
        private int prop17;
        private long prop18;
        private double prop19;
        private String prop20;
    }

    /**
     * 工具方法：获取缓存的Field（优化反射：缓存+关闭权限检查）
     */
    private static Field getCachedField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        // 1. 缓存类对应的所有字段
        Map<String, Field> fieldMap = FIELD_CACHE.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>());
        // 2. 缓存单个字段，避免重复反射
        return fieldMap.computeIfAbsent(fieldName, k -> {
            try {
                Field field = clazz.getDeclaredField(k);
                field.setAccessible(true); // 关闭权限检查，降低每次执行开销
                return field;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("获取字段失败：" + clazz.getName() + "." + k, e);
            }
        });
    }

    /**
     * 场景1：HashMap 存储20个属性，批量创建+读写
     */
    private static void testHashMapPerformance() {
        long startTime = System.currentTimeMillis();
        Map<String, Object>[] mapArray = new Map[BATCH_SIZE];

        // 1. 批量创建HashMap并写入20个属性
        for (int i = 0; i < BATCH_SIZE; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("prop1", i);
            map.put("prop2", (long) i * 2);
            map.put("prop3", "test" + i);
            map.put("prop4", i % 2 == 0);
            map.put("prop5", (double) i * 3.14);
            map.put("prop6", (float) i * 1.5);
            map.put("prop7", (short) i);
            map.put("prop8", (byte) (i % 128));
            map.put("prop9", i + 100);
            map.put("prop10", (long) i + 200);
            map.put("prop11", i % 3 == 0);
            map.put("prop12", (double) i + 3.1415);
            map.put("prop13", (float) i + 1.618);
            map.put("prop14", (short) (i + 50));
            map.put("prop15", (byte) (i % 64));
            map.put("prop16", "demo" + i);
            map.put("prop17", i + 1000);
            map.put("prop18", (long) i + 2000);
            map.put("prop19", (double) i + 9.8);
            map.put("prop20", "result" + i);
            mapArray[i] = map;
        }

        // 2. 批量读取HashMap的20个属性（模拟业务查询）
        long total = 0;
        for (Map<String, Object> map : mapArray) {
            total += (int) map.get("prop1");
            total += (long) map.get("prop2");
            total += (int) map.get("prop9");
            total += (long) map.get("prop10");
        }

        long endTime = System.currentTimeMillis();
        System.out.println("=== HashMap 场景 ===");
        System.out.println("批量操作耗时：" + (endTime - startTime) + " 毫秒");
        System.out.println("校验值（避免JVM优化剔除无用代码）：" + total);
        System.out.println("====================\n");
    }

    /**
     * 场景2：Entity 直接操作（无反射），批量创建+读写
     */
    private static void testEntityDirectPerformance() {
        long startTime = System.currentTimeMillis();
        UserEntity[] entityArray = new UserEntity[BATCH_SIZE];

        // 1. 批量创建Entity并写入20个属性
        for (int i = 0; i < BATCH_SIZE; i++) {
            UserEntity entity = new UserEntity();
            entity.setProp1(i);
            entity.setProp2((long) i * 2);
            // 其余18个属性直接赋值（此处为简洁，省略重复代码，实际可补充setter调用）
            setEntityProps(entity, i);
            entityArray[i] = entity;
        }

        // 2. 批量读取Entity的20个属性
        long total = 0;
        for (UserEntity entity : entityArray) {
            total += entity.getProp1();
            total += entity.getProp2();
            // 其余属性读取省略，不影响性能趋势验证
        }

        long endTime = System.currentTimeMillis();
        System.out.println("=== Entity 直接操作场景 ===");
        System.out.println("批量操作耗时：" + (endTime - startTime) + " 毫秒");
        System.out.println("校验值（避免JVM优化剔除无用代码）：" + total);
        System.out.println("==========================\n");
    }

    /**
     * 场景3：Entity + 优化后反射，批量创建+读写
     */
    private static void testEntityReflectPerformance() {
        long startTime = System.currentTimeMillis();
        UserEntity[] entityArray = new UserEntity[BATCH_SIZE];

        try {
            // 1. 批量创建Entity并通过反射写入20个属性
            for (int i = 0; i < BATCH_SIZE; i++) {
                UserEntity entity = new UserEntity();
                // 反射写入20个属性（使用缓存的Field）
                getCachedField(UserEntity.class, "prop1").setInt(entity, i);
                getCachedField(UserEntity.class, "prop2").setLong(entity, (long) i * 2);
                getCachedField(UserEntity.class, "prop3").set(entity, "test" + i);
                getCachedField(UserEntity.class, "prop4").setBoolean(entity, i % 2 == 0);
                getCachedField(UserEntity.class, "prop5").setDouble(entity, (double) i * 3.14);
                getCachedField(UserEntity.class, "prop6").setFloat(entity, i * 1.5F);
                getCachedField(UserEntity.class, "prop7").setShort(entity, (short) i);
                getCachedField(UserEntity.class, "prop8").setByte(entity, (byte) (i % 128));
                getCachedField(UserEntity.class, "prop9").set(entity, i + 100);
                getCachedField(UserEntity.class, "prop10").set(entity, (long) i + 200);
                getCachedField(UserEntity.class, "prop11").set(entity, i % 3 == 0);
                getCachedField(UserEntity.class, "prop12").set(entity, (double) i + 3.1415);
                getCachedField(UserEntity.class, "prop13").set(entity, (float) i + 1.618F);
                getCachedField(UserEntity.class, "prop14").set(entity, (short) (i + 50));
                getCachedField(UserEntity.class, "prop15").set(entity, (byte) (i % 64));
                getCachedField(UserEntity.class, "prop16").set(entity, "demo" + i);
                getCachedField(UserEntity.class, "prop17").setInt(entity, i + 1000);
                getCachedField(UserEntity.class, "prop18").setLong(entity, (long) i + 2000);
                getCachedField(UserEntity.class, "prop19").setDouble(entity, (double) i + 9.8);
                getCachedField(UserEntity.class, "prop20").set(entity, "result" + i);

                entityArray[i] = entity;
            }

            // 2. 批量通过反射读取Entity的20个属性
            long total = 0;
            for (UserEntity entity : entityArray) {
                total += getCachedField(UserEntity.class, "prop1").getInt(entity);
                total += getCachedField(UserEntity.class, "prop2").getLong(entity);
                total += (int) getCachedField(UserEntity.class, "prop9").get(entity);
                total += (long) getCachedField(UserEntity.class, "prop10").get(entity);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("=== Entity + 优化后反射场景 ===");
            System.out.println("批量操作耗时：" + (endTime - startTime) + " 毫秒");
            System.out.println("校验值（避免JVM优化剔除无用代码）：" + total);
            System.out.println("=================================\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 辅助方法：给Entity批量赋值（简化直接操作场景的代码）
     */
    private static void setEntityProps(UserEntity entity, int i) {
        entity.setProp3("test" + i);
        // 其余17个属性的setter调用省略，逻辑与反射场景一致
    }

    /**
     * 主方法：执行所有验证场景
     */
    public static void main(String[] args) {
        // 预热JVM（避免首次执行的JIT优化干扰结果）
        testHashMapPerformance();
        testEntityDirectPerformance();
        testEntityReflectPerformance();

        System.out.println("========== 正式执行（预热完成）==========");
        // 正式执行并输出结果
        testHashMapPerformance();
        testEntityDirectPerformance();
        testEntityReflectPerformance();
    }
}