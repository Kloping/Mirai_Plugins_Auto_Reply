package com.github.kloping;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.kloping.e0.MessagePack;
import io.github.kloping.number.NumberUtils;
import io.github.kloping.reg.MatcherUtils;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;

import static com.github.kloping.e0.MessagePack.*;

/**
 * @author github-kloping
 */
public class Entity {
    private Number gid = -1;
    private String touchKey;
    private int state = 0;
    @JSONField(serialize = false, deserialize = false)
    private Set<Response> vs = new LinkedHashSet<>();
    private Set<Response0> vss = new LinkedHashSet<>();

    public Entity() {
    }

    public Entity(Number q) {
        this.gid = q;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Number getGid() {
        return gid;
    }

    public void setGid(Number gid) {
        this.gid = gid;
    }

    public String getTouchKey() {
        return touchKey;
    }

    public void setK_(String k) {
        this.touchKey = k
                .replaceAll("%", "%")
                .replaceAll("%\\?", ".{0,}")
                .replaceAll("%\\+", ".+")
                .replaceAll("%", ".{1,1}");
    }

    public void setTouchKey(String touchKey) {
        this.touchKey = touchKey;
    }

    public Set<Response> getVs() {
        return vs;
    }

    public int getVSize() {
        int i = 0;
        for (Response v : vs) {
            if (v.state != 0) continue;
            i++;
        }
        return i;
    }

    public Set<Response0> getVss() {
        return vss;
    }

    public void setVss(Set<Response0> vss) {
        this.vss = vss;
    }

    public synchronized Entity apply() {
        vss.clear();
        for (Response v : vs) {
            vss.add(v.asResponse0());
        }
        return this;
    }

    /**
     * 转为可发送
     *
     * @return
     */
    public synchronized Entity deApply() {
        vs.clear();
        for (Response0 v : vss) {
            vs.add(v.asResponse());
        }
        return this;
    }

    public Response getVs(int n) {
        int i = 1;
        for (Response v : vs) {
            if (v.state != 0) continue;
            if (i++ == n) return v;
        }
        return null;
    }

    public static class Response {
        private int weight = 1;
        private MessageChain data;
        private int state = 0;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public MessageChain getData() {
            return data;
        }

        public MessagePack mp() {
            String s0 = getString(data);
            Matcher matcher = MessagePack.PATTERN.matcher(s0);
            if (matcher.find()) {
                MessagePack pack = new MessagePack();
                int i = 1;
                MessageChainBuilder builder = new MessageChainBuilder();
                for (SingleMessage datum : data) {
                    Message message = null;
                    if (datum instanceof PlainText) {
                        PlainText pt = (PlainText) datum;
                        String text = pt.contentToString();
                        String[] ps = MatcherUtils.matcherAll(text, PATTEN_STR);
                        if (ps.length > 0) {
                            int n = 0;
                            while (true) {
                                if (text.isEmpty()) break;
                                if (n >= ps.length) break;
                                int i0 = text.indexOf(ps[n]);
                                if (i0 == 0) {
                                    if (!builder.isEmpty()) {
                                        pack.getData().put(i++, SEND, builder.build());
                                        builder.clear();
                                    }
                                    i = getI(pack, i, ps[n]);
                                    text = text.substring(ps[n].length());
                                } else if (i0 + ps[n].length() == text.length()) {
                                    String t0 = text.substring(0, i0);
                                    builder.append(t0);
                                    pack.getData().put(i++, SEND, builder.build());
                                    builder.clear();
                                    i = getI(pack, i, ps[n]);
                                    text = text.substring(t0.length() + ps[n].length());
                                    break;
                                } else {
                                    String t0 = text.substring(0, i0);
                                    builder.append(t0);
                                    pack.getData().put(i++, SEND, builder.build());
                                    builder.clear();
                                    i = getI(pack, i, ps[n]);
                                    text = text.substring(i0 + ps[n].length());
                                }
                                n++;
                            }
                            if (!text.isEmpty()) {
                                builder.append(text);
                            }
                        } else {
                            builder.append(datum);
                        }
                    } else {
                        builder.append(datum);
                    }
                }
                if (builder.size() != 0) {
                    pack.getData().put(i++, SEND, builder.build());
                    builder.clear();
                }
                return pack;
            } else {
                MessagePack pack = new MessagePack();
                pack.getData().put(1, SEND, data);
                return pack;
            }
        }

        private int getI(MessagePack pack, int i, String s1) {
            String sn0 = NumberUtils.findNumberFromString(s1);
            if (sn0 != null && !sn0.isEmpty()) {
                pack.getData().put(i++, SLEEP, Long.valueOf(sn0));
            } else {
                pack.getData().put(i++, SLEEP, 200L);
            }
            return i;
        }

        public void setData(MessageChain data) {
            this.data = data;
        }

        /**
         * 转为可储存
         *
         * @return
         */
        public Response0 asResponse0() {
            Response0 response0 = new Response0();
            response0.setWeight(weight);
            String code = data.serializeToMiraiCode();
//            code = filterMatcher(code);
            if (code.isEmpty())
                code = MessageChain.serializeToJsonString(data);
            response0.setData(code);
            response0.setState(state);
            return response0;
        }

        public Message toString(String str) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append(str);
            builder.append(data);
            builder.append("\n    权重:");
            builder.append(String.valueOf(weight));
            return builder.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Response response = (Response) o;
            return weight == response.weight && Objects.equals(data, response.data);
        }

        @Override
        public String toString() {
            return getString(data);
        }

        @NotNull
        static String getString(MessageChain data) {
            StringBuilder sb = new StringBuilder();
            for (SingleMessage datum : data) {
                if (datum instanceof PlainText) {
                    sb.append(((PlainText) datum).getContent());
                } else if (datum instanceof Image) {
                    sb.append("[图片]");
                } else if (datum instanceof At) {
                    sb.append("[At:").append(((At) datum).getTarget()).append("]");
                } else {
                    sb.append("[其他类型消息]");
                }
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(weight, data);
        }
    }

    public static class Response0 {
        private int weight = 1;
        private String data;
        private int state = 0;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Response asResponse() {
            Response response = new Response();
            response.setWeight(weight);
            MessageChain chain = null;
            if (data.trim().startsWith("[{")) {
                chain = MessageChain.deserializeFromJsonString(data);
            } else {
                chain = MiraiCode.deserializeMiraiCode(data);
            }
            response.setData(chain);
            response.setState(state);
            return response;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Response0 response0 = (Response0) o;
            return weight == response0.weight && Objects.equals(data, response0.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(weight, data);
        }
    }

    public Message toString(int n) {
        return toString("", n);
    }

    public Message toString(String s, int n) {
        MessageChainBuilder sb = new MessageChainBuilder();
        sb.append(s);
        sb.append("触发词:").append(MiraiCode.deserializeMiraiCode(touchKey));
        int i = 0;
        for (Response v : vs) {
            if (v.state != 0) continue;
            if (i++ == n) break;
            sb.append("\n").append(String.valueOf(i)).
                    append(".回复词:\"").append(MiraiCode.deserializeMiraiCode(v.getData().serializeToMiraiCode()))
                    .append("\"\n    权重:").append(String.valueOf(v.getWeight()));
        }
        return sb.build();
    }
}
