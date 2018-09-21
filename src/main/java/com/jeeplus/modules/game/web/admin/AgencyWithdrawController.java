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
import com.jeeplus.modules.game.entity.admin.AgencyWithdraw;
import com.jeeplus.modules.game.service.admin.AgencyWithdrawService;

/**
 * 代理提现Controller
 * @author orange
 * @version 2018-09-20
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/agencyWithdraw")
public class AgencyWithdrawController extends BaseController {

	@Autowired
	private AgencyWithdrawService agencyWithdrawService;
	
	@ModelAttribute
	public AgencyWithdraw get(@RequestParam(required=false) String id) {
		AgencyWithdraw entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = agencyWithdrawService.get(id);
		}
		if (entity == null){
			entity = new AgencyWithdraw();
		}
		return entity;
	}
	
	/**
	 * 代理提现列表页面
	 */
	@RequiresPermissions("game:admin:agencyWithdraw:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/agencyWithdrawList";
	}
	
		/**
	 * 代理提现列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agencyWithdraw:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(AgencyWithdraw agencyWithdraw, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AgencyWithdraw> page = agencyWithdrawService.findPage(new Page<AgencyWithdraw>(request, response), agencyWithdraw); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑代理提现表单页面
	 */
	@RequiresPermissions(value={"game:admin:agencyWithdraw:view","game:admin:agencyWithdraw:add","game:admin:agencyWithdraw:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(AgencyWithdraw agencyWithdraw, Model model) {
		model.addAttribute("agencyWithdraw", agencyWithdraw);
		return "modules/game/admin/agencyWithdrawForm";
	}

	/**
	 * 保存代理提现
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:agencyWithdraw:add","game:admin:agencyWithdraw:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(AgencyWithdraw agencyWithdraw, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, agencyWithdraw)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		agencyWithdrawService.save(agencyWithdraw);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存代理提现成功");
		return j;
	}
	
	/**
	 * 删除代理提现
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agencyWithdraw:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(AgencyWithdraw agencyWithdraw, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		agencyWithdrawService.delete(agencyWithdraw);
		j.setMsg("删除代理提现成功");
		return j;
	}
	
	/**
	 * 批量删除代理提现
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agencyWithdraw:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			agencyWithdrawService.delete(agencyWithdrawService.get(id));
		}
		j.setMsg("删除代理提现成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:agencyWithdraw:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(AgencyWithdraw agencyWithdraw, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "代理提现"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<AgencyWithdraw> page = agencyWithdrawService.findPage(new Page<AgencyWithdraw>(request, response, -1), agencyWithdraw);
    		new ExportExcel("代理提现", AgencyWithdraw.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出代理提现记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:agencyWithdraw:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<AgencyWithdraw> list = ei.getDataList(AgencyWithdraw.class);
			for (AgencyWithdraw agencyWithdraw : list){
				try{
					agencyWithdrawService.save(agencyWithdraw);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条代理提现记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条代理提现记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入代理提现失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/agencyWithdraw/?repage";
    }
	
	/**
	 * 下载导入代理提现数据模板
	 */
	@RequiresPermissions("game:admin:agencyWithdraw:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "代理提现数据导入模板.xlsx";
    		List<AgencyWithdraw> list = Lists.newArrayList(); 
    		new ExportExcel("代理提现数据", AgencyWithdraw.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/agencyWithdraw/?repage";
    }

}