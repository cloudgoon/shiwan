/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.entity.admin;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 商品管理Entity
 * @author orange
 * @version 2018-09-03
 */
public class Shop extends DataEntity<Shop> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 商品名
	private String description;		// 商品描述
	private String picture;		// 商品图片
	private Integer hot;		// 热度
	
	public Shop() {
		super();
	}

	public Shop(String id){
		super(id);
	}

	@ExcelField(title="商品名", align=2, sort=1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ExcelField(title="商品描述", align=2, sort=2)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@ExcelField(title="商品图片", align=2, sort=3)
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	@ExcelField(title="热度", align=2, sort=4)
	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}
	
}