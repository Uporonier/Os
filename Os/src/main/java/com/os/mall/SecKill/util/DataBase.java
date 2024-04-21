//package com.rabbiter.em.SecKill.util;
//
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.util.Properties;
//
//public class DataBase {
//
//    private static Properties props;
//
//    static {
//        try {
//            InputStream in = DataBase.class.getClassLoader().getResourceAsStream("application.yml");
//            props = new Properties();
//            props.load(in);
//            in.close();
//
//            System.out.println("找到了yml");
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Connection getConn() throws Exception{
//        String url = props.getProperty("spring.datasource.url");
//        String username = props.getProperty("spring.datasource.username");
//        String password = props.getProperty("spring.datasource.password");
//        String driver = props.getProperty("spring.datasource.driver-class-name");
//        Class.forName(driver);
//        return DriverManager.getConnection(url,username, password);
//    }
//}
package com.os.mall.SecKill.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class DataBase {

    private static Map<String, Object> yamlProps;

    static {
        try {
            InputStream in = DataBase.class.getClassLoader().getResourceAsStream("application.yml");
            Yaml yaml = new Yaml();
            // 加载YAML文件内容到Map中
            yamlProps = yaml.load(in);
            in.close();
            System.out.println("找到了yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn() throws Exception {
        // 使用嵌套的Map来访问深层属性
        Map<String, Object> datasource = (Map<String, Object>) ((Map<String, Object>) yamlProps.get("spring")).get("datasource");
        String url = (String) datasource.get("url");
        String username = (String) datasource.get("username");
        String password = (String) datasource.get("password");
        String driver = (String) datasource.get("driver-class-name");

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}
