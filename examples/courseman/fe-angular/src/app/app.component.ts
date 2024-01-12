import { Component, Input, ViewContainerRef, ViewRef, ɵcompileComponent } from '@angular/core';
// import { PatternService } from './pattern/pattern.consumer';
import { NgFor } from '@angular/common';
import { compileComponentFromMetadata } from '@angular/compiler';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(
    private viewRef: ViewContainerRef,
    // private patternService: PatternService
  ) {
    // patternService.registerPattern(Accordip)
  }

  chatItem = {
    message: 'Initial name',
    avatar:
      'https://icon-library.com/images/avatar-icon-images/avatar-icon-images-4.jpg',
  };

  updateChatItem() {
    const randomInt = Math.round(Math.random() * 10);
    this.chatItem = {
      message: `Random ${randomInt}`,
      avatar: `https://icon-library.com/images/avatar-icon-images/avatar-icon-images-${randomInt}.jpg`,
    };
  } 
  
}