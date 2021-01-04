package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Message;
import com.wiblog.core.mapper.MessageMapper;
import com.wiblog.core.service.IMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务实现类
 *
 * @author pwm
 * @since 2019-11-06
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }


    @Override
    public ServerResponse<?> getMessageCount(Long id) {
        List<Map> list = messageMapper.selectCountList(id);
        return ServerResponse.success(list);
    }
}
