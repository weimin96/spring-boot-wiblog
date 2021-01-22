package com.wiblog.core.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 文档类 article
 *
 * @author pwm
 * @date 2019/10/25
 */
@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "article")
@Accessors(chain = true)
public class EsArticle  implements Serializable {
    private static final long serialVersionUID = -7577471814974481136L;

    /**
     * id为String
     */
    @Id
    private String id;

    private Long articleId;

    private String title;

    private String content;

    @Field(type = FieldType.Long)
    private Long categoryId;

    private Long createTime;

    @Field(type = FieldType.Text)
    private String url;

    public EsArticle(Long articleId, String title, String content, Long categoryId, Long createTime, String url) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.createTime = createTime;
        this.url = url;
    }


}
