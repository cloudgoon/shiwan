/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.entity.admin;

import com.jeeplus.modules.game.entity.admin.Agency;

import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 代理提现Entity
 * @author orange
 * @version 2018-09-20
 */
public class AgencyWithdraw extends DataEntity<AgencyWithdraw> {
	
	private static final long serialVersionUID = 1L;
	private Double sum;		// 金额
	private String state;		// 提现状态
	private Agency agency;		// 提现代理
	
	public AgencyWithdraw() {
		super();
	}

	public AgencyWithdraw(String id){
		super(id);
	}

	@ExcelField(title="金额", align=2, sort=1)
	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}
	
	@ExcelField(title="提现状态", dictType="withdraw_state", align=2, sort=2)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@ExcelField(title="提现代理", fieldType=Agency.class, value="agency.name", align=2, sort=3)
	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}
	
}