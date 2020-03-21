package com.wiblog.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiblog.core.entity.Comment;
import com.wiblog.core.vo.CommentManageVo;
import com.wiblog.core.vo.CommentVo;
import com.wiblog.core.vo.SubCommentVo;
import com.wiblog.core.vo.UserCommentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author pwm
 * @since 2019-09-01
 */
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 分页查找主评论
     *
     * @param page      page
     * @param articleId articleId
     * @return IPage
     */
    IPage<CommentVo> selectCommentPage(Page page, @Param("articleId") Long articleId);

    /**
     * 查找子评论
     *
     * @param commentId commentId
     * @param orderBy   orderBy
     * @return IPage
     */
    List<SubCommentVo> selectSubCommentLimit(@Param("commentId") Long commentId, @Param("orderBy") String orderBy);

    /**
     * 分页查找所有文章评论
     *
     * @param page      page
     * @param articleId 文章id
     * @param state     评论状态
     * @param title     标题
     * @param username  用户名
     * @return IPage
     */
    IPage<CommentManageVo> selectCommentManagePage(Page<CommentManageVo> page,
                                                   @Param("articleId") Long articleId,
                                                   @Param("state") Integer state,
                                                   @Param("title") String title,
                                                   @Param("username") String username);

    /**
     * 修改评论状态
     *
     * @param id id
     * @return Integer
     */
    Integer updateStateById(Integer id);

    /**
     * 修改评论状态
     *
     * @param id id
     * @return Integer
     */
    Integer restoreStateById(Integer id);

    /**
     * 查询用户评论
     *
     * @param uid  uid
     * @param page page
     * @return List
     */
    IPage<UserCommentVo> selectCommentByUid(Page<UserCommentVo> page, Long uid);

    /**
     * 查询用户被评论回复
     *
     * @param uid uid
     * @param page page
     * @return List
     */
    IPage<UserCommentVo> selectUserReplyByUid(Page<UserCommentVo> page,Long uid);

    /**
     * 更新用户id
     * @param oldUid oldUid
     * @param newUid newUid
     */
    void updateUid(@Param("oldUid") Long oldUid,@Param("newUid") Long newUid);
}
