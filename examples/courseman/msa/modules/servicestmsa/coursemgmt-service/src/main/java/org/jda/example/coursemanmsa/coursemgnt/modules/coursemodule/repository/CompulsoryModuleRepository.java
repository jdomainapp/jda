package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.repository;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.ElectiveModule;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompulsoryModuleRepository extends PagingAndSortingRepository<CompulsoryModule,Integer>  {
}
