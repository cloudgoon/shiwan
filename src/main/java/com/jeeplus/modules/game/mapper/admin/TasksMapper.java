/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.mapper.admin;

import java.util.List;

import com.jeeplus.core.persistence.BaseMapper;
import com.jeeplus.core.persistence.annotation.MyBatisMapper;
import com.jeeplus.modules.game.entity.admin.Tasks;

/**
 * 任务管理MAPPER接口
 * @author orange
 * @version 2018-08-06
 */
@MyBatisMapper
public interface TasksMapper extends BaseMapper<Tasks> {
		List<Tasks> listTasks();
}