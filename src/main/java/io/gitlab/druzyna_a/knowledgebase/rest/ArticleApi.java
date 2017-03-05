package io.gitlab.druzyna_a.knowledgebase.rest;

import io.gitlab.druzyna_a.knowledgebase.model.Article;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Damian Terlecki
 */
@Api(value = "/article", tags = {"article"}, description = "Articles about fishing from external resources")
@RequestMapping("/article")
public interface ArticleApi {

    @ApiOperation(httpMethod = "GET", value = "Fetch articles by title, description and/or author")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of found articles", response = Article.class, responseContainer = "List")
        ,
        @ApiResponse(code = 404, message = "Articles not found", response = Void.class)})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<List<Article>> fetchArticles(@ApiParam(value = "Regex for searching by title") @RequestParam(required = false) String titleRegex,
            @ApiParam(value = "Regex for searching by description") @RequestParam(required = false) String descriptionRegex,
            @ApiParam(value = "Regex for searching by author") @RequestParam(required = false) String authorRegex);
}
