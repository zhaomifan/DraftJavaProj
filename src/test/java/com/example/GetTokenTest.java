package com.example;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class GetTokenTest {

    public static void main(String[] args) {
        // ========== 1. 配置你的参数 ==========
        String clientId = "XCOA.crcgas";
        String clientSecret = "e7****************"; // 接口给的clientSecret
        String keyHex = "96a****************"; // 接口给的32位16进制密钥
        // 1. 拼接明文
        String plainText = clientId + "#" + clientSecret;

        // 2. 十六进制key 转 16字节密钥（修复报错核心）
        byte[] key = HexUtil.decodeHex(keyHex);

        // 3. SM4 加密
        SM4 sm4 = new SM4(Mode.ECB, Padding.PKCS5Padding, key);
        byte[] encryptBytes = sm4.encrypt(plainText);
        String content = Base64.encode(encryptBytes);

        // 4. 发起 GET 请求
        String url = "https://rig-gateway-uat.crcgas.com/crs-uat/oauth/token?content=" + content;


        // ===================== 重点：带请求头 GET =====================
        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", "Bearer eyJ**************************************") // 添加请求头 auth
                .timeout(10000)
                .execute();


        // 输出结果
        System.out.println("加密后 content: " + content);
        System.out.println("接口返回状态码: " + response.getStatus());
        System.out.println("接口返回内容: " + response.body());
    }
}