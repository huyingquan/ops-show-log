package com.ximalaya.ops.show.log;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by nihao on 17/1/7.
 */
public class LogServlet extends HttpServlet {

    // 资源路径
    private final String resourcePath="support";
    // 默认每页显示文件大小:512KB
    private Integer defaultSize=1048576/2;
    // 默认文件后缀:.log
    private String fileSuffix=".log";
    private final DecimalFormat df = new DecimalFormat("#.00");
    private List<File> logFileList=new ArrayList(20);

    @Override
    public void init() throws ServletException {
        String logFilePath=getServletContext().getInitParameter("logFilePathConfigLocation");
        if(logFilePath==null){
            throw new ServletException("property: logFilePathConfigLocation not found");
        }
        else{
            String[] logFilePathArray=logFilePath.split(",");
            for(String singleLogFilePath:logFilePathArray){
                if(singleLogFilePath!=null&&!singleLogFilePath.equals("")){
                    File tempFile=new File(singleLogFilePath);
                    logFileList.add(tempFile);
                }
            }
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();
        response.setCharacterEncoding("utf-8");
        if(contextPath == null) {
            contextPath = "";
        }

        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());
        if(!"".equals(path)){
            response.setContentType("application/json; charset=utf-8");
            if("/init".equals(path)){
                List<String> list=new ArrayList(logFileList.size());
                for(File file:logFileList){
                    this.aroundFile(file,list);
                }
                Collections.sort(list);
                this.writeJson(response,list);
            }
            else if("/single".equals(path)){
                String filePath=request.getParameter("path");
                long pageIndex=1;
                String pageIndexStr=request.getParameter("pageIndex");

                RandomAccessFile randomFile = new RandomAccessFile(new File(filePath),"r");
                long fileSize=randomFile.length();
                long pageSize=(fileSize/defaultSize)+1;
                if(pageIndexStr!=null){
                    pageIndex=Long.parseLong(pageIndexStr);
                }
                else{
                    pageIndex=pageSize;
                }
                //获得变化部分的
                randomFile.seek(defaultSize*(pageIndex-1));
                String si="";
                if(fileSize<1024L){
                    si=df.format((double)fileSize)+"BT";
                }else if (fileSize < 1048576) {
                    si = df.format((double) fileSize / 1024) + "KB";
                } else if (fileSize < 1073741824) {
                    si = df.format((double) fileSize / 1048576) + "MB";
                } else {
                    si = df.format((double) fileSize / 1073741824) +"GB";
                }
                String tmp = "";
                List<String> data=new ArrayList(1000);
                while( randomFile.getFilePointer()<pageIndex*defaultSize&&(tmp = randomFile.readLine())!= null) {
                    fileSize=randomFile.getFilePointer();
                    data.add(new String(tmp.getBytes("ISO8859-1")));
                }
                if(randomFile!=null){
                    randomFile.close();
                }
                Map<String,Object> map=new HashMap(10);
                map.put("list",data);
                map.put("pageSize",pageSize);
                map.put("pageIndex",pageIndex);
                map.put("si",si);
                map.put("fileSize",fileSize);
                this.writeJson(response,map);
            }
            else if("/timer".equals(path)){
                String filePath=request.getParameter("path");
                String size=request.getParameter("fileSize");
                Long fileSize=0L;
                if(size!=null){
                    fileSize=Long.parseLong(size);
                }
                RandomAccessFile randomFile = new RandomAccessFile(new File(filePath),"r");
                //获得变化部分的
                randomFile.seek(fileSize);
                String tmp = "";
                List<String> list=new ArrayList<String>(20);
                while( (tmp = randomFile.readLine())!= null) {
                    list.add(new String(tmp.getBytes("ISO8859-1")));
                }
                fileSize = randomFile.length();
                Map<String,Object> map=new HashMap(2);
                map.put("list",list);
                map.put("fileSize",fileSize);
                this.writeJson(response,map);
            }
            else{
                this.returnResourceFile(path, response);
            }
        }
    }

    private void aroundFile(File file,List<String> list){
        if(file.exists()){
            if(file.isFile()){
                //if(file.getAbsolutePath().contains(fileSuffix))
                    list.add(file.getAbsolutePath());
            }
            else if(file.isDirectory()){
                File[] files=file.listFiles();
                if(files!=null){
                    for(File childFile:files){
                        aroundFile(childFile,list);
                    }
                }
            }
        }
    }

    private void writeJson(HttpServletResponse response,Object obj){
        PrintWriter out = null;
        try{
            out=response.getWriter();
            out.print(JSON.toJSONString(obj));
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(out!=null){
                out.close();
            }
        }
    }

    protected void returnResourceFile(String fileName, HttpServletResponse response) {
        String filePath = this.getFilePath(fileName);
        if(filePath.endsWith(".html")) {
            response.setContentType("text/html; charset=utf-8");
        }
        else if(filePath.endsWith(".css")){
            response.setContentType("text/css;charset=utf-8");
        }
        else if(fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        else{
            return;
        }
        String text=Utils.readFromResource(filePath);
        PrintWriter writer=null;
        try{
            writer=response.getWriter();
            writer.write(text);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
    }

    protected String getFilePath(String fileName) {
        return this.resourcePath + fileName;
    }
}
