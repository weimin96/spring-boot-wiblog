package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.entity.Ops;
import com.wiblog.core.mapper.OpsMapper;
import com.wiblog.core.service.IOpsService;

import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author pwm
 * @since 2019-10-24
 */
@Service
public class OpsServiceImpl extends ServiceImpl<OpsMapper, Ops> implements IOpsService {

}
