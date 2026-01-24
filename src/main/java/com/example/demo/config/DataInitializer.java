package com.example.demo.config;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Only initialize if no users exist
            if (userRepository.count() == 0) {
                // Create demo user
                User demoUser = new User("demo", passwordEncoder.encode("demo123"), "demo@example.com");
                userRepository.save(demoUser);
                
                // Create sample blog posts
                Post post1 = new Post(
                    "Welcome to Clean Blog",
                    "A Beautiful Blog Template for Spring Boot",
                    "<p>This is your first blog post! Clean Blog is a carefully styled Bootstrap blog theme that is perfect for personal or company blogs. This theme features a striking full-width image header with a bold, centered site name.</p>" +
                    "<p>The blog posts are designed to be easily readable with plenty of white space. Each post features a full-width header image and stylish typography throughout.</p>" +
                    "<h2>Getting Started</h2>" +
                    "<p>To create a new post, simply log in and click the 'New Post' button in the navigation. You can write your content using HTML for formatting, add a subtitle, and even specify a custom header image URL.</p>" +
                    "<p>Happy blogging!</p>",
                    demoUser
                );
                post1.setPublished(true);
                post1.setHeaderImageUrl("https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?w=1920&h=600&fit=crop");
                postRepository.save(post1);

                Post post2 = new Post(
                    "The Power of Spring Boot",
                    "Building Modern Web Applications with Ease",
                    "<p>Spring Boot has revolutionized the way we build Java applications. With its convention-over-configuration approach, developers can focus on writing business logic instead of boilerplate code.</p>" +
                    "<h2>Key Features</h2>" +
                    "<ul>" +
                    "<li>Auto-configuration that adapts to your application</li>" +
                    "<li>Embedded servers (Tomcat, Jetty, Undertow)</li>" +
                    "<li>Production-ready features like metrics and health checks</li>" +
                    "<li>Easy integration with Spring Security, JPA, and more</li>" +
                    "</ul>" +
                    "<p>This blog application demonstrates many of these features, including Spring Security for authentication, Spring Data JPA for database operations, and Thymeleaf for server-side rendering.</p>",
                    demoUser
                );
                post2.setPublished(true);
                post2.setHeaderImageUrl("https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=1920&h=600&fit=crop");
                postRepository.save(post2);

                Post post3 = new Post(
                    "WebAuthn: The Future of Authentication",
                    "Passwordless Security with Biometric Authentication",
                    "<p>WebAuthn (Web Authentication) is a web standard published by the W3C that enables strong, public key-based authentication for web applications. It's the future of secure, passwordless authentication.</p>" +
                    "<h2>Benefits of WebAuthn</h2>" +
                    "<p><strong>Enhanced Security:</strong> Uses public-key cryptography instead of passwords, making phishing attacks virtually impossible.</p>" +
                    "<p><strong>Better User Experience:</strong> Users can authenticate using fingerprints, facial recognition, or security keys - no passwords to remember!</p>" +
                    "<p><strong>Privacy Focused:</strong> Credentials are unique per website, preventing tracking across sites.</p>" +
                    "<h2>Try It Yourself</h2>" +
                    "<p>This application includes full WebAuthn support. After logging in with your password, visit your dashboard to set up biometric authentication for future logins.</p>",
                    demoUser
                );
                post3.setPublished(true);
                post3.setHeaderImageUrl("https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=1920&h=600&fit=crop");
                postRepository.save(post3);

                Post post4 = new Post(
                    "Understanding Thymeleaf Templates",
                    "Server-Side Rendering in Spring Boot",
                    "<p>Thymeleaf is a modern server-side Java template engine for web applications. It integrates seamlessly with Spring Boot and allows you to create dynamic HTML pages with ease.</p>" +
                    "<h2>Why Thymeleaf?</h2>" +
                    "<p>Unlike JSP, Thymeleaf templates are valid HTML5 documents that can be opened directly in browsers and previewed without running the server. This makes development and design collaboration much easier.</p>" +
                    "<blockquote class=\"blockquote\">\"Natural templates that can be displayed in the browser and still work as prototypes.\"</blockquote>" +
                    "<p>The syntax is intuitive and powerful, allowing you to iterate over collections, conditionally display content, and include fragments for reusable components.</p>",
                    demoUser
                );
                post4.setPublished(true);
                post4.setHeaderImageUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1920&h=600&fit=crop");
                postRepository.save(post4);

                System.out.println("✅ Sample data initialized: 1 user and 4 blog posts created");
                System.out.println("📝 Demo login: username=demo, password=demo123");
            }
        };
    }
}
