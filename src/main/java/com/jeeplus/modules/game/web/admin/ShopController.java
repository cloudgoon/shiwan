/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.web.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.game.entity.admin.Shop;
import com.jeeplus.modules.game.service.admin.ShopService;

/**
 * 商品管理Controller
 * @author orange
 * @version 2018-09-03
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/shop")
public class ShopController extends BaseController {

	@Autowired
	private ShopService shopService;
	
	@ModelAttribute
	public Shop get(@RequestParam(required=false) String id) {
		Shop entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = shopService.get(id);
		}
		if (entity == null){
			entity = new Shop();
		}
		return entity;
	}
	
	/**
	 * 商品列表页面
	 */
	@RequiresPermissions("game:admin:shop:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/shopList";
	}
	
		/**
	 * 商品列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:shop:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Shop shop, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Shop> page = shopService.findPage(new Page<Shop>(request, response), shop); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑商品表单页面
	 */
	@RequiresPermissions(value={"game:admin:shop:view","game:admin:shop:add","game:admin:shop:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Shop shop, Model model) {
		model.addAttribute("shop", shop);
		return "modules/game/admin/shopForm";
	}

	/**
	 * 保存商品
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:shop:add","game:admin:shop:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Shop shop, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, shop)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		shopService.save(shop);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存商品成功");
		return j;
	}
	
	/**
	 * 删除商品
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:shop:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Shop shop, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		shopService.delete(shop);
		j.setMsg("删除商品成功");
		return j;
	}
	
	/**
	 * 批量删除商品
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:shop:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			shopService.delete(shopService.get(id));
		}
		j.setMsg("删除商品成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:shop:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Shop shop, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "商品"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Shop> page = shopService.findPage(new Page<Shop>(request, response, -1), shop);
    		new ExportExcel("商品", Shop.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出商品记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:shop:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Shop> list = ei.getDataList(Shop.class);
			for (Shop shop : list){
				try{
					shopService.save(shop);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条商品记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条商品记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入商品失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/shop/?repage";
    }
	
	/**
	 * 下载导入商品数据模板
	 */
	@RequiresPermissions("game:admin:shop:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "商品数据导入模板.xlsx";
    		List<Shop> list = Lists.newArrayList(); 
    		new ExportExcel("商品数据", Shop.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/shop/?repage";
    }

}