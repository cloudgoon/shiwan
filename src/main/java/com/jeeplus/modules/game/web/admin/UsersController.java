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
import com.jeeplus.modules.game.entity.admin.Users;
import com.jeeplus.modules.game.service.admin.UsersService;

/**
 * 用户管理Controller
 * @author orange
 * @version 2018-09-18
 */
@Controller
@RequestMapping(value = "${adminPath}/game/admin/users")
public class UsersController extends BaseController {

	@Autowired
	private UsersService usersService;
	
	@ModelAttribute
	public Users get(@RequestParam(required=false) String id) {
		Users entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = usersService.get(id);
		}
		if (entity == null){
			entity = new Users();
		}
		return entity;
	}
	
	/**
	 * 用户列表页面
	 */
	@RequiresPermissions("game:admin:users:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/game/admin/usersList";
	}
	
		/**
	 * 用户列表数据
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:users:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Users users, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Users> page = usersService.findPage(new Page<Users>(request, response), users); 
		return getBootstrapData(page);
	}

	/**
	 * 查看，增加，编辑用户表单页面
	 */
	@RequiresPermissions(value={"game:admin:users:view","game:admin:users:add","game:admin:users:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Users users, Model model) {
		model.addAttribute("users", users);
		return "modules/game/admin/usersForm";
	}

	/**
	 * 保存用户
	 */
	@ResponseBody
	@RequiresPermissions(value={"game:admin:users:add","game:admin:users:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Users users, Model model, RedirectAttributes redirectAttributes) throws Exception{
		AjaxJson j = new AjaxJson();
		if (!beanValidator(model, users)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		usersService.save(users);//新建或者编辑保存
		j.setSuccess(true);
		j.setMsg("保存用户成功");
		return j;
	}
	
	/**
	 * 删除用户
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:users:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Users users, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		usersService.delete(users);
		j.setMsg("删除用户成功");
		return j;
	}
	
	/**
	 * 批量删除用户
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:users:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			usersService.delete(usersService.get(id));
		}
		j.setMsg("删除用户成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("game:admin:users:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Users users, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "用户"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Users> page = usersService.findPage(new Page<Users>(request, response, -1), users);
    		new ExportExcel("用户", Users.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出用户记录失败！失败信息："+e.getMessage());
		}
			return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("game:admin:users:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Users> list = ei.getDataList(Users.class);
			for (Users users : list){
				try{
					usersService.save(users);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/users/?repage";
    }
	
	/**
	 * 下载导入用户数据模板
	 */
	@RequiresPermissions("game:admin:users:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据导入模板.xlsx";
    		List<Users> list = Lists.newArrayList(); 
    		new ExportExcel("用户数据", Users.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/game/admin/users/?repage";
    }

}