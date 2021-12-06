package com.hrs.kloping;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hrs.kloping.Resource.*;

public class Client implements Runnable {
    public static ExecutorService threads = Executors.newFixedThreadPool(10);
    public static final String uuid = UUID.randomUUID().toString();
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
        }
    }

    private void start() throws Exception {
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        String[] sss = null;
        boolean can = false;
        while ((line = br.readLine()) != null) {
            if (sss == null) sss = line.split("\\s+");
            if (line.trim().isEmpty()) break;
            sb.append(line).append("\n");
            if (line.startsWith("cookie")) {
                can = line.equals("cookie: key=" + uuid);
            }
        }
        String url = sss[1];
        if (url.equals(uuidW)) {
            source("/index.html");
            return;
        } else if (can && url.startsWith("/modify")) {
            url = url.substring("/modify?".length());
            String[] ss = url.split("&");
            Map<String, String> map = new HashMap<>();
            for (String s : ss) {
                String[] s2 = s.split("=");
                map.put(s2[0], s2[1]);
            }
            if (tryModify(map))
                ok();
            else data("error");
            return;
        } else if (can && url.startsWith("/search")) {
            url = url.substring("/search?".length());
            String[] ss = url.split("&");
            Map<String, String> map = new HashMap<>();
            for (String s : ss) {
                String[] s2 = s.split("=");
                map.put(s2[0], s2[1]);
            }
            data(trySearch(map));
            return;
        }
        switch (url) {
            case "/favicon.ico":
                favicon();
                return;
            case "/getAll":
                allData();
                return;
            default:
                source(sss[1]);
                return;
        }
    }


    private void allData() throws Exception {
        data(JSON.toJSONString(entityMap));
    }

    private void ok() throws Exception {
        data("ok");
    }

    private void data(String data) throws Exception {
        OutputStream os = socket.getOutputStream();
        String jsonStr = data;
        byte[] dataBytes = jsonStr.getBytes(StandardCharsets.UTF_8);
        String sss = ("HTTP/1.1 200 OK\n" +
                "content-type: application/json\n" +
                "content-length: " + dataBytes.length + "\n" +
                "keep-alive: timeout=60\n" +
                "connection: keep-alive\r\n\r\n");
        os.write(sss.getBytes(StandardCharsets.UTF_8));
        os.write(dataBytes);
        os.write("\r\n".getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }

    private void favicon() throws IOException {
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        byte[] bytes = this.getClass().getClassLoader().getResourceAsStream("static/index.html").readAllBytes();
        pw.println("HTTP/1.1 302 Found\n" +
                "location: http://q1.qlogo.cn/g?b=qq&nk=3474006766&s=640\n" +
                "content-language: zh-CN\n" +
                "content-length: 0\n" +
                "date: Mon, 06 Dec 2021 02:35:41 GMT\n" +
                "keep-alive: timeout=60\n" +
                "connection: keep-alive");
        pw.println();
        pw.flush();
        pw.close();
    }

    private void source(String name) throws Exception {
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("static" + name);
        String type = getContentType(name);
        byte[] bytes = is.readAllBytes();
        pw.println("HTTP/1.1 200 OK\n" +
                "vary: Origin, Access-Control-Request-Method, Access-Control-Request-Headers\n" +
                "content-type: " + type + "\n" +
                "content-length: " + bytes.length + "\n" +
                "keep-alive: timeout=60\n" +
                "connection: keep-alive");
        if (name.equals("/index.html"))
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
