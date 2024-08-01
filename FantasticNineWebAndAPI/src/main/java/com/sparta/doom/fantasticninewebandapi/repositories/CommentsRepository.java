package com.sparta.doom.fantasticninewebandapi.repositories;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends MongoRepository<CommentDoc, String> {
    Page<CommentDoc> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<CommentDoc> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<CommentDoc> findByMovieId(String movieId, Pageable pageable);
}
