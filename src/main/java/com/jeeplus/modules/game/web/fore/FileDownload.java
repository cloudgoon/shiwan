package com.jeeplus.modules.game.web.fore;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="download")
public class FileDownload {
	/**
	 * 下载享玩游安装包xiangwanyou.apk
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="apk")
	public void download(HttpServletRequest request,HttpServletResponse response)throws IOException {
		//apk所在文件目录
		String path = request.getSession().getServletContext().getRealPath("/userfiles/download/xiangwanyou.apk");
		System.out.println(path);
		File file = new File(path);
		//获取文件的名字
		String fileName = file.getName();
		System.out.println(fileName);
		//在浏览器的下载提示框上显示文件的名字
		response.addHeader("Content-Disposition", "attachment,filename="+java.net.URLEncoder.encode(fileName, "utf-8"));
		//将文件以输出流的方式传输至客户端
		FileUtils.copyFile(file, response.getOutputStream());
	}
}
