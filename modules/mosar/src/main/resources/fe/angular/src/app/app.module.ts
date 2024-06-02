import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BaseModule } from './modules/base/base.module';
import { BaseService } from './modules/base/services/base.service';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbRatingModule } from '@ng-bootstrap/ng-bootstrap';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { RatingModule } from 'ngx-bootstrap/rating';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
@loop{importModuleComponents}[[
import { @slot{{ModuleName}}FormComponent } from './@slot{{moduleJname}}/components/@slot{{moduleJname}}-form/@slot{{moduleJname}}-form.component';
import { @slot{{ModuleName}}ListComponent } from './@slot{{moduleJname}}/components/@slot{{moduleJname}}-list/@slot{{moduleJname}}-list.component';
import { @slot{{ModuleName}}Component } from './@slot{{moduleJname}}/components/@slot{{moduleJname}}/@slot{{moduleJname}}.component';
]]loop{importModuleComponents}@
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { NgxSliderModule } from 'ngx-slider-v2';
import { PatternService } from './modules/base/pattern/pattern.service';

@NgModule({
  declarations: [
    AppComponent,
    @loop{declareModuleComponents}[[
    @slot{{ModuleName}}Component,
    @slot{{ModuleName}}ListComponent,
    @slot{{ModuleName}}FormComponent,
]]loop{declareModuleComponents}@
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,

    BaseModule,
    FormsModule,
    ReactiveFormsModule,

    // PatternModule,
      BrowserAnimationsModule,

      RatingModule.forRoot(),
    // NgbModule
    NgbRatingModule,
    BsDatepickerModule.forRoot(),
    AccordionModule.forRoot(),
    NgxSliderModule
  ],
  providers: [BaseService, PatternService],
  bootstrap: [AppComponent]
})
export class AppModule { }
