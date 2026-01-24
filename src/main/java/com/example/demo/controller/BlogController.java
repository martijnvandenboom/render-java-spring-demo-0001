package com.example.demo.controller;

import com.example.demo.dto.PostRequest;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String blogHome(Model model) {
        List<Post> posts = blogService.getAllPublishedPosts();
        model.addAttribute("posts", posts);
        return "blog-home";
    }

    @GetMapping("/post/{slug}")
    public String viewPost(@PathVariable String slug, Model model) {
        Post post = blogService.getPostBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        model.addAttribute("post", post);
        return "blog-post";
    }

    @GetMapping("/new")
    public String newPostForm(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("post", new PostRequest());
        return "blog-new";
    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute PostRequest postRequest, 
                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post(
                postRequest.getTitle(),
                postRequest.getSubtitle(),
                postRequest.getContent(),
                author
        );
        post.setHeaderImageUrl(postRequest.getHeaderImageUrl());
        post.setPublished(postRequest.isPublished());

        blogService.createPost(post);
        return "redirect:/blog";
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, 
                              Authentication authentication, 
                              Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Post post = blogService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Check if user is the author
        String username = authentication.getName();
        if (!post.getAuthor().getUsername().equals(username)) {
            return "redirect:/blog";
        }

        model.addAttribute("post", post);
        return "blog-edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id,
                            @ModelAttribute PostRequest postRequest,
                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Post existingPost = blogService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is the author
        String username = authentication.getName();
        if (!existingPost.getAuthor().getUsername().equals(username)) {
            return "redirect:/blog";
        }

        Post updatedPost = new Post();
        updatedPost.setTitle(postRequest.getTitle());
        updatedPost.setSubtitle(postRequest.getSubtitle());
        updatedPost.setContent(postRequest.getContent());
        updatedPost.setHeaderImageUrl(postRequest.getHeaderImageUrl());
        updatedPost.setPublished(postRequest.isPublished());

        blogService.updatePost(id, updatedPost);
        return "redirect:/blog/post/" + existingPost.getSlug();
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Post post = blogService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is the author
        String username = authentication.getName();
        if (!post.getAuthor().getUsername().equals(username)) {
            return "redirect:/blog";
        }

        blogService.deletePost(id);
        return "redirect:/blog";
    }

    @GetMapping("/my-posts")
    public String myPosts(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts = blogService.getPostsByAuthor(author);
        model.addAttribute("posts", posts);
        model.addAttribute("author", author);
        return "blog-my-posts";
    }
}
