package com.jeeplus.modules.game.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.game.entity.admin.Users;
import com.jeeplus.modules.game.mapper.admin.UsersMapper;

/**
 * 用户管理Service
 * @author orange
 * @version 2018-09-18
 */
@Service
@Transactional(readOnly = true)
public class UsersService extends CrudService<UsersMapper, Users> {
	
	@Autowired
	UsersMapper mapper;
	
	public List<Users> getRichList(){
		return mapper.getRichList();
	}
	public Users getById(String id) {
		return mapper.getById(id);
	}
	public Users get(String id) {
		return super.get(id);
	}
	
	public List<Users> findList(Users users) {
		return super.findList(users);
	}
	
	public Page<Users> findPage(Page<Users> page, Users users) {
		return super.findPage(page, users);
	}
	
	@Transactional(readOnly = false)
	public void save(Users users) {
		super.save(users);
	}
	
	@Transactional(readOnly = false)
	public void delete(Users users) {
		super.delete(users);
	}
	
}