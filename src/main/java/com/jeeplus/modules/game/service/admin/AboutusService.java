/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.game.entity.admin.Aboutus;
import com.jeeplus.modules.game.mapper.admin.AboutusMapper;

/**
 * 关于我们Service
 * @author orange
 * @version 2018-09-04
 */
@Service
@Transactional(readOnly = true)
public class AboutusService extends CrudService<AboutusMapper, Aboutus> {

	public Aboutus get(String id) {
		return super.get(id);
	}
	
	public List<Aboutus> findList(Aboutus aboutus) {
		return super.findList(aboutus);
	}
	
	public Page<Aboutus> findPage(Page<Aboutus> page, Aboutus aboutus) {
		return super.findPage(page, aboutus);
	}
	
	@Transactional(readOnly = false)
	public void save(Aboutus aboutus) {
		super.save(aboutus);
	}
	
	@Transactional(readOnly = false)
	public void delete(Aboutus aboutus) {
		super.delete(aboutus);
	}
	
}