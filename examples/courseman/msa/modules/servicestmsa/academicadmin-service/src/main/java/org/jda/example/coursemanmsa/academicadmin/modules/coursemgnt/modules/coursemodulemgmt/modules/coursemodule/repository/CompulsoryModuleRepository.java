package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.repository;

import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model.CompulsoryModule;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompulsoryModuleRepository extends PagingAndSortingRepository<CompulsoryModule,Integer>  {
}
