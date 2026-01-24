package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPublishedPosts() {
        return postRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public Optional<Post> getPostBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    public Optional<Post> getPostById(Long id) {
        if (id == null) return Optional.empty();
        return postRepository.findById(id);
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Post> getRecentPosts(int limit) {
        return postRepository.findTop5ByPublishedTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public Post createPost(Post post) {
        if (post == null) throw new IllegalArgumentException("Post cannot be null");
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        if (id == null) throw new IllegalArgumentException("Post ID cannot be null");
        if (updatedPost == null) throw new IllegalArgumentException("Updated post cannot be null");
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setSubtitle(updatedPost.getSubtitle());
                    post.setContent(updatedPost.getContent());
                    post.setHeaderImageUrl(updatedPost.getHeaderImageUrl());
                    post.setPublished(updatedPost.isPublished());
                    return postRepository.save(post);
                })
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public void deletePost(Long id) {
        if (id == null) throw new IllegalArgumentException("Post ID cannot be null");
        postRepository.deleteById(id);
    }

    @Transactional
    public void togglePublish(Long id) {
        if (id == null) throw new IllegalArgumentException("Post ID cannot be null");
        postRepository.findById(id).ifPresent(post -> {
            post.setPublished(!post.isPublished());
            postRepository.save(post);
        });
    }
}
