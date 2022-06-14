import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule} from './app-routing.module';
import { AppComponent } from './app.component';
import { BaseComponent } from './base/base.component';
import { BaseFormComponent } from './base/base-form/base-form.component';
import { BaseListComponent } from './base/base-list/base-list.component';
import { BaseFormDirectiveComponent } from './base/base-form/base-form-directive.component';
import { CompDirective } from './common/comp.directive';
import { BaseService } from './base/base.service';
import { BaseCommonComponent } from './base/base-common/base-common.component';
import { AlertComponent } from './alert/alert.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NotificationService } from './common/notification.service';
@loop{import}[[
@slot{{import-main}}
@slot{{import-form}}]]loop{import}@

@NgModule({
  declarations: [
    AppComponent,
    BaseComponent,
    BaseFormComponent,
    BaseListComponent,
    BaseFormDirectiveComponent,
    CompDirective,
    BaseCommonComponent,
    AlertComponent,
    @loop{declare}[[
    @slot{{decl-main}},
    @slot{{decl-form}}]]loop{declare}@
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule ,
    AppRoutingModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule
  ],
  providers: [BaseService, NotificationService],
  bootstrap: [AppComponent]
})
export class AppModule { }
