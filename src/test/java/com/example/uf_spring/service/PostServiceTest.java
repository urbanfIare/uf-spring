package com.example.uf_spring.service;

import com.example.uf_spring.dto.PostDto;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.User;
import com.example.uf_spring.model.PostCategory;
import com.example.uf_spring.repository.PostRepository;
import com.example.uf_spring.repository.UserRepository;
import com.example.uf_spring.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private User testUser;
    private PostDto.CreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("테스트 사용자");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("테스트 게시글");
        testPost.setContent("테스트 내용");
        testPost.setCategory(PostCategory.GENERAL);
        testPost.setAuthor(testUser);
        testPost.setLikeCount(0);
        testPost.setViewCount(0);

        createRequest = new PostDto.CreateRequest();
        createRequest.setTitle("새 게시글");
        createRequest.setContent("새 내용");
        createRequest.setCategory(PostCategory.GENERAL);
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        // Given
        List<Post> expectedPosts = Arrays.asList(testPost);
        when(postRepository.findByIsDeletedFalseOrderByCreatedAtDesc()).thenReturn(expectedPosts);
        when(commentRepository.countByPostIdAndIsDeletedFalse(any())).thenReturn(0L);

        // When
        List<PostDto.Response> actualPosts = postService.getAllPosts();

        // Then
        assertThat(actualPosts).hasSize(1);
        assertThat(actualPosts.get(0).getTitle()).isEqualTo("테스트 게시글");
        verify(postRepository, times(1)).findByIsDeletedFalseOrderByCreatedAtDesc();
    }

    @Test
    void getPostById_WhenPostExists_ShouldReturnPost() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentRepository.countByPostIdAndIsDeletedFalse(any())).thenReturn(0L);

        // When
        PostDto.Response result = postService.getPostById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 게시글");
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void createPost_ShouldReturnCreatedPost() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(commentRepository.countByPostIdAndIsDeletedFalse(any())).thenReturn(0L);

        // When
        PostDto.Response result = postService.createPost(createRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 게시글");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getPostsByCategory_ShouldReturnFilteredPosts() {
        // Given
        List<Post> expectedPosts = Arrays.asList(testPost);
        when(postRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(PostCategory.GENERAL))
                .thenReturn(expectedPosts);
        when(commentRepository.countByPostIdAndIsDeletedFalse(any())).thenReturn(0L);

        // When
        List<PostDto.Response> result = postService.getPostsByCategory(PostCategory.GENERAL);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(PostCategory.GENERAL);
        verify(postRepository, times(1)).findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(PostCategory.GENERAL);
    }

    @Test
    void searchPosts_ShouldReturnMatchingPosts() {
        // Given
        List<Post> expectedPosts = Arrays.asList(testPost);
        when(postRepository.findByTitleOrContentContaining("테스트"))
                .thenReturn(expectedPosts);
        when(commentRepository.countByPostIdAndIsDeletedFalse(any())).thenReturn(0L);

        // When
        List<PostDto.Response> result = postService.searchPosts("테스트");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("테스트");
        verify(postRepository, times(1)).findByTitleOrContentContaining("테스트");
    }

    @Test
    void likePost_ShouldIncrementLikeCount() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(commentRepository.countByPostIdAndIsDeletedFalse(any())).thenReturn(0L);

        // When
        PostDto.Response result = postService.likePost(1L);

        // Then
        assertThat(result).isNotNull();
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void incrementViewCount_ShouldIncrementViewCount() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        postService.incrementViewCount(1L);

        // Then
        verify(postRepository, times(1)).save(any(Post.class));
    }
} 