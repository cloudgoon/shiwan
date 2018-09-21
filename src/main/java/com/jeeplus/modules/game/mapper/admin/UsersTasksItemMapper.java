/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.core.persistence.BaseMapper;
import com.jeeplus.core.persistence.annotation.MyBatisMapper;
import com.jeeplus.modules.game.entity.admin.UsersTasksItem;

/**
 * 用户任务项管理MAPPER接口
 * @author orange
 * @version 2018-08-06
 */
@MyBatisMapper
public interface UsersTasksItemMapper extends BaseMapper<UsersTasksItem> {
	void updateUsersTasksItem(@Param(value="imgUrl")String imgUrl,@Param(value="commitContent")String commitContent,@Param(value="usersTasksId")String usersTasksId,@Param(value="state")String state);
	List<UsersTasksItem> selectByUsersIdAndTasksId(@Param(value="usersId")String usersId,@Param(value="tasksId")String tasksId);
	List<UsersTasksItem> selectByUsersId(String usersId);
	List<UsersTasksItem> selectPassed(String usersId);
	List<UsersTasksItem> selectPassedAndNot(String usersId);
	Integer selectTodayFinished();
	Integer selectTodayReward();


}