package com.alinesno.infra.base.gateway.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.time.DateUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description JWT跨域管理token工具类
 * @author JL
 * @date 2019/08/08
 * @version v1.0.0
 */
public class JwtTokenUtils {

    /**
     JSON Web Token（缩写 JWT）是目前最流行的跨域认证解决方案。
     JWT 的三个部分依次如下
     Header（头部）:是一个 JSON 对象，描述 JWT 的元数据{ "alg": "HS256", typ": "JWT" }
     Payload（负载）:也是一个 JSON 对象，用来存放实际需要传递的数据,JWT 规定了7个官方字段:
         iss (issuer)：签发人
         exp (expiration time)：过期时间
         sub (subject)：主题
         aud (audience)：受众
         nbf (Not Before)：生效时间
         iat (Issued At)：签发时间
         jti (JWT ID)：编号
     Signature（签名）:对前两部分的签名，防止数据篡改

     1.JWT中Header头和Payload有效载荷序列化的算法都用到了Base64URL，签名哈希部分是对Header与Payload两部分数据签名
     2.客户端接收服务器返回的JWT，将其存储在Cookie或localStorage中，客户端将在与服务器交互中都会带JWT，将它放入HTTP请求的Header Authorization字段中
     3.JWT的最大缺点是服务器不保存会话状态，所以在使用期间不可能取消令牌或更改令牌的权限
     4.JWT本身包含认证信息，因此一旦信息泄露，任何人都可以获得令牌的所有权限
     5.JWT不建议使用HTTP协议来传输代码，而是使用加密的HTTPS协议进行传输
     */

    /**
     * 加密密钥
     */
    private static final String SECRET = "$1$b1373b8398f94e47bcbec4c013179da7-!@*&^$@#$%^&*()_+=%*%$#_+=";

    /**
     * jwt创建token，考虑安全性，token中不因该放入太多信息（勿放密码之类的敏感信息），只放入关键字段值即可，如用户ID
     * @param sub     主题（可以放入关键数据，如:userid, 用户唯一值等）
     * @param timeout 过期时长（秒）
     * @param secretKey 用户加盐值
     * @return
     */
    public static String createToken(String sub, int timeout, String secretKey) {
        Date expiresAt = null;
        if (timeout > 0){
            expiresAt = DateUtils.addSeconds(new Date(), timeout);
        }
        return createToken(sub, expiresAt, secretKey);
    }

    /**
     * jwt创建token，考虑安全性，token中不因该放入太多信息（勿放密码之类的敏感信息），只放入关键字段值即可，如用户ID
     * @param sub     主题（可以放入关键数据，如:userid, 用户唯一值等）
     * @param expiresAt 过期时长
     * @param secretKey 用户加盐值
     * @return
     */
    public static String createToken(String sub, Date expiresAt, String secretKey) {
        JWTCreator.Builder builder = JWT.create();
        //主题
        builder.withSubject(sub);
        builder.withIssuer("pro-server");
        //过期时间,小于0则不设置过期时间
        if (expiresAt != null) {
            builder.withExpiresAt(expiresAt);
        }
        String md5Crypt = getMd5Crypt(secretKey);
        return builder.sign(Algorithm.HMAC256(md5Crypt));
    }

    /**
     * 对jwt创建的token进行验签与解析，返回Subject（主题）中存放的内容
     * @param token
     * @param secretKey 用户加盐值
     * @return
     * @throws TokenExpiredException          会话超时异常
     * @throws SignatureVerificationException 验签无效异常
     */
    public static String parseToken(String token, String secretKey) throws TokenExpiredException, SignatureVerificationException {
        String md5Crypt = getMd5Crypt(secretKey);
        return JWT.require(Algorithm.HMAC256(md5Crypt)).build().verify(token).getSubject();
    }

    private static String getMd5Crypt(String secretKey){
        return Md5Crypt.md5Crypt(secretKey.getBytes(Charset.forName("utf8")), SECRET);
    }

    /**
     * jwt创建token，考虑安全性，token中不因该放入太多信息（勿放密码之类的敏感信息）
     * @param loadMap   数据集合
     * @param timeout   过期时长（秒）
     * @return
     */
    public static String createToken(Map<String, Object> loadMap, int timeout) {
        JWTCreator.Builder builder = JWT.create();
        loadMap.forEach((k, v) -> {
            if (v instanceof String) {
                builder.withClaim(k, (String) v);
            } else if (v instanceof Date) {
                builder.withClaim(k, (Date) v);
            } else if (v instanceof Long) {
                builder.withClaim(k, (Long) v);
            } else if (v instanceof Integer) {
                builder.withClaim(k, (Integer) v);
            } else if (v instanceof Boolean) {
                builder.withClaim(k, (Boolean) v);
            }
        });
        builder.withIssuer("pro-server");
        if (timeout>0) {
            //过期时间
            builder.withExpiresAt(DateUtils.addSeconds(new Date(), timeout));
        }
        return builder.sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 对jwt创建的token进行验签与解析，返回集合
     * @param token
     * @return
     * @throws TokenExpiredException          会话超时异常
     * @throws SignatureVerificationException 验签无效异常
     */
    public static Map<String, Object> parseTokenToMap(String token) throws TokenExpiredException, SignatureVerificationException {
        Map<String, Claim> claimMap = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getClaims();
        if (claimMap == null){
            return null;
        }
        Map<String, Object> loadMap = new HashMap<>();
        claimMap.forEach((k, v) -> {
            Object obj = null;
            if (v.asString() != null) {
                obj = v.asString();
            } else if (v.asBoolean() != null) {
                obj = v.asBoolean();
            } else if (v.asDate() != null || v.asLong() != null) {//Date类型按Long方式来处理
                obj = v.asLong();
            } else if (v.asInt() != null) {
                obj = v.asInt();
            }
            loadMap.put(k, obj);
        });
        return loadMap;
    }

    //测试方法
    public static void main(String[] args) {
        /*
        //创建token和解析token
        String subject = "userid_001";
        System.out.println("新建subject = " + subject);
        String jwtToken = JwtTokenUtils.createToken(subject, 60, "123456");
        System.out.println("生成token = " + jwtToken);
        String [] parts = jwtToken.split("\\.");
        for (int i=0;i<2;i++){
            System.out.println(new String(Base64.decodeBase64(parts[i]), StandardCharsets.UTF_8));
        }
        System.out.println(parts[parts.length-1]);
        try {
            subject = JwtTokenUtils.parseToken(jwtToken, "123456");
            System.out.println("解析subject = " + subject);
        } catch(TokenExpiredException tee){
            throw new TokenExpiredException("token已过有效期，请重新申请：" + tee.getMessage());
        } catch(SignatureVerificationException sve){
            //验证签名不通过（数据被篡改过）
            throw sve;
        }
        */
        //过期测试
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyaWRfMDAxIiwiaXNzIjoicHJvLXNlcnZlciIsImV4cCI6MTYzMjc0NDk2NX0.AKkM3TtxY75zUkCdOcaB4wlEPITnNKXTkF-8sqLgXAc";
        String subject = JwtTokenUtils.parseToken(jwtToken, "123456");

        //无效验鉴（将最后一位更改为z）
//        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyaWRfMDAxIiwiaXNzIjoicHJvLXNlcnZlciIsImV4cCI6MTYzMjc0NDk2NX0.AKkM3TtxY75zUkCdOcaB4wlEPITnNKXTkF-8sqLgXAz";
//        String subject = JwtTokenUtils.parseToken(jwtToken,  "123456");

        //将多种数据放入集合中，通过jwt创建token
        /*
        Map<String, Object> loadMap = new HashMap<>();
        loadMap.put("userId", (Long) 1000000L);
        loadMap.put("userName", "test");
        loadMap.put("isLogin", true);
        String jwtToken = JwtTokenUtils.createToken(loadMap, 60);
        System.out.println(jwtToken);

        Map<String, Object> loadMap2 = JwtTokenUtils.parseTokenToMap(jwtToken);
        for (Map.Entry<String, Object> entry : loadMap2.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        */
    }
}