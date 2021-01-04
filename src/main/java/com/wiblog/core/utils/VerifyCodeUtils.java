package com.wiblog.core.utils;

import com.wiblog.core.common.Constant;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 图片验证码
 *
 * @author pwm
 * @date 2019/11/4
 */
@Slf4j
public class VerifyCodeUtils {

    /**
     * 随机产生数字与字母组合的字符串
     */
    private static String randString = "23456789ABCDEFGHIJKMNPQRSTUVWXYZ";

    /**
     * 图片宽
     */
    private static int width = 95;

    /**
     * 图片高
     */
    private static int height = 25;

    /**
     * 干扰线数量
     */
    private static int lineSize = 40;

    /**
     * 随机产生字符数量
     */
    private static int stringNum = 4;

    private static Random random = new Random();

    /**
     * 获得字体
     */
    private static Font getFont() {
        return new Font("Fixedsys", Font.BOLD, 18);
    }

    /**
     * 获得颜色
     */
    private static Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public static void getRandomCode(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        // 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        g.setColor(getRandColor(110, 133));
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drewLine(g);
        }
        // 绘制随机字符
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            randomString = drewString(g, randomString, i);
        }
        log.info(randomString);
        //将生成的随机字符串保存到session中
        session.removeAttribute(Constant.VERIFY_CODE_SESSION_KEY);
        session.setAttribute(Constant.VERIFY_CODE_SESSION_KEY, randomString);
        g.dispose();
        try {
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", response.getOutputStream());
        } catch (Exception e) {
            log.error("将内存中的图片通过流动形式输出到客户端失败>>>>   ", e);
        }

    }

    /**
     * 绘制字符串
     */
    private static String drewString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = getRandomString(random.nextInt(randString.length()));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     */
    private static void drewLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     */
    private static String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

    /**
     * 生成随机字符串
     */
    public static String getRandomCode() {
        // 绘制随机字符
        StringBuilder randomString = new StringBuilder();
        for (int i = 1; i <= stringNum; i++) {
            String rand = getRandomString(random.nextInt(randString.length()));
            randomString.append(rand);
        }
        System.out.println(randomString.toString());
        return randomString.toString();
    }
}
