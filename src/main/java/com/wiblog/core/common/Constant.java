package com.wiblog.core.common;

/**
 * 常量类
 * @author pwm
 * @date 2019/4/25
 */
public class Constant {

    /**
     * 邮箱格式
     */
    public static final String EM = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * 手机号格式
     */
    public static final String PH = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0,5-9]))\\d{8}$";

    /**
     * 特殊字符
     */
    public static final String SPECIAL_CHAR = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";

    /**
     * 纯数字
     */
    public static final String PURE_NUM_CHAR = "^(?!\\d+$).+$";

    public static final String LOGIN_REDIS_KEY = "user_login_";

    public static final String COOKIES_KEY = "uToken";

    /**     * redis key 前缀 邮箱验证码
     */
    public static final String CHECK_EMAIL_KEY = "check_email_";

    public static final String VERIFY_CODE_SESSION_KEY = "verify_code";

    /**
     * 点赞redis key
     */
    public static final String LIKE_RECORD_KEY = "like_record";

    /**
     * 点击率redis key
     */
    public static final String HIT_RECORD_KEY = "hit_record_";

    /**
     * 邮箱发送次数
     */
    public static final String EMAIL_COUNT = "email_count_";

    /**
     * 淘宝ip地址库
     */
    public static final String IP_BAIDU_URL = "http://opendata.baidu.com/api.php?co=&resource_id=6006&t=1433920989928&ie=utf8&oe=utf-8&format=json&query=";

    /**
     * 分类列表缓存key
     */
    public static final String CATEGORY_KEY = "category";

    /**
     * 邮件推送redis队列
     */
    public static final String EMAIL_PUSH_KEY = "email_push";

    /**
     * 文章排行榜redis zset
     */
    public static final String ARTICLE_RANKING_KEY  = "article_ranking";

    /**
     * 附近的人
     */
    public static final String NEAR_USER_KEY = "near_user";
}

