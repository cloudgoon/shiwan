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
import com.jeeplus.modules.game.entity.admin.News;
import com.jeeplus.modules.game.service.admin.NewsService;

/**
 * 新闻Controller
 * @author orange
 * @version 2018-09-21
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/news")
public class NewsController extends BaseController {

	@Autowired
	private NewsService newsService;
	
	@ModelAttribute
	public News get(@RequestParam(required=false) String id) {
		News entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = newsService.get(id);
		}
		if (entity == null){
			entity = new News();
		}
		return entity;
	}
	
	/**
	 * 新闻列表页面
	 */
	@RequiresPermissions("game:admin:news:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/newsList";
	}
	
		/**
	 * 新闻列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:news:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(News news, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<News> page = newsService.findPage(new Page<News>(request, response), news); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑新闻表单页面
	 */
	@RequiresPermissions(value={"game:admin:news:view","game:admin:news:add","game:admin:news:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(News news, Model model) {
		model.addAttribute("news", news);
		return "modules/game/admin/newsForm";
	}

	/**
	 * 保存新闻
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:news:add","game:admin:news:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(News news, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, news)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		newsService.save(news);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存新闻成功");
		return j;
	}
	
	/**
	 * 删除新闻
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:news:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(News news, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		newsService.delete(news);
		j.setMsg("删除新闻成功");
		return j;
	}
	
	/**
	 * 批量删除新闻
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:news:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			newsService.delete(newsService.get(id));
		}
		j.setMsg("删除新闻成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:news:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(News news, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "新闻"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<News> page = newsService.findPage(new Page<News>(request, response, -1), news);
    		new ExportExcel("新闻", News.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出新闻记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:news:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<News> list = ei.getDataList(News.class);
			for (News news : list){
				try{
					newsService.save(news);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条新闻记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条新闻记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入新闻失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/news/?repage";
    }
	
	/**
	 * 下载导入新闻数据模板
	 */
	@RequiresPermissions("game:admin:news:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "新闻数据导入模板.xlsx";
    		List<News> list = Lists.newArrayList(); 
    		new ExportExcel("新闻数据", News.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/news/?repage";
    }

}