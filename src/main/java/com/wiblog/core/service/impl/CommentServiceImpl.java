package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Comment;
import com.wiblog.core.entity.UserSetting;
import com.wiblog.core.event.CommentEvent;
import com.wiblog.core.mapper.CommentMapper;
import com.wiblog.core.mapper.UserSettingMapper;
import com.wiblog.core.service.ICommentService;
import com.wiblog.core.service.IMailService;
import com.wiblog.core.vo.CommentManageVo;
import com.wiblog.core.vo.CommentVo;
import com.wiblog.core.vo.SubCommentVo;
import com.wiblog.core.vo.UserCommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服务实现类
 *
 * @author pwm
 * @since 2019-09-01
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Autowired
    private ApplicationEventPublisher publisher;


    @Override
    public ServerResponse<?> reply(Comment comment) {
        Date date = new Date();
        comment.setId(null);
        comment.setLikes(0);
        comment.setCreateTime(date);
        comment.setUpdateTime(date);
        // 没有回复id是回复文章
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
            comment.setGenId(0L);
            // 查找主评论数量
            int floor = commentMapper.selectCount(new QueryWrapper<Comment>()
                    .eq("article_id", comment.getArticleId())
                    .eq("gen_id", 0L));
            comment.setFloor(++floor);
        }
        int count = commentMapper.insert(comment);
        if (count <= 0) {
            return ServerResponse.error("评论失败", 40001);
        }
        // 发布评论事件
        publisher.publishEvent(new CommentEvent(this,comment));
        return ServerResponse.success(null, "评论成功");
    }

    @Override
    public ServerResponse<?> commentListPage(Long id, Integer pageNum, Integer pageSize, String orderBy) {
        Page<CommentVo> page = new Page<>(pageNum, pageSize);
        orderBy(page, orderBy);
        IPage<CommentVo> commentPage = commentMapper.selectCommentPage(page, id);
        for (CommentVo item : commentPage.getRecords()) {
            List<SubCommentVo> subComment = commentMapper.selectSubCommentLimit(item.getId(), orderBy);
            item.setSubCommentVoList(subComment);
        }
        Map<String, Object> result = new HashMap<>(16);
        result.put("data", commentPage);
        result.put("time", System.currentTimeMillis());
        return ServerResponse.success(result, "获取评论成功");
    }

    @Override
    public ServerResponse<?> commentManageListPage(Long articleId, String title, Integer state, String username, Integer pageNum, Integer pageSize, String orderBy) {
        Page<CommentManageVo> page = new Page<>(pageNum, pageSize);
        orderBy(page, orderBy);
        IPage<CommentManageVo> commentPage = commentMapper.selectCommentManagePage(page, articleId, state, title, username);
        return ServerResponse.success(commentPage, "获取评论成功");
    }

    @Override
    public ServerResponse<?> deleteComment(Integer id) {
        int count = commentMapper.updateStateById(id);
        return ServerResponse.success(count, "删除评论成功");
    }

    @Override
    public ServerResponse<?> restoreComment(Integer id) {
        int count = commentMapper.restoreStateById(id);
        return ServerResponse.success(count, "恢复删除评论成功");
    }

    @Override
    public ServerResponse<?> getUserComment(Long uid, Integer pageNum, Integer pageSize, String orderBy, Boolean isPermit, String type) {
        // 不是管理员或自己
        if (!isPermit) {
            UserSetting userSetting = userSettingMapper.selectOne(new QueryWrapper<UserSetting>().eq("uid", uid));
            // 设置了不开放
            if (userSetting != null && userSetting.getComment().equals(0)) {
                return ServerResponse.error("用户设置了权限", 300001);
            }
        }

        Page<UserCommentVo> page = new Page<>(pageNum, pageSize);
        orderBy(page, orderBy);
        IPage<UserCommentVo> list;
        if ("comment".equals(type)) {
            list = commentMapper.selectCommentByUid(page, uid);
        } else {
            list = commentMapper.selectUserReplyByUid(page, uid);
        }

        return ServerResponse.success(list);
    }

    private void orderBy(Page<?> page, String orderBy) {
        if ("asc".equals(orderBy)) {
            page.setAsc("create_time");
        } else {
            page.setDesc("create_time");
        }
    }

}
