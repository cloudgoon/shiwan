/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.entity.admin;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 关于我们Entity
 * @author orange
 * @version 2018-09-04
 */
public class Aboutus extends DataEntity<Aboutus> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// 标题
	private String aboutus;		// 关于我们
	
	public Aboutus() {
		super();
	}

	public Aboutus(String id){
		super(id);
	}

	@ExcelField(title="标题", align=2, sort=1)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@ExcelField(title="关于我们", align=2, sort=2)
	public String getAboutus() {
		return aboutus;
	}

	public void setAboutus(String aboutus) {
		this.aboutus = aboutus;
	}
	
}