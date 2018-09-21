<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>个人中心公告管理</title>
	<meta name="decorator" content="ani"/>
	<!-- SUMMERNOTE -->
	<%@include file="/webpage/include/summernote.jsp" %>
	<script type="text/javascript">
		var validateForm;
		var $table; // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		function doSubmit(table, index){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		  if(validateForm.form()){
			  $table = table;
			  $topIndex = index;
			  jp.loading();
					$("input[name='content']").val($('#content').summernote('code'));//取富文本的值
			  $("#inputForm").submit();
			  return true;
		  }

		  return false;
		}

		$(document).ready(function() {
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/game/admin/noticepersonal/save",$('#inputForm').serialize(),function(data){
						if(data.success){
	                    	$table.bootstrapTable('refresh');
	                    	jp.success(data.msg);
	                    	jp.close($topIndex);//关闭dialog

	                    }else{
            	  			jp.error(data.msg);
	                    }
					})
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
				//富文本初始化
			$('#content').summernote({
				height: 300,
                lang: 'zh-CN'
            });
		});
	</script>
</head>
<body class="bg-white">
		<form:form id="inputForm" modelAttribute="noticepersonal" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">标题：</label></td>
					<td class="width-35">
						<form:input path="title" htmlEscape="false"    class="form-control "/>
					</td>
					<td class="width-15 active"><label class="pull-right">标题图片：</label></td>
					<td class="width-35">
						<form:hidden id="picture" path="picture" htmlEscape="false" maxlength="255" class="form-control"/>
						<sys:ckfinder input="picture" type="files" uploadPath="/game/admin/noticepersonal" selectMultiple="true"/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">内容：</label></td>
					<td class="width-85">
                        <%-- <input type="hidden" name="content"/>
						<div id="content">
                          ${fns:unescapeHtml(noticepersonal.content)}
                        </div> --%>
						<textarea name="content" id="editor" cols="200" value="noticepersonal.content"></textarea>
						<script type="text/javascript" src="/shiwan/static/plugin/ueditor/ueditor.config.js"></script>
						<script type="text/javascript" src="/shiwan/static/plugin/ueditor/ueditor.all.js"></script>
						<script type="text/javascript">
							var ue = UE.getEditor('editor');
						</script>
					</td>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
					<td class="width-35">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>