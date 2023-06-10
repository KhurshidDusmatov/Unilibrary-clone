package com.company.repository;


import com.company.entity.AttachEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttachRepository extends CrudRepository<AttachEntity,String>, PagingAndSortingRepository<AttachEntity,String> {
    Page<AttachEntity> findAll(Pageable paging);
}
