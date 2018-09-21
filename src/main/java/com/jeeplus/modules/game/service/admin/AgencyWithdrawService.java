/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.game.entity.admin.AgencyWithdraw;
import com.jeeplus.modules.game.mapper.admin.AgencyWithdrawMapper;

/**
 * 代理提现Service
 * @author orange
 * @version 2018-09-20
 */
@Service
@Transactional(readOnly = true)
public class AgencyWithdrawService extends CrudService<AgencyWithdrawMapper, AgencyWithdraw> {

	public AgencyWithdraw get(String id) {
		return super.get(id);
	}
	
	public List<AgencyWithdraw> findList(AgencyWithdraw agencyWithdraw) {
		return super.findList(agencyWithdraw);
	}
	
	public Page<AgencyWithdraw> findPage(Page<AgencyWithdraw> page, AgencyWithdraw agencyWithdraw) {
		return super.findPage(page, agencyWithdraw);
	}
	
	@Transactional(readOnly = false)
	public void save(AgencyWithdraw agencyWithdraw) {
		super.save(agencyWithdraw);
	}
	
	@Transactional(readOnly = false)
	public void delete(AgencyWithdraw agencyWithdraw) {
		super.delete(agencyWithdraw);
	}
	
}