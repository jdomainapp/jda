import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

@loop{1}[[
  @slot{{import}}]]loop{1}@


const routes: Routes = [  
    @loop{2}[[
    {path: '@slot{{apiName}}', component: @slot{{moduleComponent}}, pathMatch: 'full'},]]loop{2}@
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
