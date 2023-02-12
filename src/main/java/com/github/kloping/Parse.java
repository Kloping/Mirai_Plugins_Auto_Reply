package com.github.kloping;

import com.alibaba.fastjson.JSON;
import io.github.kloping.io.ReadUtils;
import io.github.kloping.url.UrlUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author github.kloping
 */
public class Parse {
    private static final Pattern PATTER_FACE = Pattern.compile("<Face:\\d+>");
    private static final Pattern PATTER_PIC = Pattern.compile("<Pic:[^>^]+?>");
    private static final Pattern PATTER_URL = Pattern.compile("<Url:[^>^]+>");
    private static final Pattern PATTER_AT = Pattern.compile("<At:\\d+>");
    private static final Pattern PATTER_MUSIC = Pattern.compile("<Music:\\d+>");
    private static final Pattern PATTER_VOICE = Pattern.compile("<Audio:.+>");
    public static final Pattern[] PATTERNS = {PATTER_FACE, PATTER_PIC, PATTER_URL, PATTER_AT, PATTER_VOICE, PATTER_MUSIC};

    private static final String BASE64 = "base64,";

    private static final Map<Integer, Face> FACES = new HashMap<>();
    private static final Map<Long, At> ATS = new HashMap<>();

    public static List<Object> aStart(String line) {
        List<String> list = new ArrayList<>();
        List<Object> olist = new ArrayList<>();
        a1b2c3(list, line);
        for (String s : list) {
            int i = line.indexOf(s);
            if (i > 0) {
                olist.add(line.substring(0, i));
            }
            olist.add(s);
            line = line.substring(i + s.length());
        }
        if (!line.isEmpty())
            olist.add(line);
        return olist;
    }

    public static void a1b2c3(List<String> list, String line) {
        if (list == null || line == null || line.isEmpty()) return;
        Map<Integer, String> nm = getNearestOne(line, PATTER_FACE, PATTER_PIC, PATTER_URL, PATTER_AT, PATTER_VOICE, PATTER_MUSIC);
        if (nm.isEmpty()) {
            list.add(line);
            return;
        }
        int n = nm.keySet().iterator().next();
        String v = nm.get(n);
        String[] ss = new String[2];
        ss[0] = line.substring(0, line.indexOf(v));
        ss[1] = line.substring(line.indexOf(v) + v.length(), line.length());
        if (!ss[0].isEmpty()) {
            list.add(ss[0]);
            line = line.substring(ss[0].length());
        }
        line = ss[1];
        list.add(v);
        a1b2c3(list, line);
        return;
    }

    public static Map<Integer, String> getNearestOne(final String line, Pattern... patterns) {
        try {
            Map<Integer, String> map = new LinkedHashMap<>();
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String l1 = matcher.group();
                    int i1 = line.indexOf(l1);
                    map.put(i1, l1);
                }
            }
            Map<Integer, String> result1 = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> result1.put(x.getKey(), x.getValue()));
            return result1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MessageChain getMessageFromString(String str, Contact group) {
        try {
            if (str == null || str.isEmpty() || group == null) return null;
            MessageChainBuilder builder = new MessageChainBuilder();
            append(str, builder, group);
            MessageChain message = builder.build();
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return new MessageChainBuilder().append(str).build();
        }
    }

    private static List<Object> append(String sb, MessageChainBuilder builder, Contact contact) throws Exception {
        List<Object> lls = aStart(sb);
        for (Object o : lls) {
            String str = o.toString();
            boolean k = (str.startsWith("<") || str.startsWith("[")) && !str.matches("\\[.+]请使用最新版手机QQ体验新功能");
            if (k) {
                String ss = str.replace("<", "").replace(">", "");
                int i1 = ss.indexOf(":");
                String s1 = ss.substring(0, i1);
                String s2 = ss.substring(i1 + 1);
                switch (s1) {
                    case "Pic":
                        builder.append(createImage(contact, s2));
                        break;
                    case "Face":
                        builder.append(getFace(Integer.parseInt(s2)));
                        break;
                    case "At":
                        builder.append(getAt(Long.parseLong(s2)));
                        break;
                    case "Voice":
                    case "Audio":
                        builder.append(createVoiceMessageInGroup(s2, contact.getId(), contact));
                        break;
                    case "Music":
                        builder.append(createMusic(contact, s2));
                        break;
                    default:
                        break;
                }
            } else {
                builder.append(str);
            }
        }
        return lls;
    }

    private static Message createMusic(Contact contact, String s2) {
        String[] ss = s2.split(",");
        MusicKind kind = MusicKind.valueOf(ss[0]);
        MusicShare share = new MusicShare(kind
                , ss[1]
                , ss[2]
                , ss[3]
                , ss[4]
                , ss[5]
        );
        return share;
    }

    private static Face getFace(int parseInt) {
        if (FACES.containsKey(parseInt)) {
            return FACES.get(parseInt);
        } else {
            Face face = new Face(parseInt);
            FACES.put(parseInt, face);
            return face;
        }
    }

    public static At getAt(long id) {
        if (ATS.containsKey(id)) {
            return ATS.get(id);
        } else {
            At at = new At(id);
            ATS.put(id, at);
            return at;
        }
    }

    public static Message createImage(Contact group, String path) {
        Message image = null;
        try {
            if (path.startsWith("http")) {
                image = Contact.uploadImage(group, new ByteArrayInputStream(ReadUtils.readAll(new URL(path).openStream())));
            } else if (path.startsWith("{")) {
                image = Image.fromId(path);
            } else if (path.contains(BASE64)) {
                image = Contact.uploadImage(group, new ByteArrayInputStream(getBase64Data(path)));
            } else if (path.startsWith("[") && path.endsWith("]")) {
                image = createForwardMessageByPic(group, JSON.parseArray(path).toArray(new String[0]));
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    return new PlainText(path);
                } else {
                    image = Contact.uploadImage(group, file);
                }
            }
        } catch (Exception e) {
            System.err.println(path + "加载失败");
            e.printStackTrace();
        }
        return image;
    }

    public static byte[] getBase64Data(String base64) {
        int i = base64.indexOf(BASE64);
        String base64Str = base64.substring(i + BASE64.length());
        byte[] bytes = Base64.getDecoder().decode(base64Str);
        return bytes;
    }

    public static Message createVoiceMessageInGroup(String url, long id, Contact contact) {
        ExternalResource resource = null;
        try {
            byte[] bytes = UrlUtils.getBytesFromHttpUrl(url);
            resource = ExternalResource.create(bytes);
            if (contact instanceof Group) {
                return ((Group) contact).uploadAudio(resource);
            } else if (contact instanceof Friend) {
                return ((Friend) contact).uploadAudio(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (resource != null) {
                try {
                    resource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Message createForwardMessageByPic(Contact contact, String... picUrl) {
        ForwardMessageBuilder builder = new ForwardMessageBuilder(contact);
        for (String s : picUrl) {
            builder.add(contact.getId(), contact.getBot().getNick(), createImage(contact, s));
        }
        return builder.build();
    }


    public static final String QID = "$qid";
    public static final String QID0 = "\\$qid";

    public static final String QNAME = "$qname";
    public static final String QNAME0 = "\\$qname";

    public static final String MNAME = "$mname";
    public static final String MNAME0 = "\\$mname";

    public static final String GNAME = "$gname";
    public static final String GNAME0 = "\\$gname";

    public static final String GID = "$gid";
    public static final String GID0 = "\\$gid";

    public static final String CHAR0 = "\\$%s";

    public static final String ALL = "$all";

    public static final String PAR_URL = "$url";

    public static final String PAR_NUMBER = "$number";
    public static final String PAR_NUMBER0 = "\\$number";

    public static String filterId(String text, Bot bot, Long gid, Long qid) {
        if (text == null) return text;
        if (qid != null) {
            if (text.contains(QID)) {
                text = text.replaceAll(QID0, String.valueOf(qid));
            }
        }
        if (gid != null) {
            if (text.contains(GID)) {
                text = text.replaceAll(GID0, String.valueOf(gid));
            }
        }
        if (text.contains(QNAME)) {
            Friend friend = bot.getFriend(qid);
            if (friend != null) {
                text = text.replaceAll(QNAME0, friend.getNick());
            } else {
                if (gid != null) {
                    Member member = bot.getGroup(gid).getMembers().get(qid);
                    if (member != null)
                        text = text.replaceAll(QNAME0, member.getNick());
                }
            }
        }
        if (text.contains(MNAME)) {
            if (gid != null) {
                Member member = bot.getGroup(gid).getMembers().get(qid);
                if (member != null)
                    text = text.replaceAll(MNAME0, member.getNameCard());
            }
        }
        if (gid != null) {
            if (text.contains(GNAME)) {
                Group group = bot.getGroup(gid);
                if (group != null)
                    text = text.replaceAll(GNAME0, group.getName());
            }
        }
        return text;
    }

    public static MessageChain getMessageChainAndFilterId(String text, MessageEvent event) {
        Long gid = null;
        Long qid = null;
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent gme = (GroupMessageEvent) event;
            gid = gme.getGroup().getId();
        }
        qid = event.getSender().getId();
        text = filterId(text, event.getBot(), gid, qid);
        return getMessageFromString(text, event.getSubject());
    }
}
