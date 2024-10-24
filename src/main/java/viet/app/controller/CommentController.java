package viet.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import viet.app.dto.request.CommentRequest;
import viet.app.dto.response.ResponseData;
import viet.app.dto.response.ResponseError;
import viet.app.service.CommentService;

@RestController
@RequestMapping("/comment")
@Slf4j
@Tag(name = "Comment Controller")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private static final String ERROR_MESSAGE = "errorMessage={}";

    @Operation(method = "POST", summary = "Add new comment", description = "Send a request via this API to create new comment")
    @PostMapping("/")
    private ResponseData<Long> addComment(@RequestBody CommentRequest commentRequest) {
        try {
            long commentId = commentService.saveComment(commentRequest);
            return new ResponseData<>(HttpStatus.CREATED.value(), "add comment success", commentId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add comment fail");
        }
    }

    @Operation(method = "GET", summary = "get all comment by post", description = "Send a request via this API to get all comment by post")
    @GetMapping("/list/{postId}")
    private ResponseData<?> listPosts(@PathVariable @Min(1) long postId) {
        try {
            return commentService.getAllCommentsByPostId(postId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "list comments fail");
        }
    }
}
