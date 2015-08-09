<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!--meta name="viewport" content="width=device-width, initial-scale=1"--><!-- 禁用响应式布局 -->
    <%--<title>Stigmergic-Modeling - ${title}</title>--%>
    <!-- CSS -->
    <link href="/static/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/dist/css/font-awesome.min.css" rel="stylesheet">
    <link href="/static/dist/css/stigmod.css" rel="stylesheet">

    <!--[if lt IE 9]><script src="http:html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->

</head>
<body>

<!-- 顶部导航条 -->
<nav class="navbar navbar-default navbar-fixed-top stigmod-hide-when-full-screen" role="navigation" id="navbarTop">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="/"><img src="/static/dist/img/stigmod_logo_sm.png" alt="stigmod-logo-sm"/></a>
        </div>
        <div class="collapse navbar-collapse">
            <form class="navbar-form navbar-left" role="search">
                <div class="form-group">
                    <input type="text" class="twitter-typeahead form-control" placeholder="Search Stigmergic-Modeling" id="stigmod-navbar-input">
                </div>
            </form>
            <ul class="nav navbar-nav">
                <li><a href="/about">About</a></li>
                <li><a href="#">Help</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <%--<% if (!user) { %>--%>
                <%--<li><a href="/reg"><span class="glyphicon glyphicon-flash"></span> Sign up</a></li>--%>
                <%--<li><a href="/login"><span class="glyphicon glyphicon-log-in"></span> &nbsp;Sign in</a></li>--%>
                <%--<% } else { %>--%>
                <%--<li><a href="/u"><span class="glyphicon glyphicon-user"></span> <%= user.mail %></a></li>--%>
                <%--<li><a href="/u/settings/profile"><span class="glyphicon glyphicon-cog"></span> Settings</a></li>--%>
                <%--<li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Sign out</a></li>--%>
                <%--<% } %>--%>
            </ul>
        </div>
    </div>
</nav>

<!-- 主容器 -->
<div class="container">
    <!-- 信息通知栏 -->
    <%--<% if (success) { %>--%>
    <%--<div class="alert alert-success alert-dismissible">--%>
        <%--<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>--%>
        <%--<%= success %>--%>
    <%--</div>--%>
    <%--<% } %>--%>
    <%--<% if (error) { %>--%>
    <%--<div class="alert alert-danger alert-dismissible">--%>
        <%--<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>--%>
        <%--<%= error %>--%>
    <%--</div>--%>
    <%--<% } %>--%>
    <!-- 内容 -->

