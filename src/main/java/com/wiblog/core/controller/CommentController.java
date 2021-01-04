package com.wiblog.core.controller;

import com.wiblog.core.aop.RequestRequire;
import com.wiblog.core.common.BaseController;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Comment;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.ICommentService;
import com.wiblog.core.service.IUserRoleService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 评论控制层
 *
 * @author pwm
 * @date 2019/9/1
 */
@Log
@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController {

    private final ICommentService commentService;

    private final IUserRoleService userRoleService;

    @Autowired
    public CommentController(ICommentService commentService, IUserRoleService userRoleService) {
        this.commentService = commentService;
        this.userRoleService = userRoleService;
    }


    /**
     * 发表评论
     *
     * @param comment comment
     * @return ServerResponse
     */
    @PostMapping("/reply")
    @RequestRequire(require = "articleId,content", parameter = Comment.class)
    public ServerResponse<?> reply(Comment comment, HttpServletRequest request) {
        User user = getLoginUser(request);
        comment.setUid(user.getUid());
        return commentService.reply(comment);
    }

    /**
     * 获取文章评论
     *
     * @param articleId 文章id
     * @param pageNum   pageNum
     * @param pageSize  pageSize
     * @param orderBy   orderBy
     * @return ServerResponse
     */
    @PostMapping("/commentListPage")
    public ServerResponse<?> commentListPage(
            Long articleId,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy", defaultValue = "asc") String orderBy) {
        if (articleId == null) {
            return ServerResponse.error("参数错误", 300001);
        }
        return commentService.commentListPage(articleId, pageNum, pageSize, orderBy);
    }

    /**
     * 获取评论管理列表
     *
     * @param articleId 文章id
     * @param title     文章标题模糊查询
     * @param state     评论状态
     * @param username  用户名模糊查询
     * @param pageNum   pageNum
     * @param pageSize  pageSize
     * @param orderBy   orderBy
     * @return ServerResponse
     */
    @PostMapping("/commentManageListPage")
    public ServerResponse<?> commentManageListPage(
            @RequestParam(value = "articleId", required = false) Long articleId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "state", required = false) Integer state,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy", defaultValue = "asc") String orderBy) {
        return commentService.commentManageListPage(articleId, title, state, username, pageNum, pageSize, orderBy);
    }

    @PostMapping("/deleteComment")
    public ServerResponse<?> deleteComment(Integer id) {
        return commentService.deleteComment(id);
    }

    /**
     * 恢复删除
     *
     * @param id id
     * @return ServerResponse
     */
    @PostMapping("/restoreComment")
    public ServerResponse<?> restoreComment(Integer id) {
        return commentService.restoreComment(id);
    }

    /**
     * 获取用户评论
     *
     * @param type type comment/reply
     * @return ServerResponse
     */
    @GetMapping("/getUserComment")
    public ServerResponse<?> getUserComment(HttpServletRequest request, Long uid, String type,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "desc") String orderBy) {
        User user = getLoginUser(request);
        boolean isPermit = false;
        // 用户本身 或 管理员
        if (user != null && (user.getUid().equals(uid) || userRoleService.checkAuthorize(user, RoleEnum.ADMIN).isSuccess())) {
            isPermit = true;
        }
        return commentService.getUserComment(uid, pageNum, pageSize, orderBy, isPermit, type);
    }
}
