/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.entity.admin;

import com.jeeplus.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 代理Entity
 * @author orange
 * @version 2018-09-20
 */
public class Agency extends DataEntity<Agency> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 姓名
	private String password;		// 密码
	private String phoneNum;		// 手机号
	private User user;		// 系统后台账户
	private Double balance;		// 余额
	private String alipayName;		// 支付宝昵称
	private String alipayAccount;		// 支付宝账号
	
	public Agency() {
		super();
	}

	public Agency(String id){
		super(id);
	}

	@ExcelField(title="姓名", align=2, sort=1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ExcelField(title="密码", align=2, sort=2)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@ExcelField(title="手机号", align=2, sort=3)
	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	@NotNull(message="系统后台账户不能为空")
	@ExcelField(title="系统后台账户", fieldType=User.class, value="user.loginName", align=2, sort=4)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@NotNull(message="余额不能为空")
	@ExcelField(title="余额", align=2, sort=5)
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	@ExcelField(title="支付宝昵称", align=2, sort=6)
	public String getAlipayName() {
		return alipayName;
	}

	public void setAlipayName(String alipayName) {
		this.alipayName = alipayName;
	}
	
	@ExcelField(title="支付宝账号", align=2, sort=7)
	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}
	
}