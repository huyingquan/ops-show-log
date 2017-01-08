package com.ximalaya.ops.show.log;

import java.io.*;

/**
 * Created by nihao on 17/1/7.
 */
public class Utils {
    public static String readFromResource(String resource){
        InputStream in = null;
        StringBuilder sb=new StringBuilder();
        try{
            in=Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if(in==null){
                in=Utils.class.getResourceAsStream(resource);
            }
            if(in == null) {
                return null;
            }
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while ((line=reader.readLine())!=null){
                sb.append(line+"\r\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
