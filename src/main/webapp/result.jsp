<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String queryBack = (String) request.getAttribute("queryBack");
    ArrayList<Map<String, Object>> newslist = (ArrayList<Map<String, Object>>) request.getAttribute("newslist");
    String totalHits = (String) request.getAttribute("totalHits");
    String totalTime = (String) request.getAttribute("totalTime");
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
    int pages = Integer.parseInt(totalHits) / 10 + 1;
    //pages = pages > 10 ? 10 : pages;
%>
<html>
<head>
    <title>搜索结果</title>
    <link type="text/css" rel="stylesheet" href="css/result.css">
</head>
<body>
<div class="result_search">
    <div class="logo">
        <h2><a href="index.jsp">ElasticSearch</a></h2>
    </div>
    <div class="searchbox">
        <form action="/SearchData" method="get">
            <input type="text" name="query" value="<%=queryBack%>">
            <input type="submit" value="搜索一下">
        </form>
    </div>
</div>
<h5 class="result_info">共搜索到<span><%=totalHits%></span>条结果,耗时<span> <%=Double.parseDouble(totalTime) / 1000.0 %></span>秒
</h5>
<div class="newslist">
    <%
        if (newslist.size() > 0) {
            Iterator<Map<String, Object>> iter = newslist.iterator();
            while (iter.hasNext()) {
                Map<String, Object> news = iter.next();
                String content = news.get("content").toString();
                content = content.length() > 200 ? content.substring(0, 200) : content;
                String publishtime = news.get("publish_time").toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long lt = new Long(publishtime);
                publishtime = simpleDateFormat.format(lt);
                //System.out.println(res + "====");
    %>
    <div class="news">
        <h4><a href="<%=news.get("url")%>"><%=news.get("search_word")%></a></h4>
        <p><%= publishtime%> &nbsp&nbsp<%=content%> ...</p>
    </div>
    <%
            }
        }
    %>
</div>
<div class="page">
    <ul>
        <%
            for (int i = 1; i <= pages; i++) {
        %>
        <li><a href="/SearchData?query=<%=queryBack%>&pageNum=<%=i%>&startDate=<%=startDate%>&endDate=<%=endDate%>"><%=i%></a></li>
        <%
            }
        %>

    </ul>
</div>
<div class="info">
    <p>ElasticSearchTest Powered By <b> chenlg</b></p>
    <p>@2018 All right reserved</p>
</div>
</body>
</html>
