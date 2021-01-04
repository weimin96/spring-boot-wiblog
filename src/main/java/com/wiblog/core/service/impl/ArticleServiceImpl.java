package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.User;
import com.wiblog.core.mapper.ArticleMapper;
import com.wiblog.core.service.IArticleService;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.vo.ArticleDetailVo;
import com.wiblog.core.vo.ArticlePageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务实现类
 *
 * @author pwm
 * @since 2019-06-12
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private final ArticleMapper articleMapper;

    private final IUserRoleService userRoleService;

    private final RedisTemplate<String, Object> redisTemplate;

    public ArticleServiceImpl(ArticleMapper articleMapper, IUserRoleService userRoleService, RedisTemplate<String, Object> redisTemplate) {
        this.articleMapper = articleMapper;
        this.userRoleService = userRoleService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ServerResponse<IPage<ArticlePageVo>> articlePageList(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<ArticlePageVo> iPage = articleMapper.selectPageList(page, 0, categoryId);
        return ServerResponse.success(iPage, "获取文章列表成功");
    }

    @Override
    public ServerResponse<IPage<ArticlePageVo>> articlesManage(Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        IPage<ArticlePageVo> iPage = articleMapper.selectPageList(page, null, null);
        return ServerResponse.success(iPage, "获取文章列表成功");
    }

    @Override
    public ServerResponse<?> delArticle(Long id) {
        if (id == null) {
            return ServerResponse.success(null, "删除失败");
        }
        int count = articleMapper.updateState(id);
        if (count == 0) {
            return ServerResponse.success(null, "删除失败");
        }
        Article article = articleMapper.selectById(id);
        String title = "";
        if (article != null) {
            title = article.getTitle();
        }
        return ServerResponse.success(null, "删除成功", title);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerResponse<?> getArticleRank() {
        Set<TypedTuple<Object>> rankSet = redisTemplate.opsForZSet().reverseRangeWithScores(Constant.RedisKey.ARTICLE_RANKING_KEY, 0, 9);
        List<Map<String, Object>> result = new ArrayList<>();
        if (rankSet != null && rankSet.size() > 0) {
            Map<Object, Object> entry = redisTemplate.opsForHash().entries(Constant.RedisKey.ARTICLE_DETAIL_KEY);
            for (TypedTuple<Object> o : rankSet) {
                Map<String, Object> map = (Map<String, Object>) entry.get(o.getValue());
                if (map != null && o.getScore() != null && Double.compare(o.getScore(), 0.0d) != 0) {
                    map.put("score", o.getScore());
                    result.add(map);
                }
            }
        }
        return ServerResponse.success(result);
    }

    @Override
    public ServerResponse<ArticleDetailVo> getArticle(String url, User user) {
        ArticleDetailVo detailVo = articleMapper.selectArticleByUrl(url);
        if (detailVo == null) {
            return ServerResponse.error("获取文章失败", 20001);
        }
        // 文章私有且没有管理员权限不给看
        if (detailVo.getPrivately() && !userRoleService.checkAuthorize(user, RoleEnum.ADMIN).isSuccess()) {
            return ServerResponse.error("获取文章失败", 20001);
        }
        return ServerResponse.success(detailVo, "获取文章成功");
    }


}
