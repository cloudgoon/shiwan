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
import com.jeeplus.modules.game.entity.admin.Aboutus;
import com.jeeplus.modules.game.service.admin.AboutusService;

/**
 * 关于我们Controller
 * @author orange
 * @version 2018-09-04
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/aboutus")
public class AboutusController extends BaseController {

	@Autowired
	private AboutusService aboutusService;
	
	@ModelAttribute
	public Aboutus get(@RequestParam(required=false) String id) {
		Aboutus entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = aboutusService.get(id);
		}
		if (entity == null){
			entity = new Aboutus();
		}
		return entity;
	}
	
	/**
	 * 关于我们列表页面
	 */
	@RequiresPermissions("game:admin:aboutus:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/aboutusList";
	}
	
		/**
	 * 关于我们列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:aboutus:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Aboutus aboutus, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Aboutus> page = aboutusService.findPage(new Page<Aboutus>(request, response), aboutus); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑关于我们表单页面
	 */
	@RequiresPermissions(value={"game:admin:aboutus:view","game:admin:aboutus:add","game:admin:aboutus:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Aboutus aboutus, Model model) {
		model.addAttribute("aboutus", aboutus);
		return "modules/game/admin/aboutusForm";
	}

	/**
	 * 保存关于我们
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:aboutus:add","game:admin:aboutus:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Aboutus aboutus, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, aboutus)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		aboutusService.save(aboutus);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存关于我们成功");
		return j;
	}
	
	/**
	 * 删除关于我们
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:aboutus:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Aboutus aboutus, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		aboutusService.delete(aboutus);
		j.setMsg("删除关于我们成功");
		return j;
	}
	
	/**
	 * 批量删除关于我们
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:aboutus:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			aboutusService.delete(aboutusService.get(id));
		}
		j.setMsg("删除关于我们成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:aboutus:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Aboutus aboutus, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "关于我们"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Aboutus> page = aboutusService.findPage(new Page<Aboutus>(request, response, -1), aboutus);
    		new ExportExcel("关于我们", Aboutus.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出关于我们记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:aboutus:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Aboutus> list = ei.getDataList(Aboutus.class);
			for (Aboutus aboutus : list){
				try{
					aboutusService.save(aboutus);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条关于我们记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条关于我们记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入关于我们失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/aboutus/?repage";
    }
	
	/**
	 * 下载导入关于我们数据模板
	 */
	@RequiresPermissions("game:admin:aboutus:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "关于我们数据导入模板.xlsx";
    		List<Aboutus> list = Lists.newArrayList(); 
    		new ExportExcel("关于我们数据", Aboutus.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/aboutus/?repage";
    }

}