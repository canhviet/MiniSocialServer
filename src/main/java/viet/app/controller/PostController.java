package viet.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import viet.app.dto.request.LikeRequest;
import viet.app.dto.request.PostRequest;
import viet.app.dto.response.ResponseData;
import viet.app.dto.response.ResponseError;
import viet.app.service.LikeService;
import viet.app.service.PostService;

@RestController
@RequestMapping("/post")
@Slf4j
@Tag(name = "Post Controller")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private static final String ERROR_MESSAGE = "errorMessage={}";
    private final LikeService likeService;

    @Operation(method = "POST", summary = "Add new post", description = "Send a request via this API to create new post")
    @PostMapping("/")
    private ResponseData<Long> addPost(@RequestBody PostRequest postRequest) {
        try {
            long postId = postService.savePost(postRequest);
            return new ResponseData<>(HttpStatus.CREATED.value(), "add post success", postId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add post fail");
        }
    }

    @Operation(method = "GET", summary = "get all post", description = "Send a request via this API to get all post")
    @GetMapping("/getAll")
    private ResponseData<?> listPosts() {
        try {
            return postService.getAllPosts();
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "list posts fail");
        }
    }

    @Operation(method = "GET", summary = "get all post and sort by user", description = "Send a request via this API to get all post and sort by user")
    @GetMapping("/list")
    private ResponseData<?> listPostsAndSortByUser(@RequestParam long userId) {
        try {
            return postService.getAllPostsAndSortByUser(userId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "list posts fail");
        }
    }

    @Operation(summary = "Update post", description = "Send a request via this API to update post")
    @PutMapping("/{postId}")
    public ResponseData<Void> updateUser(@PathVariable @Min(1) long postId, @RequestBody PostRequest request) {
        log.info("Request update postId={}", postId);

        try {
            postService.updatePost(postId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "post.upd.success");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update post fail");
        }
    }

    @Operation(method = "GET", summary = "get all post by userId", description = "Send a request via this API to get all post by userId")
    @GetMapping("/list/{userId}")
    private ResponseData<?> listPostsByUserId(@PathVariable @Min(1) long userId) {
        try {
            return postService.getPostsByUserId(userId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "list posts fail");
        }
    }

    @Operation(method = "POST", summary = "like post by userId", description = "Send a request via this API to like post by userId")
    @PostMapping("/like")
    private void likePost(@RequestBody LikeRequest likeRequest) {
        log.info("Request like post");
        likeService.likePost(likeRequest);
    }

    @Operation(method = "POST", summary = "like post by userId", description = "Send a request via this API to like post by userId")
    @PostMapping("/unlike")
    private void unLikePost(@RequestBody LikeRequest likeRequest) {
        log.info("Request un like post");
        likeService.unLikePost(likeRequest);
    }

    @GetMapping("/check/{userId}/{postId}")
    private ResponseData<?> checkLikePost(@PathVariable long userId, @PathVariable long postId) {
        try {
            boolean check = likeService.check(userId, postId);
            return new ResponseData<>(HttpStatus.CREATED.value(), "check like", check);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
