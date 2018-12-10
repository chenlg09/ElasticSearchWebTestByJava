<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ElasticSearch</title>
    <link type="text/css" rel="stylesheet" href="css/index.css">
</head>
<body>
<div class="box">
    <h1>Elasticsearch</h1>
    <div class="searchbox">
        <form action="/SearchData" method="get">
            <input type="text" name="query">
            <input type="submit" value="搜索一下">
            <h4>时间过滤：</h4><p>
            <h5>起始时间：<input type="date" name="startDate">
            结束时间：<input type="date" name="endDate"></h5>
        </form>
    </div>
</div>
</body>
</html>
