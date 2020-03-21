package com.wiblog.core.utils;

import com.vdurmont.emoji.EmojiParser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/1
 */
@Component
public final class Commons {

    /**
     * An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!
     * <p>
     * 这种格式的字符转换为emoji表情
     *
     * @param value
     * @return
     */
    public static String emoji(String value) {
        return EmojiParser.parseToUnicode(value);
    }


    public static String dateFormat(Date date){
        DateFormat df = DateFormat.getDateInstance();
        System.out.println(df.format(date));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DATE);

        String[] m = new String[]{"一","二","三","四","五","六","七","八","九","十","十一","十二"};
        return m[month]+"月"+day+","+year;
    }
}
