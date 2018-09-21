<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
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
					$("input[name='description']").val($('#description').summernote('code'));//取富文本的值
			  $("#inputForm").submit();
			  return true;
		  }

		  return false;
		}

		$(document).ready(function() {
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/game/admin/shop/save",$('#inputForm').serialize(),function(data){
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
			$('#description').summernote({
				height: 300,
                lang: 'zh-CN'
            });
		});
	</script>
</head>
<body class="bg-white">
		<form:form id="inputForm" modelAttribute="shop" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>商品名：</label></td>
					<td class="width-35">
						<form:input path="name" htmlEscape="false" maxlength="64"    class="form-control required"/>
					</td>
					<td class="width-15 active"><label class="pull-right">商品描述：</label></td>
					<td class="width-35">
                        <input type="hidden" name="description"/>
						<div id="description">
                          ${fns:unescapeHtml(shop.description)}
                        </div>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">商品图片：</label></td>
					<td class="width-35">
						<form:hidden id="picture" path="picture" htmlEscape="false" maxlength="256" class="form-control"/>
						<sys:ckfinder input="picture" type="files" uploadPath="/game/admin/shop" selectMultiple="true"/>
					</td>
					<td class="width-15 active"><label class="pull-right">热度：</label></td>
					<td class="width-35">
						<form:input path="hot" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
					<td class="width-35">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
					<td class="width-15 active"></td>
		   			<td class="width-35" ></td>
		  		</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>