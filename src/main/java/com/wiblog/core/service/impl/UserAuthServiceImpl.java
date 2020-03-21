package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.entity.UserAuth;
import com.wiblog.core.mapper.UserAuthMapper;
import com.wiblog.core.service.IUserAuthService;

import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author pwm
 * @since 2019-11-07
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements IUserAuthService {

}
