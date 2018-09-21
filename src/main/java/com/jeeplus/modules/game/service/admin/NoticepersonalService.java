/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.game.entity.admin.Noticepersonal;
import com.jeeplus.modules.game.mapper.admin.NoticepersonalMapper;

/**
 * 个人中心公告Service
 * @author orange
 * @version 2018-09-03
 */
@Service
@Transactional(readOnly = true)
public class NoticepersonalService extends CrudService<NoticepersonalMapper, Noticepersonal> {

	public Noticepersonal get(String id) {
		return super.get(id);
	}
	
	public List<Noticepersonal> findList(Noticepersonal noticepersonal) {
		return super.findList(noticepersonal);
	}
	
	public Page<Noticepersonal> findPage(Page<Noticepersonal> page, Noticepersonal noticepersonal) {
		return super.findPage(page, noticepersonal);
	}
	
	@Transactional(readOnly = false)
	public void save(Noticepersonal noticepersonal) {
		super.save(noticepersonal);
	}
	
	@Transactional(readOnly = false)
	public void delete(Noticepersonal noticepersonal) {
		super.delete(noticepersonal);
	}
	
}