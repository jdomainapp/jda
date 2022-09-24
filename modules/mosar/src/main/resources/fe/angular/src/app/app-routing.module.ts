import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';@loop{importModuleComponent}[[
import { @slot{{ModuleName}}Component } from './@slot{{moduleJname}}/@slot{{moduleJname}}.component';]]loop{importModuleComponent}@

const routes: Routes = [@loop{routes}[[
    {path:'@slot{{moduleJnames}}' , component:@slot{{ModuleName}}Component, pathMatch: 'full'},]]loop{routes}@
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
