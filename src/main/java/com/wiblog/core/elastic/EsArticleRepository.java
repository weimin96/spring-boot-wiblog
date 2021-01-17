package com.wiblog.core.elastic;

import com.wiblog.core.entity.EsArticle;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/10/25
 */
public interface EsArticleRepository extends ElasticsearchRepository<EsArticle,String> {
    /**
     * 删除
     * @param articleId articleId
     */
    void deleteByArticleId(Long articleId);

    /**
     * 查找
     * @param articleId id
     * @return EsArticle
     */
    EsArticle queryEsArticleByArticleId(Long articleId);
}
