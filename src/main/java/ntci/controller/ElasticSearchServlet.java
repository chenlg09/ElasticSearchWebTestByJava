package ntci.controller;

import ntci.elastic.EsUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author: chenlg
 * @date: 2018/11/13
 * @time: 20:27
 */

@WebServlet(name="/SearchData",urlPatterns = "/SearchData")
public class ElasticSearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //System.out.println("=================");
        req.setCharacterEncoding("UTF-8");
        String query = req.getParameter("query");

        String pageNumStr=req.getParameter("pageNum");

        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");

        System.out.println(query);
        System.out.println(pageNumStr);
        System.out.println(startDate);
        System.out.println(endDate);

        int pageNum=1;

        if (pageNumStr!=null&&Integer.parseInt(pageNumStr)>1){
            pageNum=Integer.parseInt(pageNumStr);
        }

        searchOsnData(query, startDate,endDate,pageNum,req);

        req.setAttribute("queryBack", query);
        req.getRequestDispatcher("result.jsp").forward(req, resp);
    }

    private void searchOsnData(String query,String startDate,String endDate,int pageNum,HttpServletRequest req){
        System.out.println("-------------------");
        long start = System.currentTimeMillis();
        TransportClient client = EsUtils.getSingleClient();

        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders
                .multiMatchQuery(query, "search_word", "content");


        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .preTags("<span style=\"color:red\">")
                .postTags("</span>")
                .field("search_word")
                .field("content");

        SearchResponse searchResponse;


        if(startDate!=null && endDate!=null && !startDate.equals("") && !endDate.equals("")){
            searchResponse = client.prepareSearch("xmt-index","test")
                    .setTypes("osn_search_data")
                    .setQuery(multiMatchQuery)
                    .highlighter(highlightBuilder)
                    .setPostFilter(QueryBuilders.rangeQuery("publish_time").from(startDate).format("yyyy-MM-dd").to(endDate).format("yyyy-MM-dd"))
                    .setFrom((pageNum-1)*10)
                    .setSize(10)
                    .execute()
                    .actionGet();
        }else{
            searchResponse = client.prepareSearch("xmt-index")
                    .setTypes("osn_search_data")
                    .setQuery(multiMatchQuery)
                    .highlighter(highlightBuilder)
                    .setFrom((pageNum-1)*10)
                    .setSize(10)
                    .execute()
                    .actionGet();
        }


        SearchHits hits = searchResponse.getHits();
        ArrayList<Map<String, Object>> newslist = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : hits) {
            Map<String, Object> news = hit.getSourceAsMap();

            HighlightField hTitle = hit.getHighlightFields().get("search_word");
            if (hTitle != null) {
                Text[] fragments = hTitle.fragments();
                String hTitleStr = "";
                for (Text text : fragments) {
                    hTitleStr += text;
                }
                news.put("search_word", hTitleStr);
            }

            HighlightField hContent = hit.getHighlightFields().get("content");
            if (hContent != null) {
                Text[] fragments = hContent.fragments();
                String hContentStr = "";
                for (Text text : fragments) {
                    hContentStr += text;
                }
                news.put("content", hContentStr);
            }
            newslist.add(news);
        }
        long end = System.currentTimeMillis();
        req.setAttribute("newslist", newslist);
        req.setAttribute("totalHits", hits.getTotalHits() + "");
        req.setAttribute("totalTime", (end - start) + "");
        req.setAttribute("startDate",startDate);
        req.setAttribute("endDate",endDate);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
