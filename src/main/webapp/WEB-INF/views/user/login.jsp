<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>Login</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<style type="text/css">
*[id$=errors] {
	color: red;
	font-style: italic;
	display: block;
	margin: 5px;
	font-size: 12px;
	flex: 1;
	text-align: center;
}
span#message.errors {
	
}
a.logoHome {
    max-width: 100px;
    height: 100px;
    display: block;
    margin: auto;
}
img.img-responsive {
    width: 100%;
    height: auto;
}
.iconLogo {
    border-radius: 100%;
    width: 100%!important;
    height: 100%!important;
}
</style>

</head>
<body>
	<%@include file="/WEB-INF/views/user/header.jsp"%>

	<div class="limiter">
		<div class="container-login100"
			style="background-image: url('resources/login/images/bg-01.jpg');">
			<div class="wrap-login100 p-l-55 p-r-55 p-t-65 p-b-54">
				<form:form class="login100-form validate-form" action="user/login.htm" modelAttribute="user">
					<a href="page/home.htm" class="logoHome"><img alt="resources/page/images/logo1.PNG" src="resources/page/image/logo1.PNG" class="img-responsive iconLogo" ></a>
					<span class="login100-form-title p-b-49 p-t-15"> Login </span>

					<div class="wrap-input100  m-b-23"
						data-validate="Username is reauired">
						<span class="label-input100">Username</span>
						<form:input path="user_name" class="input100" type="text"
							name="username" placeholder="Type your username" />
						<span class="focus-input100" data-symbol="&#xf206;"></span>
					</div>
					<form:errors path="user_name"></form:errors>
					<div class="wrap-input100 " data-validate="Password is required">
						<span class="label-input100">Password</span>
						<form:input path="password" class="input100" type="password"
							name="pass" placeholder="Type your password" />
						<span class="focus-input100" data-symbol="&#xf190;"></span>
					</div>
					<form:errors path="password"></form:errors>

					<div class="text-right p-t-8 p-b-31">
						<a href="user/forgotpassword.htm"> Forgot password? </a>
					</div>

					<div class="container-login100-form-btn">
						<div class="wrap-login100-form-btn">
							<div class="login100-form-bgbtn"></div>
							<form:button class="login100-form-btn">
								Login
							</form:button>
						</div>
					</div>
					<span id="message.errors">${message}</span>
					<div class="txt1 text-center p-t-54 p-b-20">
						<span> Or Sign Up Using </span>
					</div>

					<div class="flex-c-m">
						<a href="#" class="login100-social-item bg1"> <i
							class="fa fa-facebook"></i>
						</a> <a href="#" class="login100-social-item bg2"> <i
							class="fa fa-twitter"></i>
						</a> <a href="#" class="login100-social-item bg3"> <i
							class="fa fa-google"></i>
						</a>
					</div>

					<div class="flex-col-c p-t-155">
						<a href="user/register.htm" class="txt2"> Sign Upp </a>
					</div>
				</form:form>
			</div>
		</div>
	</div>


	<div id="dropDownSelect1"></div>

	<!--===============================================================================================-->


</body>
</html>
