package com.wiblog.core.utils;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wiblog.core.weixin.TextMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pwm
 * @date 2020/4/3
 */
public class MessageUtil {

    /**
     * xml转换为map
     *
     * @param request request
     * @return Map
     * @throws IOException IOException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, SAXException {
        Map<String, String> map = new HashMap<>(16);
        SAXReader reader = new SAXReader();
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        InputStream ins = null;
        try {
            ins = request.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Document doc;
        try {
            doc = reader.read(ins);
            Element root = doc.getRootElement();

            List<Element> list = root.elements();

            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }

            return map;
        } catch (DocumentException e1) {
            e1.printStackTrace();
        } finally {
            if (ins != null) {
                ins.close();
            }
        }

        return null;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessage textMessage) {

        // use xstream:1.4.11
        XStream xstream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.alias("xml", TextMessage.class);
        xstream.aliasField("ToUserName", TextMessage.class, "ToUserName");
        xstream.aliasField("FromUserName", TextMessage.class, "FromUserName");
        xstream.aliasField("CreateTime", TextMessage.class, "CreateTime");
        xstream.aliasField("MsgType", TextMessage.class, "MsgType");
        xstream.aliasField("Content", TextMessage.class, "Content");
        return xstream.toXML(textMessage);
    }
}
