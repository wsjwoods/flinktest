package org.datastream.util;

import java.util.Properties;

/**
 * @Author: wushijian
 * @Date: 2020/3/17 15:07
 */
public class GetProperties {
    public static Properties getProperties(String filePath) throws Exception{
        Properties props = new Properties();
        props.load(GetProperties.class.getClassLoader().getResourceAsStream(filePath));
        return props;
    }
}
