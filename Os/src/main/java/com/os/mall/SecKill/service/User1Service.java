package com.os.mall.SecKill.service;

import com.os.mall.SecKill.redis.RedisService1;
import com.os.mall.SecKill.util.MD5Hash;
import com.os.mall.service.UserService;
import com.os.mall.SecKill.Result.CodeMessage;
import com.os.mall.SecKill.dao.UserDao;
import com.os.mall.SecKill.entity.User1;
import com.os.mall.SecKill.recParam.LoginRP;
import com.os.mall.SecKill.redis.UserKey;
import com.os.mall.SecKill.util.MD5;
import com.os.mall.SecKill.util.UUID;
import com.os.mall.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class User1Service {
    @Autowired
    UserDao userDao;
    @Autowired
    RedisService1 redisService1;
    @Autowired
    UserService userService;
    public static final String COOKIE_NAME_TOKEN="token";
    public CodeMessage updatePass(long id,String newpass,String formPass,String token){
        User1 user1 =this.getById(id);
        if (user1 ==null)
        {
            return CodeMessage.LoginMobile_ERROR;
        }
        else {  //用户存在
            User1 newUser1 =new User1();
            newUser1.setId(id);
            newUser1.setPassword(MD5.formToDB(formPass, user1.getSalt()));
            //更新数据库
            userDao.update(newUser1);

            //更新缓存  改Userkey对应的
            redisService1.delete(UserKey.getById,""+id);
            redisService1.delete(UserKey.getByName,""+ user1.getUsername());
            //改一下密码 下一行使用
            user1.setPassword(newUser1.getPassword());
            //防止无法登录对token进行更新而不是删除
            redisService1.set(UserKey.token,token, user1);
            return CodeMessage.SUCCESS;
        }

    }
    public User1 getById(long id){
        User1 user1 = redisService1.get(UserKey.getById,""+id, User1.class);
        if(user1 !=null){
            return user1;
        }else {
            //缓存找不到 则 从数据库中查找
            user1 =userDao.getById(id);
            if (user1 !=null){
                //已经从数据库取出 再放到缓存中
                System.out.println("从数据库重新写入缓存了！");
                redisService1.set(UserKey.getById,""+id, user1);
            }
            return user1;
        }
//        return userDao.getById(id);
    }
    public User1 getByNickName(String nickname){
        return userDao.getByNickName(nickname);
    }

    public CodeMessage login(HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest, LoginRP loginRP) {
        if(loginRP ==null) return  CodeMessage.SERVER_ERROR;
        String id= loginRP.getMobile();
        User user= userService.findByUsername(loginRP.getMobile());

//        User1 user1 =getById(Long.parseLong(id));
        String passForm= loginRP.getPassword();
        if(user ==null){
            return CodeMessage.LoginMobile_EMPTY;
        }
        String pass= MD5Hash.toMD5(loginRP.getPassword());
        if (pass.equals(user.getPassword())){
            String token = UUID.uuid();
            System.out.println("ttttttt"+token);

            addCookie1(user,token,httpServletResponse,httpServletRequest);
//            CodeMessage codeMessage1=new CodeMessage(0,token); //0的时候代表成功
            System.out.println("-------------------");
            return new CodeMessage(token,0); //登录成功  返回的对象 便于获得token
        }
        else {
            return CodeMessage.Pass_ERROR;//密码错误
        }
        /*
        //
        String DBPassword= user1.getPassword();
        String DBSalt= user1.getSalt(); //数据库的盐
        String PassRight= MD5.formToDB(passForm,DBSalt); //form转数据库密码后的密码
        if(DBPassword.equals(PassRight))    //验证最终密码是否相同
        {
//            //生成cookie
//            String token = UUID.uuid();
//            redisService.set(UserKey.token,token,user);
//            Cookie cookie=new Cookie(COOKIE_NAME_TOKEN,token);
//            cookie.setMaxAge(UserKey.token.befSeconds());//cookie的有效期
//            cookie.setPath("/");
//            httpServletResponse.addCookie(cookie);
            String token = UUID.uuid();
            System.out.println("ttttttt"+token);

            addCookie(user1,token,httpServletResponse,httpServletRequest);
//            CodeMessage codeMessage1=new CodeMessage(0,token); //0的时候代表成功
            System.out.println("-------------------");
            return new CodeMessage(token,0); //登录成功  返回的对象 便于获得token
        }else {
            return CodeMessage.Pass_ERROR;//密码错误
        }

*/
    }
    private void addCookie(User1 user1, String token, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        System.out.println("调用了addcookie的RedisService的set");
        redisService1.set(UserKey.token,token, user1);

//        Cookie cookie=new Cookie(COOKIE_NAME_TOKEN,token);
//        cookie.setMaxAge(UserKey.token.befSeconds());//cookie的有效期
//        cookie.setPath("/");
//        System.out.println("Cookie：   "+cookie.getValue());
//        System.out.println("Cookie：   "+cookie.getPath());
////        System.out.println("Cookie：   "+cookie.());
//        httpServletResponse.addCookie(cookie);

        // 获取之前请求中的Cookie数组
        Cookie[] cookies = httpServletRequest.getCookies();

        // 判断是否存在同名的Cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME_TOKEN.equals(cookie.getName())) {
                    // 如果存在同名的Cookie，覆盖原来的Cookie
                    cookie.setValue(token);
                    cookie.setMaxAge(UserKey.token.befSeconds());
                    cookie.setPath("/");
//                    httpServletResponse.header('Access-Control-Allow-Origin', '*'); // *表示支持所有的域名，可以换成具体的域名
//                    cookie.setDomain("localhost:9191"); // 设置域名为localhost
//                    cookie.setPort(9191); // 设置端口号为9191
                    httpServletResponse.addCookie(cookie);
                    return;
                }
            }
        }

        // 如果不存在同名的Cookie，创建新的Cookie并添加
        Cookie newCookie = new Cookie(COOKIE_NAME_TOKEN, token);
        newCookie.setMaxAge(UserKey.token.befSeconds());
        newCookie.setPath("/");
        httpServletResponse.addCookie(newCookie);
    }
    private void addCookie1(User user1, String token, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        System.out.println("调用了addcookie的RedisService的set");
        redisService1.set(UserKey.token,token, user1);

//        Cookie cookie=new Cookie(COOKIE_NAME_TOKEN,token);
//        cookie.setMaxAge(UserKey.token.befSeconds());//cookie的有效期
//        cookie.setPath("/");
//        System.out.println("Cookie：   "+cookie.getValue());
//        System.out.println("Cookie：   "+cookie.getPath());
////        System.out.println("Cookie：   "+cookie.());
//        httpServletResponse.addCookie(cookie);

        // 获取之前请求中的Cookie数组
        Cookie[] cookies = httpServletRequest.getCookies();

        // 判断是否存在同名的Cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME_TOKEN.equals(cookie.getName())) {
                    // 如果存在同名的Cookie，覆盖原来的Cookie
                    cookie.setValue(token);
                    cookie.setMaxAge(UserKey.token.befSeconds());
                    cookie.setPath("/");
//                    httpServletResponse.header('Access-Control-Allow-Origin', '*'); // *表示支持所有的域名，可以换成具体的域名
//                    cookie.setDomain("localhost:9191"); // 设置域名为localhost
//                    cookie.setPort(9191); // 设置端口号为9191
                    httpServletResponse.addCookie(cookie);
                    return;
                }
            }
        }

        // 如果不存在同名的Cookie，创建新的Cookie并添加
        Cookie newCookie = new Cookie(COOKIE_NAME_TOKEN, token);
        newCookie.setMaxAge(UserKey.token.befSeconds());
        newCookie.setPath("/");
        httpServletResponse.addCookie(newCookie);
    }
    public User1 getByToken(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, String token) {
        if(StringUtils.isEmpty(token))
        {
            return null;
        }

         User1 user1 =  redisService1.get(UserKey.token,token, User1.class);
         //还需要延长一下cookie有效期
        if(user1 !=null)
        addCookie(user1,token,httpServletResponse,httpServletRequest);
        return user1;
    }




}
