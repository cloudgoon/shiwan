/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.entity.admin;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 个人中心公告Entity
 * @author orange
 * @version 2018-09-03
 */
public class Noticepersonal extends DataEntity<Noticepersonal> {
	
	private static final long serialVersionUID = 1L;
	private String title;		// 标题
	private String picture;		// 标题图片
	private String content;		// 内容
	
	public Noticepersonal() {
		super();
	}

	public Noticepersonal(String id){
		super(id);
	}

	@ExcelField(title="标题", align=2, sort=1)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@ExcelField(title="标题图片", align=2, sort=2)
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	@ExcelField(title="内容", align=2, sort=3)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}