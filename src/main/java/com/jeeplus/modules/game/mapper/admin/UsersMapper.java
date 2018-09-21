package com.jeeplus.modules.game.mapper.admin;

import java.util.List;

import com.jeeplus.core.persistence.BaseMapper;
import com.jeeplus.core.persistence.annotation.MyBatisMapper;
import com.jeeplus.modules.game.entity.admin.Users;

/**
 * 用户管理MAPPER接口
 * @author orange
 * @version 2018-09-18
 */
@MyBatisMapper
public interface UsersMapper extends BaseMapper<Users> {
	List<Users> getRichList();
	Users getById(String id);
}