package org.example.Tool;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.Entity.User;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;


public class JWT_Utils
{
    /*
     * 公用密钥-保存在服务端，客户端是不知道密钥的，以防被攻击
     * */
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //生成JWT
    public static String JWT_Creat(long id) throws Exception
    {
        //签发时间
        Date iaDate = new Date();

        //过期时间
        Calendar NowTime = Calendar.getInstance();
        NowTime.add(Calendar.DAY_OF_WEEK,7);
        Date expiresDate = NowTime.getTime();

        String token = Jwts.builder()
                .setSubject(String.valueOf(id))
//                .claim("username", username)
//                .claim("Password",password);
//                .claim("email", email)
//                .claim("role", role)
                .setIssuedAt(iaDate)
                .setExpiration(expiresDate)
                .signWith(SECRET_KEY)   // 用安全密钥
                .compact();

        return token;
    }

    //解析JWT
    public static String JWT_Parse(String token) throws Exception
    {
        //解析token
        var claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)   //验证签名
                .build()
                .parseClaimsJws(token)
                .getBody();

        //获取subject
        String id = claims.getSubject();

        return id;
    }
}
