/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.game.entity.admin.Agency;
import com.jeeplus.modules.game.mapper.admin.AgencyMapper;

/**
 * 代理Service
 * @author orange
 * @version 2018-09-20
 */
@Service
@Transactional(readOnly = true)
public class AgencyService extends CrudService<AgencyMapper, Agency> {

	public Agency get(String id) {
		return super.get(id);
	}
	
	public List<Agency> findList(Agency agency) {
		return super.findList(agency);
	}
	
	public Page<Agency> findPage(Page<Agency> page, Agency agency) {
		return super.findPage(page, agency);
	}
	
	@Transactional(readOnly = false)
	public void save(Agency agency) {
		super.save(agency);
	}
	
	@Transactional(readOnly = false)
	public void delete(Agency agency) {
		super.delete(agency);
	}
	
}