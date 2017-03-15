package io.gitlab.druzyna_a.knowledgebase.rest.offered;

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

    @ApiOperation(httpMethod = "GET", value = "Fetch articles by assigned request id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of found articles", response = Article.class, responseContainer = "List")
        ,
        @ApiResponse(code = 202, message = "Searching for articles...", response = Void.class)
        ,
        @ApiResponse(code = 404, message = "Articles not found", response = Void.class)})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<List<Article>> fetchArticles(@ApiParam(value = "Id of articles request") @RequestParam Long id);

    @ApiOperation(httpMethod = "GET", value = "Request articles by title, description and/or author. The request will queued and associated id will be returned. Refer to other resoruce for articles fetching.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Request id", response = Long.class)})
    @RequestMapping(path = "queue", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<Long> requestArticles(@ApiParam(value = "Regex for searching by title") @RequestParam(required = false) String titleRegex,
            @ApiParam(value = "Regex for searching by description") @RequestParam(required = false) String descriptionRegex,
            @ApiParam(value = "Regex for searching by author") @RequestParam(required = false) String authorRegex);

    @ApiOperation(httpMethod = "GET", value = "Request rod tags that could be used when requesting articles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of tags", response = String.class, responseContainer = "List")})
    @RequestMapping(path = "tags/rod", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<List<String>> requestRodTags();

    @ApiOperation(httpMethod = "GET", value = "Request reel tags that could be used when requesting articles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of tags", response = String.class, responseContainer = "List")})
    @RequestMapping(path = "tags/reel", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<List<String>> requestReelTags();

    @ApiOperation(httpMethod = "GET", value = "Request lure tags that could be used when requesting articles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of tags", response = String.class, responseContainer = "List")})
    @RequestMapping(path = "tags/lure", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<List<String>> requestLureTags();

    @ApiOperation(httpMethod = "GET", value = "Request accesory tags that could be used when requesting articles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of tags", response = String.class, responseContainer = "List")})
    @RequestMapping(path = "tags/accessory", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<List<String>> requestAccesssoryTags();

    @ApiOperation(httpMethod = "GET", value = "Request tackle tags that could be used when requesting articles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of tags", response = String.class, responseContainer = "List")})
    @RequestMapping(path = "tags/tackle", method = RequestMethod.POST, produces = "application/json")
    ResponseEntity<List<String>> requestTackleTags();

}
