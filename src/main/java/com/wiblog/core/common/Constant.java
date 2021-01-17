package com.wiblog.core.common;

/**
 * 常量类
 * @author pwm
 * @date 2019/4/25
 */
public class Constant {

    /**
     * 校验验证码session key
     */
    public static final String VERIFY_CODE_SESSION_KEY = "verify_code";

    /**
     * 用户信息 cookie
     */
    public static final String COOKIES_KEY = "uToken";

    /**
     * 淘宝ip地址库
     */
    public static final String IP_BAIDU_URL = "http://opendata.baidu.com/api.php?co=&resource_id=6006&t=1433920989928&ie=utf8&oe=utf-8&format=json&query=";

    /**
     * 日志列表
     */
    public static final String LOG_PATH = "/home/pwm/log/";

    /**
     * 正则
     */
    public interface Regular{
        /**
         * 邮箱格式
         */
        String EM = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        /**
         * 手机号格式
         */
        String PH = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0,5-9]))\\d{8}$";

        /**
         * 特殊字符
         */
        String SPECIAL_CHAR = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";

        /**
         * 纯数字
         */
        String PURE_NUM_CHAR = "^(?!\\d+$).+$";

        /**
         * html语法
         */
        String HTML = "<[^>]+>";

        /**
         * 换行 tab 空格
         */
        String WRAP = "\\s*|\t|\r|\n";
    }

    /**
     * redis key
     */
    public interface RedisKey{

        /**
         * 是否初始化数据缓存
         */
        String APP_INIT = "app_init";

        /**
         * 是否初始化数据缓存
         */
        String ELASTIC_SEARCH_INIT = "elastic_search_init";

        String LOGIN_REDIS_KEY = "user_login_";
        /**
         * redis key 前缀 邮箱验证码
         */
        String CHECK_EMAIL_KEY = "check_email_";
        /**
         * 点赞redis key
         */
        String LIKE_RECORD_KEY = "like_record";
        /**
         * 点击率redis key
         */
        String HIT_RECORD_KEY = "hit_record";
        /**
         * 邮箱发送次数
         */
        String EMAIL_COUNT = "email_count_";
        /**
         * 分类列表缓存key
         */
        String CATEGORY_KEY = "category";
        /**
         * 邮件推送redis队列
         */
        String EMAIL_PUSH_KEY = "email_push";
        /**
         * 文章排行榜redis zset
         */
        String ARTICLE_RANKING_KEY  = "article_ranking";
        /**
         * 文章简要信息redis
         */
        String ARTICLE_DETAIL_KEY  = "article_detail";
        /**
         * 附近的人
         */
        String NEAR_USER_KEY = "near_user";
        /**
         * 微信登录校验码
         */
        String WECHAT_LOGIN_CODE_ = "wechat_login_code_";
    }


}

