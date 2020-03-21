package com.wiblog.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Message;

/**
 *   服务类
 *
 * @author pwm
 * @since 2019-11-06
 */
public interface IMessageService extends IService<Message> {

    ServerResponse getMessageCount(Long id);
}
