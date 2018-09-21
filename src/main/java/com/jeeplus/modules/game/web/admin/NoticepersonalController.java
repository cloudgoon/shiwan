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
import com.jeeplus.modules.game.entity.admin.Noticepersonal;
import com.jeeplus.modules.game.service.admin.NoticepersonalService;

/**
 * 个人中心公告Controller
 * @author orange
 * @version 2018-09-03
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/noticepersonal")
public class NoticepersonalController extends BaseController {

	@Autowired
	private NoticepersonalService noticepersonalService;
	
	@ModelAttribute
	public Noticepersonal get(@RequestParam(required=false) String id) {
		Noticepersonal entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = noticepersonalService.get(id);
		}
		if (entity == null){
			entity = new Noticepersonal();
		}
		return entity;
	}
	
	/**
	 * 个人中心公告列表页面
	 */
	@RequiresPermissions("game:admin:noticepersonal:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/noticepersonalList";
	}
	
		/**
	 * 个人中心公告列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:noticepersonal:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Noticepersonal noticepersonal, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Noticepersonal> page = noticepersonalService.findPage(new Page<Noticepersonal>(request, response), noticepersonal); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑个人中心公告表单页面
	 */
	@RequiresPermissions(value={"game:admin:noticepersonal:view","game:admin:noticepersonal:add","game:admin:noticepersonal:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Noticepersonal noticepersonal, Model model) {
		model.addAttribute("noticepersonal", noticepersonal);
		return "modules/game/admin/noticepersonalForm";
	}

	/**
	 * 保存个人中心公告
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:noticepersonal:add","game:admin:noticepersonal:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Noticepersonal noticepersonal, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, noticepersonal)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		noticepersonalService.save(noticepersonal);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存个人中心公告成功");
		return j;
	}
	
	/**
	 * 删除个人中心公告
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:noticepersonal:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Noticepersonal noticepersonal, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		noticepersonalService.delete(noticepersonal);
		j.setMsg("删除个人中心公告成功");
		return j;
	}
	
	/**
	 * 批量删除个人中心公告
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:noticepersonal:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			noticepersonalService.delete(noticepersonalService.get(id));
		}
		j.setMsg("删除个人中心公告成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:noticepersonal:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Noticepersonal noticepersonal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "个人中心公告"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Noticepersonal> page = noticepersonalService.findPage(new Page<Noticepersonal>(request, response, -1), noticepersonal);
    		new ExportExcel("个人中心公告", Noticepersonal.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出个人中心公告记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:noticepersonal:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Noticepersonal> list = ei.getDataList(Noticepersonal.class);
			for (Noticepersonal noticepersonal : list){
				try{
					noticepersonalService.save(noticepersonal);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条个人中心公告记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条个人中心公告记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入个人中心公告失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/noticepersonal/?repage";
    }
	
	/**
	 * 下载导入个人中心公告数据模板
	 */
	@RequiresPermissions("game:admin:noticepersonal:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "个人中心公告数据导入模板.xlsx";
    		List<Noticepersonal> list = Lists.newArrayList(); 
    		new ExportExcel("个人中心公告数据", Noticepersonal.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/noticepersonal/?repage";
    }

}