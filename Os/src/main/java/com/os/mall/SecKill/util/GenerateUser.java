package com.os.mall.SecKill.util;

import com.os.mall.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.os.mall.entity.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class GenerateUser {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    static UserService userService;

    private static void createUser(int count) throws Exception{
        List<User> user = new ArrayList<User>(count);
        //生成用户
        for(int i=0;i<count;i++) {
            User User = new User();
            User.setUsername("user"+i);
//            String pass=generateRandomPassword();
            User.setPassword(MD5Hash.toMD5("123456"));
//            userService.save(User);
            user.add(User);
        }
        System.out.println("create user");

/*
////		//插入数据库
		Connection conn = DataBase.getConn();
        System.out.println("链接数据库成功");
		String sql = "insert into sys_user(username, password)values(?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for(int i = 0; i< user.size(); i++) {
			User user1 = user.get(i);
			pstmt.setString(1, user1.getUsername());
			pstmt.setString(2, user1.getPassword());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		pstmt.close();
		conn.close();
		System.out.println("插入数据库完成");
////        //登录，生成token


*/
        //模拟登录 生成token

        String urlString = "http://localhost:9191/login1/doLogin";
        File file = new File("D:\\desktop\\os\\Os\\Os\\token\\token.txt");
        if(file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i = 0; i< user.size(); i++) {
            User User = user.get(i);

            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "password="+ "123456"+"&mobile="+User.getUsername();
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0 ,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            JSONObject jo = JSON.parseObject(response);
            String token = jo.getString("data");


//            String token = TokenUtils.genToken(User.getId().toString(), User.getUsername());
            System.out.println("JSON=="+jo.toString());
            System.out.println("create token : " + User.getUsername());

            String row = token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + User.getUsername());


        }
        raf.close();

        System.out.println("over");

    }
    private static String generateRandomPassword() {
        int length=5;
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }



    public static void main(String[] args)throws Exception {
        createUser(10000);
    }
}
