package com.jeeplus.modules.game.web.fore;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.inject.New;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.game.entity.admin.Aboutus;
import com.jeeplus.modules.game.entity.admin.Agency;
import com.jeeplus.modules.game.entity.admin.AgencyWithdraw;
import com.jeeplus.modules.game.entity.admin.Inform;
import com.jeeplus.modules.game.entity.admin.News;
import com.jeeplus.modules.game.entity.admin.Notice;
import com.jeeplus.modules.game.entity.admin.Noticepersonal;
import com.jeeplus.modules.game.entity.admin.Shop;
import com.jeeplus.modules.game.entity.admin.Tasks;
import com.jeeplus.modules.game.entity.admin.TasksStat;
import com.jeeplus.modules.game.entity.admin.Users;
import com.jeeplus.modules.game.entity.admin.UsersTasksItem;
import com.jeeplus.modules.game.entity.admin.Withdraw;
import com.jeeplus.modules.game.service.admin.AboutusService;
import com.jeeplus.modules.game.service.admin.AgencyService;
import com.jeeplus.modules.game.service.admin.AgencyWithdrawService;
import com.jeeplus.modules.game.service.admin.InformService;
import com.jeeplus.modules.game.service.admin.NewsService;
import com.jeeplus.modules.game.service.admin.NoticeService;
import com.jeeplus.modules.game.service.admin.NoticepersonalService;
import com.jeeplus.modules.game.service.admin.ShopService;
import com.jeeplus.modules.game.service.admin.TasksService;
import com.jeeplus.modules.game.service.admin.TasksStatService;
import com.jeeplus.modules.game.service.admin.UsersService;
import com.jeeplus.modules.game.service.admin.UsersTasksItemService;
import com.jeeplus.modules.game.service.admin.WithdrawService;
import com.jeeplus.modules.game.util.AppResponse;
import com.jeeplus.modules.game.util.MsgUtil;
import com.jeeplus.modules.sys.service.SystemService;

import redis.clients.jedis.Jedis;

@Controller
@RequestMapping(value="app")
public class AppInterfaceController {
	@Autowired
	UsersService usersService;
	@Autowired
	TasksService tasksService;
	@Autowired
	UsersTasksItemService itemService;
	@Autowired
	WithdrawService withdrawService;
	@Autowired
	TasksStatService tasksStatService;
	@Autowired
	ShopService shopService;
	@Autowired
	NoticeService noticeService;
	@Autowired
	InformService informService;
	@Autowired
	NoticepersonalService noticepersonalService;
	@Autowired
	AboutusService aboutusService;
	@Autowired
	AgencyService agencyService;
	@Autowired
	AgencyWithdrawService agencyWithdrawService;
	@Autowired
	NewsService newsService;
	
	
	/**
	 * app接口  会员登录
	 * @param phoneNum 手机号
	 * @param password 密码
	 * @return '1'密码正确， '2'密码错误，'3'用户不存在
	 */
	@RequestMapping("login")
	@ResponseBody
	public AppResponse<Users> login(String phoneNum,String password,HttpServletRequest request) {
		
		System.out.println("*****phoneNum:"+phoneNum+" password:"+password+"*****");
		HttpSession session = request.getSession();
		System.out.println("login sessionId:"+session.getId());
		//根据请求中的phoneNum向数据库查询
		Users uniUser = usersService.findUniqueByProperty("phone_num", phoneNum);
		if(uniUser != null) {
			System.out.println(uniUser.toString());
			//若用户存在，则把请求中的密码和加密后的用户密码比较
			if(SystemService.validatePassword(password, uniUser.getPassword())) {
				// '1' 代表密码正确
				session.setAttribute("userId", uniUser.getId());
				System.out.println("userId:"+uniUser.getId());
				return new AppResponse<Users>(1,"登录成功",uniUser);
			}else {
				//2 密码错误
				return new AppResponse<Users>(2,"密码错误",null);
			}
		}else {
			//3 用户不存在
			return new AppResponse<Users>(3,"用户不存在",null);
		}
	}
	
	/**
	 * app接口 代理登录
	 * @param phoneNum 手机号
	 * @param password 密码
	 * @return '1'密码正确， '2'密码错误，'3'用户不存在
	 */
	@RequestMapping("agencyLogin")
	@ResponseBody
	public AppResponse<Agency> agencyLogin(String phoneNum,String password,HttpServletRequest request) {
		
		System.out.println("*****phoneNum:"+phoneNum+" password:"+password+"*****");
		HttpSession session = request.getSession();
		System.out.println("login sessionId:"+session.getId());
		//根据请求中的phoneNum向数据库查询
		Agency uniAgency = agencyService.findUniqueByProperty("phone_num",phoneNum);
//		Users uniUser = usersService.findUniqueByProperty("phone_num", phoneNum);
		if(uniAgency != null) {
			System.out.println(uniAgency.toString());
			//若用户存在，则把请求中的密码和用户密码比较
			if(password.equals(uniAgency.getPassword()) || password == uniAgency.getPassword()) {
				// '1' 代表密码正确
				session.setAttribute("agencyId", uniAgency.getId());
				System.out.println("agencyId:" + uniAgency.getId());
				uniAgency.setPassword("");
				return new AppResponse<Agency>(1,"登录成功",uniAgency);
			}else {
				//2 密码错误
				return new AppResponse<Agency>(2,"密码错误",null);
			}
		}else {
			//3 用户不存在
			return new AppResponse<Agency>(3,"用户不存在",null);
		}
	}
	
	/**
	 * 验证手机号是否存在
	 * @param phoneNum
	 * @return '1' 用户不存在 ， '2' 用户已存在
	 */
	@RequestMapping(value="validatePhoneNum")
	@ResponseBody
	public AppResponse<String> validatePhoneNum(String phoneNum) {
		System.out.println("validate phoneNum-----"+phoneNum);
		//验证唯一性
		Users uniUser = usersService.findUniqueByProperty("phone_num", phoneNum);
		if(uniUser == null) {
			//用户不存在
			return new AppResponse<String>(1,"用户不存在",null);
		}else {
			//用户已存在
			return new AppResponse<String>(0,"用户已存在",null);
		}
	}
	
	/**
	 * 验证手机号是否存在
	 * @param phoneNum
	 * @return '1' 用户不存在 ， '2' 用户已存在
	 */
	@RequestMapping(value="validateAgencyPhoneNum")
	@ResponseBody
	public AppResponse<String> validateAgencyPhoneNum(String phoneNum) {
		System.out.println("validate phoneNum-----"+phoneNum);
		//验证唯一性
		Agency agency = agencyService.findUniqueByProperty("phone_num", phoneNum);
		if(agency == null) {
			//用户不存在
			return new AppResponse<String>(1,"用户不存在",null);
		}else {
			//用户已存在
			return new AppResponse<String>(0,"用户已存在",null);
		}
	}
	
	/**
	 * 发送验证码
	 * @param phoneNum 手机号
	 * @param request http请求
	 * @return '1' 已发送
	 */
	@RequestMapping(value="sendVerifyCode")
	@ResponseBody
	public AppResponse<String> sendVerifyCode(String phoneNum,HttpServletRequest request) {
		HttpSession session = request.getSession();
		 String code = MsgUtil.sendVerifyCode(phoneNum, request);
		//'1' 已发送
		System.out.println("session.getId():"+session.getId());
		return new AppResponse<String>(1,"已发送",code);
	}
	/**
	 * 用户注册
	 * @param users
	 * @param verifyCode 验证码
	 * @param req
	 * @return '0'注册失败， '1'注册成功， '2'验证码不正确
	 */
	@RequestMapping(value="register")
	@ResponseBody
	public AppResponse<String> register(Users users,String verifyCode,HttpServletRequest req,HttpServletResponse response) {
		System.out.println(users.toString());
		//验证唯一性
		HttpSession session =req. getSession();
		System.out.println("****register sessionid"+session.getId());
		System.out.println(" request.getRequestedSessionId()"+ req.getRequestedSessionId());
		String code = (String) session.getAttribute("code");
		System.out.println("****code:"+code);
		System.out.println("verifyCode:"+verifyCode);
//		if(!verifyCode.equals(code)) {
//			//验证码不正确
//			return "2";
//		}
		Users uniUser = usersService.findUniqueByProperty("phone_num", users.getPhoneNum());
		if(uniUser == null) {
			//加密密码，并且存入数据库
			users.setPassword(SystemService.entryptPassword(users.getPassword()));
			users.setStatus(0);
			if(!verifyCode.equals(code)) {
				//验证码不正确
				return new AppResponse<String>(2,"验证码不正确",null);
			}
			users.setBalance(0.0);
			users.setExpireDate(new Date());
			usersService.save(users);
			//注册成功
			return new AppResponse<String>(1,"注册成功",null);
		}else {
			//注册失败
			return new AppResponse<String>(0,"注册失败",null);
		}
	}
	
	/**首页内容
	 * @return 从数据库获取index.jsp页面的内容，放入model
	 */
	@RequestMapping(value="index")
	@ResponseBody
	public AppResponse<Map<String, Object>> index(){
		//查询任务
				System.out.println("---------------");
				List<Tasks> tasks = tasksService.listTasks();
				for (Tasks tasks2 : tasks) {
					//修改图片路径以匹配前端
					tasks2.setIcon(tasks2.getIcon().substring(9, tasks2.getIcon().length()));
				}
				//任务统计
				List<TasksStat> stat = tasksStatService.selectStat();
				//今日已完成
				Integer todayFinished = itemService.selectTodayFinished();
				todayFinished = todayFinished == null?0:todayFinished;
				//今日奖励
				Integer todayReward = itemService.selectTodayReward();
				todayReward = todayReward == null?0:todayReward;

				Map<String, Object> map = new HashMap<>();
				map.put("tasks", tasks);
				map.put("stat", stat);
				map.put("todayFinished",todayFinished);
				map.put("todayReward",todayReward);
				System.out.println("*****todayReward"+todayReward);
				return new AppResponse<>(1,"任务列表",map);
	}
	
	
	@RequestMapping(value="logout")
	@ResponseBody
	public String logout(HttpServletRequest req) {
		
		//移除用户id
		System.out.println("-----移除session");
		HttpSession session = req.getSession();
		session.removeAttribute("userId");
		session.removeAttribute("usersStatus");
		System.out.println("-----移除session成功");
		return "1";
	}
	/**
	 * 任务详情
	 * @param taskId 任务id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="taskDetail")
	@ResponseBody
	public AppResponse<Map<String, Object>> getTasks(String taskId,HttpServletRequest request){
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
//		Tasks tasks = tasksService.findUniqueByProperty("id", taskId);
//		if(tasks!=null) {
//			tasks.setIcon(tasks.getIcon().substring(9, tasks.getIcon().length()));
//			System.out.println(tasks.toString());
//		}
		
		Tasks t = new Tasks();
		//查询listTasks
		List<Tasks> tss = tasksService.findList(t);
//		System.out.println("tss"+tss.size() + "***taskid"+ tasksid);
		//将要返回的tasks
		Tasks uniTasks = null;
		//若查询不为空
		if(tss.size() >= 1) {
			//遍历，并且将id=tasksid的对象给unitasks
			for (Tasks tasks : tss) {
				System.out.println("***tasks:"+ tasks.toString());
				if (tasks.getId().equals(taskId)) {
					tasks.setIcon(tasks.getIcon().substring(9, tasks.getIcon().length()));
					uniTasks = tasks;
					System.out.println("-----tasks:"+ tasks.toString());
				}
			}
		}
		
		
		//返回内容
		Map<String , Object> content = new HashMap<>();
		if(uniTasks == null) {
			return new AppResponse<>(0,"系统错误，请返回首页",null);
		}
		/*
		 * 查询UsersTasksItem,返回state
		 */
		List<UsersTasksItem> items = itemService.selectByUsersIdAndTasksId(userId, taskId);
		if(items.size() >= 1) {
			System.out.println("*****items.size():"+items.size());
			for (UsersTasksItem item : items) {
				if(item.getUsers().getId().equals(userId) && item.getTasks().getId().equals(taskId)) {
					System.out.println(item.toString());
					content.put("userTaskItemId", item.getId());
					content.put("userTaskItemState", item.getState());
					content.put("userTaskItemCommit", item.getRemarks());
					System.out.println("item.getRemarks:"+item.getRemarks());
				}
			}
		}else {
			content.put("userTaskItemState", '0');
		}
		content.put("task", uniTasks);
		return new AppResponse<>(1,"任务详情",content);
	}
	/**
	 * 抢任务
	 * @param taskId 任务id
	 * @param request
	 * @return 
	 */
	@RequestMapping(value="addUsersTasksItem")
	@ResponseBody
	public AppResponse<Object> addUsersTasksItem(String taskId ,String userId,HttpServletRequest request){
//		HttpSession session = request.getSession();
//		String userId = (String) session.getAttribute("userId");
//		System.out.println(session.getId());
		Users users = new Users();
		Tasks tasks = new Tasks();
		tasks.setId(taskId);
		users.setId(userId);
		//设置item的各个属性
		UsersTasksItem item = new UsersTasksItem();
		item.setUsers(users);
		item.setTasks(tasks);
		item.setPicture("未提交");
		item.setRemarks("未提交");
		item.setState("1");
		itemService.save(item);
		item.setUsers(null);
		item.setTasks(null);
		//返回item的状态
		return new AppResponse<>(1,"抢任务成功",item);
	}
	
	/**
	 * 提交图片
	 * @param request
	 * @return 图片路径
	 * @throws IOException
	 */
	@RequestMapping(value = "getImg")
	@ResponseBody
	public String getImg(MultipartHttpServletRequest request,HttpServletRequest req) throws IOException {
	          Iterator<String> itr = request.getFileNames(); 
	          System.out.println("-------getImg");
	          MultipartFile mpf = null;  
	          String relativePath = "";
	          while (itr.hasNext()) {    
	               mpf = request.getFile(itr.next());
	                    try {    
	                         if(mpf.getSize()<104857600){  
	                             // String attachmentName = StringUtils.substringBeforeLast(mpf.getOriginalFilename(), ".");         
	                              String ext = StringUtils.substringAfterLast(mpf.getOriginalFilename(), "."); 
	                              //需要在配置文件（sysConfig.properties）里面加上    根路径fileUploadPath=F:\\
	                              //String rootPath = ResourceUtil.getConfigByName("fileUploadPath"); 
	                              String rootPath = "C://apache-tomcat-7.0.73/webapps/shiwan"; 
	                              String filePath = "userfiles/commitImgupload/leave";  
	                              String fileName = UUID.randomUUID()+"."+ext;  
	                              relativePath = filePath+"/"+fileName; 
	                              File realPath = new File(rootPath+"/"+filePath); 
	                              if(!realPath.exists()){    
	                                   //建立多级文件夹
	                                   realPath.mkdirs();     
	                              }  
	                              FileCopyUtils.copy(mpf.getBytes(),new File(realPath.getAbsolutePath()+"\\"+fileName));    
	                         }else{     
	                         } 
	                    } catch (Exception e) {     
	                    }
	          }
	    System.out.println("relativePath:------"+relativePath);
		return relativePath;
	}
	/**
	 * 
	 * @param commitContent 
	 * @param usersTasksId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="commitTask")
	@ResponseBody
	public String commitTask(String commitContent,String userTaskItemId,String imgUrl,String userId,HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(userId == null) {
			return "0";
		}
		//拼接图片路径 以便jeeplus后台查看
		if(imgUrl != null) {
			System.out.println("imgUrl.length():"+imgUrl.length());
			String url = "|/shiwan/";
			imgUrl = url+imgUrl;
		}else {
			imgUrl="无";
		}
		System.out.println("---imgUrl:"+imgUrl);
		System.out.println("********"+userTaskItemId);
		itemService.updateUsersTasksItem(imgUrl, commitContent, userTaskItemId,"2");
		return "1";
	}
	
	/**
	 * 会员提现接口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="withdraw")
	@ResponseBody
	public AppResponse<Map<String , Object>> withdraw(HttpServletRequest request,String userId) {
		//获取session中的用户id，判断用户是否登录
		HttpSession session = request.getSession();
//		String usersId = (String) session.getAttribute("userId");
		//查询user
		Users users = usersService.getById(userId);
		//创建一个map充当容器
		Map<String, Object> map = new HashMap<>();
		if(users != null) {
			//用户登录则把用户的信息返回
			session.setAttribute("usersStatus", users.getStatus());
//			String alipayAccount = users.getAlipayAccount();
//			String alipayName = users.getAlipayName();
//			map.put("alipayAccount", alipayAccount);
//			map.put("alipayName", alipayName);
			Integer userStatus = users.getStatus();
			Double balance	= users.getBalance();
			String agencyName = users.getAgency().getName();
			map.put("userStatus", userStatus);
			map.put("agencyName", agencyName);
			map.put("balance", balance);
		}else {
			return new AppResponse<>(0,"error",null);
		}
		return new AppResponse<>(1,"success",map);
	}
	
	/**
	 * 代理提现接口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="agencyWithdraw")
	@ResponseBody
	public AppResponse<Map<String , Object>> agencyWithdraw(HttpServletRequest request,String agencyId) {
		//获取session中的用户id，判断用户是否登录
		HttpSession session = request.getSession();
//		String usersId = (String) session.getAttribute("userId");
		//查询user
//		Users users = usersService.findUniqueByProperty("id", usersId);
		Agency agency = agencyService.findUniqueByProperty("id", agencyId);
		//创建一个map充当容器
		Map<String, Object> map = new HashMap<>();
		if(agency != null) {
			//用户登录则把用户的信息返回
//			session.setAttribute("usersStatus", users.getStatus());
//			Integer userStatus = users.getStatus();
			String alipayAccount = agency.getAlipayAccount();
			String alipayName = agency.getAlipayName();
//			String alipayName = users.getAlipayName();
			Double balance	= agency.getBalance();
//			map.put("userStatus", userStatus);
			map.put("alipayAccount", alipayAccount);
			map.put("alipayName", alipayName);
			map.put("balance", balance);
		}else {
			return new AppResponse<>(0,"error",null);
		}
		return new AppResponse<>(1,"success",map);
	}
	
	/**
	 * 用户 提交提现申请
	 * @param withdrawNum 提现数目
	 * @param remarks 提现备注
	 * @param request
	 * @return
	 */
	@RequestMapping(value="addWithdraw")
	@ResponseBody
	public String addWithdraw(Double withdrawNum,String remarks,String userId,HttpServletRequest request) {
//		HttpSession session = request.getSession();
//		String userId = (String) session.getAttribute("userId");
		System.out.println("*****withdrawNum:"+withdrawNum+"remarks:"+remarks+"userId"+userId+"******");
		Users users = usersService.findUniqueByProperty("id", userId);
		if(users == null) {
			return "0";
		}
		Withdraw withdraw = new Withdraw();
		withdraw.setUsers(users);
		withdraw.setSum(withdrawNum);
		withdraw.setRemarks(remarks);
		withdraw.setState("1");
		withdrawService.save(withdraw);
		return "1";
	}
	
	/**
	 * 代理 提交提现申请
	 * @param withdrawNum 提现数目
	 * @param remarks 提现备注
	 * @param request
	 * @return
	 */
	@RequestMapping(value="addAgencyWithdraw")
	@ResponseBody
	public String addAgencyWithdraw(Double withdrawNum,String remarks,String agencyId,HttpServletRequest request) {
//		HttpSession session = request.getSession();
//		String userId = (String) session.getAttribute("userId");
		System.out.println("*****withdrawNum:"+withdrawNum+"remarks:"+remarks+"agencyId"+agencyId+"******");
//		Users users = usersService.findUniqueByProperty("id", AgencyId);
		Agency agency = agencyService.findUniqueByProperty("id", agencyId);
		if(agency == null) {
			return "0";
		}
		AgencyWithdraw withdraw = new AgencyWithdraw();
		withdraw.setAgency(agency);
		withdraw.setSum(withdrawNum);
		withdraw.setRemarks(remarks);
		withdraw.setState("1");
		agencyWithdrawService.save(withdraw);
		return "1";
	}
	/**
	 * 用户提现页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="withdrawList")
	@ResponseBody
	public AppResponse<List<Withdraw>> withdrawList(HttpServletRequest request,String userId) {
		//获取session中的用户id，判断用户是否登录
		HttpSession session = request.getSession();
//		String usersId = (String) session.getAttribute("userId");
		//查询user
		
		Users users = usersService.findUniqueByProperty("id", userId);
		if(users != null) {
			//
			List<Withdraw> withdraws = withdrawService.listWithdrawByUserId(userId);
			session.setAttribute("usersStatus", users.getStatus());
			return  new AppResponse<>(1,"查询到"+withdraws.size()+"条记录",withdraws);
		}else {
			return  new AppResponse<>(1,"用户未登录",null);
		}
	}
	
	/**
	 * 用户提现页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="agencyWithdrawList")
	@ResponseBody
	public AppResponse<List<AgencyWithdraw>> agencyWithdrawList(HttpServletRequest request,String agencyId) {
		//获取session中的用户id，判断用户是否登录
		HttpSession session = request.getSession();
//		String usersId = (String) session.getAttribute("userId");
		//查询user
		Agency agency = agencyService.findUniqueByProperty("id", agencyId);
//		Users users = usersService.findUniqueByProperty("id", usersId);
		if(agency != null) {
			//
//			List<Withdraw> withdraws = withdrawService.listWithdrawByUserId(usersId);
			Agency find = new Agency();
			AgencyWithdraw aw = new AgencyWithdraw();
			aw.setAgency(find);
			find.setId(agencyId);
			List<AgencyWithdraw> agencyWithdraws = agencyWithdrawService.findList(aw);
//			session.setAttribute("usersStatus", users.getStatus());
			return  new AppResponse<>(1,"查询到"+agencyWithdraws.size()+"条记录",agencyWithdraws);
		}else {
			return  new AppResponse<>(0,"用户未登录",null);
		}
	}
	/**
	 * 验证密码
	 * @param password
	 * @param request
	 * @return
	 */
	@RequestMapping(value="validatePassword")
	@ResponseBody
	public AppResponse<Object> validatePassword(String password,HttpServletRequest request) {
		System.out.println("-----validatePassword password:"+password+"-----");
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		Users users = usersService.findUniqueByProperty("id", userId);
		if(users != null) {
			if(SystemService.validatePassword(password, users.getPassword())) {
				//密码正确
				return new AppResponse<>(1,"密码正确",null);
			}else {
				//密码错误
				return new AppResponse<>(2,"密码错误",null);
			}
		}else {
			//用户不存在
			return new AppResponse<>(0,"用户不存在",null);
		}
	}
	/**
	 * 修改密码
	 * @param password
	 * @param request
	 * @return
	 */
	@RequestMapping(value="updatePassword")
	@ResponseBody
	public AppResponse<Object> updatePassword(String password,HttpServletRequest request) {
		System.out.println("-----updatePassword password:"+password);
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		Users users = usersService.findUniqueByProperty("id", userId);
		if(users != null) {
			users.setPassword(SystemService.entryptPassword(password));
			usersService.save(users);
			//修改成功
			return new AppResponse<>(1,"修改成功",null);
		}else {
			//用户不存在
			return new AppResponse<>(0,"用户不存在",null);
		}
	}
	
	/**
	 * 用户找回密码
	 * @param phoneNum
	 * @param password
	 * @param request
	 * @return
	 */
	@RequestMapping(value="forgotPassword")
	@ResponseBody
	public AppResponse<Object> forgotPassword(String phoneNum,String password,String verifyCode,HttpServletRequest request) {
		HttpSession session = request.getSession();
		String verifyPhoneNum = (String) session.getAttribute("verifyPhoneNum");
		String rightVerifyCode = (String) session.getAttribute("code");
		System.out.println("-----verifyPhoneNum:"+verifyPhoneNum);
		System.out.println("-----rightVerifyCode:"+rightVerifyCode);
		System.out.println("-----verifyCode:"+verifyCode);
		System.out.println("-----phoneNum:"+phoneNum);
		if(!phoneNum.equals(verifyPhoneNum)) {
			//手机号和发送验证码的手机号不一致
			return new AppResponse<>(0,"手机号和发送验证码的手机号不一致",null);
		}
		Users users = usersService.findUniqueByProperty("phone_num", phoneNum);
		if(users != null) {
			//更新密码
			if(rightVerifyCode.equals(verifyCode)) {
				users.setPassword(SystemService.entryptPassword(password));
				usersService.save(users);
				//更新成功
				return new AppResponse<>(1,"更新成功",null);
			}else {
				//验证码错误
				return new AppResponse<>(2,"验证码错误",null);
			}
		}else {
			//用户不存在
			return new AppResponse<>(3,"用户不存在",null);
		}
	}
	
	/**
	 * 账户余额页查询所有已通过审核的usersTasksItem
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="selectPassedTasks")
	@ResponseBody
	public AppResponse<List<UsersTasksItem>> selectPassedTasks(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		if(userId == "" || userId == "null") {
			return new AppResponse<>(2,"用户未登录",null);
		}
		List<UsersTasksItem> items = itemService.selectPassed(userId);
		if(items.size() >= 1 ) {
			return new AppResponse<>(1,"查询到"+items.size()+"项",items);
		}
		return new AppResponse<>(0,"查询到0项",null);
	}
	
	/**
	 * 个人中心 通知
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="selectPassedAndNot")
	@ResponseBody
	public AppResponse<List<UsersTasksItem>> selectPassedAndNot(HttpServletRequest request,Model model) {
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		if(userId == "" || userId == "null") {
			return new AppResponse<>(2,"用户未登录",null);
		}
		List<UsersTasksItem> items = itemService.selectPassedAndNot(userId);
		if(items.size() >= 1 ) {
			return new AppResponse<>(1,"查询到"+items.size()+"项",items);
		}
		return new AppResponse<>(0,"查询到0项",null);
	}
	
	/**
	 * 个人中心 查看任务
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="taskView")
	@ResponseBody
	public AppResponse<List<UsersTasksItem>>  getUsersTasksItemByUserId(HttpServletRequest request,Model model) {
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		if(userId == "" || userId == "null") {
			return new AppResponse<>(2,"用户未登录",null);
		}
		List<UsersTasksItem> items= itemService.selectByUsersId(userId);
		if(items.size() >= 1 ) {
			for (UsersTasksItem item : items) {
				System.out.println(item.getTasks().toString());
			}
			return new AppResponse<>(1,"查询到"+items.size()+"项",items);
		}
		return new AppResponse<>(0,"查询到0项",null);
	}
	
	/**
	 * 商城
	 * @param model
	 * @return
	 */
	@RequestMapping(value="shop")
	@ResponseBody
	public AppResponse<List<Shop>> shop() {
		Shop shop = new Shop();
		List<Shop> shops = shopService.findList(shop);
		for (Shop s : shops) {
			s.setPicture(s.getPicture().substring(9,s.getPicture().length()));
			System.out.println(s.toString());
		}
		
		return new AppResponse<>(1, "查询到"+shops.size()+"项商品", shops);
	}
	
	@RequestMapping(value="shopDetails")
	@ResponseBody
	public AppResponse<Shop> shopDetails(String id){
		List<Shop> shops = shopService.findList(new Shop());
		Shop shop = null;
		//查询出来0条，返回空
		if(shops.size() == 0) {
			return new AppResponse<>(0,"查询到0条数据",null);
		}
		for (Shop s : shops) {
			if(id.equals(s.getId())) {
				shop = s;
			}
		}
		if(shop != null) {
			shop.setPicture(shop.getPicture().substring(9, shop.getPicture().length()));
			return new AppResponse<>(1,"success",shop);
		}
		return new AppResponse<>(2,"查询不到商品",null);
	}
	/**
	 * 公告
	 * @return
	 */
	@RequestMapping("getNotice")
	@ResponseBody
	public AppResponse<List<Notice>> getNotice(){
		List<Notice> notices = noticeService.getNotice();
		for (Notice notice : notices) {
			notice.setPicture(notice.getPicture().substring(9, notice.getPicture().length()));
		}
		return new AppResponse<>(1,"查询到了"+notices.size()+"条通告",notices);
	}
	/**
	 * 通告
	 * @return
	 */
	@RequestMapping("getInform")
	@ResponseBody
	public AppResponse<List<Inform>> getInform(){
		List<Inform> informs = informService.getInform();
		return new AppResponse<>(1,"查询到了"+informs.size()+"条通告",informs);
	}
	
	/**
	 * 获取到余额最高的20个用户（财富榜）
	 * @return
	 */
	@RequestMapping(value = "getRichList")
	@ResponseBody
	public AppResponse<List<Users>> getRichList(){
		List<Users> users = usersService.getRichList();

		if(users.size() > 0){
			return new AppResponse<>(1,"success",users);
		}else {
			return new AppResponse<>(0,"fail",null);
		}
	}
	@RequestMapping(value = "todayFinished")
	@ResponseBody
	public int todayFinished(){
		return  itemService.selectTodayFinished();
	}
	/**
	 * 个人中心公告列表
	 * @return
	 */
	@RequestMapping(value="getNoticepersonal")
	@ResponseBody
	public AppResponse<Object> getNoticepersonal(){
		List<Noticepersonal> noticepersonals = noticepersonalService.findList(new Noticepersonal());
		/*if(noticepersonals.size() > 0) {
			Collections.reverse(noticepersonals);
		}*/
		if(noticepersonals.size() == 0) {
			return new AppResponse<>(0,"fail",null);
		}
		for (Noticepersonal noticepersonal : noticepersonals) {
			String picture = noticepersonal.getPicture();
			if(picture.length()>0) {
				noticepersonal.setPicture(picture.substring(9, picture.length()));
			}
		}
		return new AppResponse<>(1,"success",noticepersonals);
	}
	/**
	 * 个人中心公告详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getNoticepersonalDetails")
	@ResponseBody
	public AppResponse<Object> getNoticepersonalDetails(String id){
		Noticepersonal noticepersonal = null;
		List<Noticepersonal> ns = noticepersonalService.findList(new Noticepersonal());
		for (Noticepersonal n : ns) {
			if(id.equals(n.getId())) {
				noticepersonal = n;
			}
		}
		String picture = noticepersonal.getPicture();
		if(picture.length()>0) {
			noticepersonal.setPicture(picture.substring(9, picture.length()));
		}
		if(noticepersonal != null) {
			System.out.println("***********"+noticepersonal.getContent());
			return new AppResponse<>(1,"success",noticepersonal);
		}else {
			return new AppResponse<>(0,"fail",null);
		}
	}
	/**
	 * 个人中心关于我们
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getAboutus")
	@ResponseBody
	public AppResponse<Object> getAboutus(){
		List<Aboutus> abs = aboutusService.findList(new Aboutus());
		if(abs.size() == 0) {
			return new AppResponse<>(0,"fail",null);
		}
		Aboutus aboutus = abs.get(0);
		return new AppResponse<>(1,"success",aboutus);
	}
	
	/**
	 * 新闻列表
	 * @return
	 */
	@RequestMapping(value="getNews")
	@ResponseBody
	public AppResponse<Object> getNews(){
		AppResponse<Object> response = new AppResponse<>();
		List<News> news = newsService.findList(new News());
		System.out.println("news.size()"+news.size());
		if(news.size() > 0) {
			response.setData(news);
			response.setMsg("查询到了"+news.size()+"条数据");
			response.setState(1);
		}else {
			response.setData(null);
			response.setMsg("查询到0条数据");
			response.setState(0);
			
		}
		return response;
	}
	
	/**
	 * 新闻详情
	 * @param newsId
	 * @return
	 */
	@RequestMapping(value="getNewsDetails")
	@ResponseBody
	public AppResponse<Object> getNewsDetails(String newsId){
		AppResponse<Object> response = new AppResponse<>();
		News news = new News();
		news.setId(newsId);
		List<News> listNews = newsService.findList(news);
		System.out.println("listNews.size()"+listNews.size());
		if(listNews.size() > 0) {
			response.setData(listNews.get(0));
			response.setMsg("success");
			response.setState(1);
		}else {
			response.setData(null);
			response.setMsg("fail");
			response.setState(0);
		}
		
		return response;
	}
}
