package com.wiblog.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.aop.OpsRecord;
import com.wiblog.core.aop.RequestRequire;
import com.wiblog.core.common.BaseController;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.User;
import com.wiblog.core.es.EsArticleRepository;
import com.wiblog.core.service.IArticleService;
import com.wiblog.core.utils.PinYinUtil;
import com.wiblog.core.utils.WiblogUtil;
import com.wiblog.core.utils.WordFilterUtil;
import com.wiblog.core.vo.ArticleDetailVo;
import com.wiblog.core.vo.ArticlePageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 文章控制层
 *
 * @author pwm
 * @date 2019-06-12
 */
@Slf4j
@RestController
@RequestMapping("/post")
public class ArticleController extends BaseController {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Pattern PATTERN_HIGH_LIGHT = Pattern.compile("<(p)>(.*?)<\\/\\1>");

    private final IArticleService articleService;

    private final WordFilterUtil wordFilterUtil;

    private final PinYinUtil pinYinUtil;

    private final EsArticleRepository articleRepository;

    final
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ArticleController(IArticleService articleService, RedisTemplate<String, Object> redisTemplate, WordFilterUtil wordFilterUtil, PinYinUtil pinYinUtil, EsArticleRepository articleRepository, ElasticsearchOperations elasticsearchOperations) {
        this.articleService = articleService;
        this.redisTemplate = redisTemplate;
        this.wordFilterUtil = wordFilterUtil;
        this.pinYinUtil = pinYinUtil;
        this.articleRepository = articleRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * 首页文章列表
     */
    @PostMapping("/articles")
    public ServerResponse<IPage<ArticlePageVo>> articlePageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            Long categoryId) {
        return articleService.articlePageList(pageNum, pageSize, categoryId);
    }

    /**
     * 后台管理文章列表
     */
    @PostMapping("/articlesManage")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<IPage<ArticlePageVo>> articlesManage(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return articleService.articlesManage(pageNum, pageSize);
    }

    /**
     * 获取所有文章标题列表
     * 评论管理界面-按文章标题查找
     * 管理员权限
     *
     * @return ServerResponse
     */
    @PostMapping("/allArticles")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<?> articlePageList() {
        LambdaQueryWrapper<Article> queryWrapper = new QueryWrapper<Article>().lambda();
        queryWrapper.select(Article::getTitle).eq(Article::getState, 1);
        List<Map<String, Object>> list = articleService.listMaps(queryWrapper);
        return ServerResponse.success(list);
    }

    /**
     * 文章详细信息
     */
    @GetMapping("/get/{id}")
    public ServerResponse<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.getById(id);
        return ServerResponse.success(article, "获取文章成功");
    }

    /**
     * 通过url获取文章信息
     */
    @GetMapping("/getArticle")
    public ServerResponse<ArticleDetailVo> getArticle(HttpServletRequest request, String url) {
        User user = getLoginUser(request);
        return articleService.getArticle(url, user);
    }

    /**
     * 发表文章 管理员权限
     *
     * @param article article
     * @return ServerResponse
     */
    @PostMapping("/push")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @OpsRecord(msg = "发表了文章<<{0}>>")
    @RequestRequire(require = "title,content,tags,categoryId,imgUrl", parameter = Article.class)
    public ServerResponse<String> pushArticle(HttpServletRequest request, Article article) {

        // 分词
        List<String> titles = wordFilterUtil.getParticiple(article.getTitle());
        String title = pinYinUtil.getStringPinYin(titles);
        String articleUrl = "/post/" + title;
        Article sameUrlArticle = articleService.getOne(new QueryWrapper<Article>().eq("article_url", articleUrl));
        if (sameUrlArticle != null) {
            return ServerResponse.error("文章发表失败，已存在相同标题", 30001);
        }
        Date date = new Date();

        // 提取纯文本
        String content = StringUtils.substring(article.getContent(), 0, 500);
        content = WiblogUtil.mdToHtml(content)
                .replaceAll(Constant.Regular.HTML, "")
                .replaceAll(Constant.Regular.WRAP, "");
        content = StringUtils.substring(content, 0, 100);
        User user = getLoginUser(request);
        // 赋值
        article.setUpdateTime(date)
                .setCreateTime(date)
                .setArticleUrl(articleUrl)
                .setUid(user.getUid())
                .setAuthor(user.getUsername())
                .setArticleSummary(content);
        boolean bool = articleService.save(article);

        if (!bool) {
            return ServerResponse.error("文章发表失败", 30001);
        }
        return ServerResponse.success(articleUrl, "文章发表成功", title);

    }

    /**
     * 修改文章 管理员权限
     *
     * @param article article
     * @return ServerResponse
     */
    @PostMapping("/update")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @OpsRecord(msg = "修改了文章<<{0}>>")
    @RequestRequire(require = "id,title,content,tags,categoryId", parameter = Article.class)
    public ServerResponse<String> updateArticle(Article article) {
        Date date = new Date();
        article.setUpdateTime(date);
        // 提取纯文本
        String content = WiblogUtil.mdToHtml(article.getContent());
        content = content.replaceAll("<[^>]+>", "");
        content = content.replaceAll("\\s*|\t|\r|\n", "");
        article.setArticleSummary(content.substring(0, 100));
        boolean bool = articleService.updateById(article);
        if (!bool) {
            return ServerResponse.error("文章发表失败", 30001);
        }
        return ServerResponse.success(null, "文章修改成功", article.getTitle());
    }

    @PostMapping("/del")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @OpsRecord(msg = "删除了文章<<{0}>>")
    public ServerResponse<?> delArticle(Long id) {
        return articleService.delArticle(id);
    }

    /**
     * TODO 全文检索
     *
     * @param keyword  keyword
     * @param pageNum  pageNum
     * @param pageSize pageSize
     * @return ServerResponse
     */
    @GetMapping("/searchArticle")
    public ServerResponse<?> searchArticle(@RequestParam(required = false, defaultValue = "") String keyword,
                                           @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        keyword = keyword.trim();
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        //高亮拼接的前缀
        String preTags = "<p>";
        //高亮拼接的后缀
        String postTags = "</p>";
        //查询具体的字段
        String[] fieldNames = {"title", "content"};
        //创建queryBuilder查询条件
        /*QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, fieldNames);
        //创建search对象
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).withHighlightFields(
                new HighlightBuilder.Field(fieldNames[0]).preTags(preTags).postTags(postTags),
                new HighlightBuilder.Field(fieldNames[1]).preTags(preTags).postTags(postTags)
        ).withPageable(pageable).build();
        elasticsearchOperations.multiSearch()
        //执行分页查询
        Page<EsArticle> page = .queryForPage(query,EsArticle.class,new SearchResultMapper(){
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                List<EsArticle> list = new ArrayList<>();
                // 获取高亮结果
                SearchHits searchHits = response.getHits();
                if (searchHits != null){
                    // 获取内容
                    SearchHit[] hits = searchHits.getHits();
                    if (hits.length>0){
                        for (SearchHit hit:hits){
                            EsArticle esArticle = new EsArticle();
                            esArticle.setId(hit.getId());
                            esArticle.setUrl((String) hit.getSourceAsMap().get("url"));
                            esArticle.setCreateTime((Long) hit.getSourceAsMap().get("createTime"));
                            esArticle.setCategoryId(Long.parseLong(hit.getSourceAsMap().get("categoryId") +""));
                            // 获取第一个字段高亮内容
                            HighlightField highlightField1 = hit.getHighlightFields().get(fieldNames[0]);
                            String value1;
                            if (highlightField1!=null){
                                value1 = highlightField1.getFragments()[0].toString();
                            }else {
                                value1 = (String) hit.getSourceAsMap().get(fieldNames[0]);
                            }
                            esArticle.setTitle(value1);
                            // 获取第二个字段高亮内容
                            HighlightField highlightField2 = hit.getHighlightFields().get(fieldNames[1]);
                            String value2;
                            if (highlightField2!=null){
                                value2 = highlightField2.getFragments()[0].toString();
                            }else {
                                value2 = (String) hit.getSourceAsMap().get(fieldNames[1]);
                            }
                            // 截取部分内容
                            Matcher mt = PATTERN_HIGH_LIGHT.matcher(value2);
                            if (mt.find()){
                                int mtStart = mt.start();
                                int start = Math.max(mtStart - 100, 0);
                                int end = Math.min(mtStart+300,value2.length()-1);
                                value2 = value2.substring(start,end);
                            }else {
                                value2 = value2.substring(0,Math.min(300,value2.length()-1));
                            }
                            esArticle.setContent(value2);

                            list.add(esArticle);
                        }
                    }
                }
                return new AggregatedPageImpl<>((List<T>)list);
            }
        });*/

        return ServerResponse.success(null);
    }


    /**
     * 点赞
     *
     * @param articleId 文章id
     * @return ServerResponse
     */
    @PostMapping("/record/like")
    public ServerResponse<Integer> like(Long articleId) {
        Integer count;
        count = (Integer) redisTemplate.opsForHash().get(Constant.RedisKey.LIKE_RECORD_KEY, articleId + "");
        if (count == null) {
            Article article = articleService.getById(articleId);
            if (article == null) {
                return ServerResponse.error("不存在该文章", 30001);
            }
            count = article.getHits();
        }
        redisTemplate.opsForHash().put(Constant.RedisKey.LIKE_RECORD_KEY, String.valueOf(articleId), ++count);

        return ServerResponse.success(count);
    }

    /**
     * 点击率记录
     *
     * @param articleId articleId
     * @return ServerResponse
     */
    @PostMapping("/record/hit")
    public ServerResponse<Integer> hit(HttpServletRequest request, HttpServletResponse response, Long articleId) {
        String check = WiblogUtil.getCookie(request, "article_" + articleId);

        Integer count;
        count = (Integer) redisTemplate.opsForHash().get(Constant.RedisKey.HIT_RECORD_KEY, articleId + "");
        if (count == null) {
            Article article = articleService.getById(articleId);
            if (article == null) {
                return ServerResponse.error("不存在该文章", 30001);
            }
            count = article.getHits();

        }
        if (StringUtils.isNotBlank(check)) {
            return ServerResponse.success(count);
        }
        redisTemplate.opsForHash().put(Constant.RedisKey.HIT_RECORD_KEY, String.valueOf(articleId), ++count);
        // cookie保留2小时
        WiblogUtil.setCookie(response, "article_" + articleId, "1", 60 * 60 * 2);

        return ServerResponse.success(count);
    }

    /**
     * 文章排行榜
     *
     * @return ServerResponse
     */
    @GetMapping("/getArticleRank")
    public ServerResponse<?> getArticleRank() {
        return articleService.getArticleRank();
    }
}
