package com.wiblog.core.thirdparty;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.entity.UserAuth;
import com.wiblog.core.mapper.CommentMapper;
import com.wiblog.core.mapper.UserAuthMapper;
import com.wiblog.core.mapper.UserMapper;
import com.wiblog.core.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * github登录
 *
 * @author pwm
 * @date 2019/11/1
 */
@Component
public class GithubProvider {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String secret;

    @Value("${github.redirect.uri}")
    private String redirectUrl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 获取accessToken
     *
     * @param code code
     * @return String
     */
    public String getAccessToken(String code,String state) {
        String url = "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s&state=%s";
        url = String.format(url, clientId, secret, code, redirectUrl, state);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        String token = null;
        if (response != null) {
            token = response.split("&")[0].split("=")[1];
            System.out.println(token);
        }
        return token;
    }

    /**
     * 获取用户信息
     *
     * @param token token
     * @return Map
     */
    public Map getUser(String token) {
        String url = "https://api.github.com/user?access_token=%s";
        url = String.format(url, token);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        Gson gson = new Gson();
        return gson.fromJson(response, Map.class);
    }

    /**
     * 用户注册 存入数据库
     *
     * @param githubUser githubUser
     * @param token      token
     */
    @Transactional(rollbackFor = Exception.class)
    public User registerGithub(Map githubUser, String token) {
        UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .eq("identity_type", "github")
                .eq("identifier", githubUser.get("node_id"))
                .eq("state", 1));
        User user = new User();
        // 未注册 直接插入数据
        if (userAuth == null) {
            user.setAvatarImg((String) githubUser.get("avatar_url"));
            user.setUsername((String) githubUser.get("name"));
            user.setState(true);
            user.setSex("male");
            user.setCreateTime(new Date());
            userMapper.insertReturnId(user);

            userAuth = new UserAuth();
            userAuth.setIdentityType("github");
            userAuth.setIdentifier((String) githubUser.get("node_id"));
            userAuth.setPassword(token);
            userAuth.setUid(user.getUid());
            userAuth.setCreateTime(new Date());
            userAuthMapper.insert(userAuth);
        } else { // 已经注册 使用原来帐号
            user = userMapper.selectOne(new QueryWrapper<User>().eq("uid", userAuth.getUid()));
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public ServerResponse bingGithub(Long uid, Map githubUser, String token) {
        // 该账号已经绑定了github
        int count = userAuthMapper.selectCount(new QueryWrapper<UserAuth>()
                .eq("uid", uid)
                .eq("identity_type", "github")
                .eq("state", 1));
        if (count > 0) {
            return ServerResponse.error("该账号已经绑定了github", 30001);
        }

        UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .eq("identity_type", "github")
                .eq("identifier", githubUser.get("node_id"))
                .eq("state", 1));
        if (userAuth == null) {
            userAuth = new UserAuth();
            userAuth.setIdentityType("github");
            userAuth.setIdentifier((String) githubUser.get("node_id"));
        } else {
            //原账号绑定多种平台需要解绑才能绑定现有帐户
            List<UserAuth> oldList = userAuthMapper.selectList(new QueryWrapper<UserAuth>()
                    .eq("uid", userAuth.getUid())
                    .eq("state", 1));
            if (oldList.size() > 1) {
                return ServerResponse.error("该github已绑定其他帐号，请解绑后再绑定", 30001);
            }
            // 注销原账号
            userService.deleteUser(userAuth.getUid());
            // 评论迁移
            commentMapper.updateUid(userAuth.getUid(), uid);
            // TODO 其他

        }
        // 插入新记录
        userAuth.setUid(uid);
        userAuth.setCreateTime(new Date());
        userAuth.setPassword(token);
        userAuthMapper.insert(userAuth);
        return ServerResponse.success(null, "绑定成功");
    }
}
