/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.mapper.admin;

import com.jeeplus.core.persistence.BaseMapper;
import com.jeeplus.core.persistence.annotation.MyBatisMapper;
import com.jeeplus.modules.game.entity.admin.News;

/**
 * 新闻MAPPER接口
 * @author orange
 * @version 2018-09-21
 */
@MyBatisMapper
public interface NewsMapper extends BaseMapper<News> {
	
}