import { NgModule } from '@angular/core';
import { CommonModule, NgComponentOutlet } from '@angular/common';
import { BaseComponent } from './components/base/base.component';
import { BaseService } from './services/base.service';
import { BaseListComponent } from './components/base-list/base-list.component';
import { PagedTableComponent } from './components/paged-table/paged-table.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BtnDeleteComponent } from './components/btn-delete/btn-delete.component';
import { HasOneComponent } from './components/has-one/has-one.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { BelongsToComponent } from './components/belongs-to/belongs-to.component';
import { HasManyComponent } from './components/has-many/has-many.component';
import { BaseManagerComponent } from './components/base-manager/base-manager.component';
import { ManagerComponent } from './components/manager/manager.component';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { ViewRegionComponent } from 'src/app/modules/base/pattern/view-region';
import { AutoSearchComponent } from './patterns/autosearch/autosearch.component';
import { PatternService } from './pattern/pattern.service';

@NgModule({
  declarations: [
    BaseComponent,
    BaseListComponent,
    PagedTableComponent,
    BtnDeleteComponent,
    BaseManagerComponent,
    ManagerComponent,
    HasOneComponent,
    BelongsToComponent,
    HasManyComponent,
    
    ViewRegionComponent,
    AutoSearchComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    FormsModule,
    NgxPaginationModule,
    ReactiveFormsModule,
    
    BrowserAnimationsModule,
    TypeaheadModule.forRoot(),

    NgComponentOutlet,    
  ],
  exports: [
    PagedTableComponent,
    BtnDeleteComponent,
    HasOneComponent,
    BelongsToComponent,
    HasManyComponent,
    BaseManagerComponent,
    ManagerComponent,
    ViewRegionComponent,

    AutoSearchComponent,
  ],
  providers: [
    BaseService,
    PatternService
  ]
})
export class BaseModule { }
