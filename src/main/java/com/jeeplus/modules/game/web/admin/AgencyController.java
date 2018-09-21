/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.game.web.admin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.jeeplus.modules.game.entity.admin.Agency;
import com.jeeplus.modules.game.service.admin.AgencyService;

/**
 * 代理Controller
 * @author orange
 * @version 2018-09-20
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/agency")
public class AgencyController extends BaseController {

	@Autowired
	private AgencyService agencyService;
	
	@ModelAttribute
	public Agency get(@RequestParam(required=false) String id) {
		Agency entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = agencyService.get(id);
		}
		if (entity == null){
			entity = new Agency();
		}
		return entity;
	}
	
	/**
	 * 代理列表页面
	 */
	@RequiresPermissions("game:admin:agency:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/agencyList";
	}
	
		/**
	 * 代理列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agency:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Agency agency, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Agency> page = agencyService.findPage(new Page<Agency>(request, response), agency); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑代理表单页面
	 */
	@RequiresPermissions(value={"game:admin:agency:view","game:admin:agency:add","game:admin:agency:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Agency agency, Model model) {
		model.addAttribute("agency", agency);
		return "modules/game/admin/agencyForm";
	}

	/**
	 * 保存代理
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:agency:add","game:admin:agency:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Agency agency, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, agency)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		agencyService.save(agency);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存代理成功");
		return j;
	}
	
	/**
	 * 删除代理
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agency:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Agency agency, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		agencyService.delete(agency);
		j.setMsg("删除代理成功");
		return j;
	}
	
	/**
	 * 批量删除代理
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agency:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			agencyService.delete(agencyService.get(id));
		}
		j.setMsg("删除代理成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agency:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Agency agency, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "代理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Agency> page = agencyService.findPage(new Page<Agency>(request, response, -1), agency);
    		new ExportExcel("代理", Agency.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出代理记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:agency:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Agency> list = ei.getDataList(Agency.class);
			for (Agency agency : list){
				try{
					agencyService.save(agency);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条代理记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条代理记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入代理失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/agency/?repage";
    }
	
	/**
	 * 下载导入代理数据模板
	 */
	@RequiresPermissions("game:admin:agency:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "代理数据导入模板.xlsx";
    		List<Agency> list = Lists.newArrayList(); 
    		new ExportExcel("代理数据", Agency.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/agency/?repage";
    }

}