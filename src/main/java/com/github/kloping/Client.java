package com.github.kloping;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable {
    public static ExecutorService threads = Executors.newFixedThreadPool(10);
    public static final String uuid = Resource.conf.getPassword().trim().isEmpty() ? UUID.randomUUID().toString() : Resource.conf.getPassword();
    private static final String uuidW = "/?key=" + uuid;
    public Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
        threads.submit(this);
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        String[] sss = null;
        boolean can = false;
        while ((line = br.readLine().toLowerCase()) != null) {
            if (sss == null) {
                sss = line.split("\\s+");
            }
            if (line.trim().isEmpty()) {
                break;
            }
            sb.append(line).append("\n");
            if (line.trim().startsWith("cookie")) {
                can = line.contains("key=" + uuid.toLowerCase());
            }
        }
        String url = sss[1];
        if (url.equals(uuidW)) {
            source("/index.html", can);
            return;
        } else if (can && url.startsWith("/modify")) {
            url = url.substring("/modify?".length());
            String[] ss = url.split("&");
            Map<String, String> map = new HashMap<>();
            for (String s : ss) {
                String[] s2 = s.split("=");
                map.put(s2[0], s2[1]);
            }
            if (Resource.tryModify(map)) {
                allData();
            } else {
                data("error");
            }
            return;
        } else if (can && url.startsWith("/search")) {
            url = url.substring("/search?".length());
            String[] ss = url.split("&");
            Map<String, String> map = new HashMap<>();
            for (String s : ss) {
                String[] s2 = s.split("=");
                map.put(s2[0], s2[1]);
            }
            data(Resource.trySearch(map));
            return;
        } else if (can && url.startsWith("/delete")) {
            url = url.substring("/delete?".length());
            String[] ss = url.split("&");
            Map<String, String> map = new HashMap<>();
            for (String s : ss) {
                String[] s2 = s.split("=");
                map.put(s2[0], s2[1]);
            }
            if (Resource.tryDelete(map))
                allData();
            else data("{}");
            return;
        } else if (can && url.startsWith("/append")) {
            url = url.substring("/append?".length());
            String[] ss = url.split("&");
            Map<String, String> map = new HashMap<>();
            for (String s : ss) {
                String[] s2 = s.split("=");
                map.put(s2[0], s2[1]);
            }
            Resource.append(map);
            allData();
            return;
        }
        switch (url) {
            case "/favicon.ico":
                favicon();
                return;
            case "/get_all":
                allData();
                return;
            default:
                source(sss[1], true);
                return;
        }
    }


    private void allData() throws Exception {
        data(JSON.toJSONString(Resource.entityMap));
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

    private void data(String data) throws Exception {
        OutputStream os = socket.getOutputStream();
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        String time = sdf.format(new Date());
        String sss = ("HTTP/1.1 200 OK\r\n" +
                "content-type: application/json\r\n" +
                "content-length: " + dataBytes.length + "\r\n" +
                "date: " + time + "\r\n" +
                "keep-alive: timeout=60\r\n" +
                "connection: keep-alive\r\n");
        os.write(sss.getBytes(StandardCharsets.UTF_8));
        os.write("\r\n".getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.write(dataBytes);
        os.flush();
        os.close();
    }

    private void favicon() throws IOException {
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        byte[] bytes = this.getClass().getClassLoader().getResourceAsStream("static/index.html").readAllBytes();
        String time = sdf.format(new Date());
        pw.println("HTTP/1.1 302 Found\n" +
                "location: http://q1.qlogo.cn/g?b=qq&nk=3474006766&s=640\n" +
                "content-language: zh-CN\n" +
                "content-length: 0\n" +
                "date: " + time + "\r\n" +
                "keep-alive: timeout=60\n" +
                "connection: keep-alive");
        pw.println();
        pw.flush();
        pw.close();
    }

    private void source(String name, boolean k) throws Exception {
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("static" + name);
        if (is == null) {
            throw new RuntimeException("not found : " + name);
        }
        String type = getContentType(name);
        byte[] bytes = is.readAllBytes();
        String time = sdf.format(new Date());
        pw.println(String.format("HTTP/1.1 200 OK\n" +
                "content-type: %s\n" +
                "content-length: %s\n" +
                "date: " + time + "\r\n" +
                "keep-alive: timeout=60\n" +
                "connection: keep-alive", type, bytes.length, new Date()));
        if (!k && "/index.html".equals(name))
            pw.println("set-cookie: key=" + uuid + "\n");
        pw.println();
        pw.flush();
        os.write(bytes);
        pw.close();
    }

    public static String getContentType(String path) {
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".jsx")) return "text/html; charset=utf-8";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".html")) return "text/html";
        return "unknown";
    }
}
