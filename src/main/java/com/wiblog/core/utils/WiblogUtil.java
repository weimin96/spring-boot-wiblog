package com.wiblog.core.utils;

import com.wiblog.core.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/1
 */
@Slf4j
public class WiblogUtil {


    /**
     * markdown解析器
     */
    private static final Parser PARSER = Parser.builder().build();

    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    /**
     * markdown转换为 html
     *
     * @param markdown markdown
     * @return html
     */
    public static String mdToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        Node document = PARSER.parse(markdown);

        String content = RENDERER.render(document);
        content = Commons.emoji(content);
        return content;
    }


    public static void setCookie(HttpServletResponse response, String key, String value, int expire) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain("");
        cookie.setPath("/");
        cookie.setMaxAge(expire);
        response.addCookie(cookie);
    }

    public static void setCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(Constant.COOKIES_KEY, token);
        cookie.setDomain("");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void delCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie c : cookies) {
            if (Constant.COOKIES_KEY.equals(c.getName())) {
                log.info("删除cookie{}", c.getName());
                c.setMaxAge(0);
                c.setValue(null);
                c.setDomain("");
                c.setPath("/");
                response.addCookie(c);
                break;
            }
        }
    }

}
