// /*
//  * Angular 2 decorators and services
//  */
// import { Component, ViewEncapsulation } from '@angular/core';
//
// import { AppState } from './app.service';
// import {TestComponent} from "./domains/test/test.component";
// import {ROUTER_DIRECTIVES} from "@angular/router";
//
// /*
//  * App Component
//  * Top Level Component
//  */
// @Component({
//   selector: 'app',
//   encapsulation: ViewEncapsulation.None,
//   directives: [TestComponent, ROUTER_DIRECTIVES],
//   providers: [],
//   styleUrls: [
//     './app.style.css'
//   ],
//   templateUrl: './app.component.html'
// })
// export class App {
//   angularclassLogo = 'assets/img/angularclass-avatar.png';
//   name = 'Angular 2 Webpack Starter';
//   url = 'https://twitter.com/AngularClass';
//
//   constructor(
//     public appState: AppState) {
//
//   }
//
//   ngOnInit() {
//     console.log('Initial App State', this.appState.state);
//   }
//
// }
//
// /*
//  * Please review the https://github.com/AngularClass/angular2-examples/ repo for
//  * more angular app examples that you may copy/paste
//  * (The examples may not be updated as quickly. Please open an issue on github for us to update it)
//  * For help or questions please contact us at @AngularClass on twitter
//  * or our chat on Slack at https://AngularClass.com/slack-join
//  */


/******************************* My version **************************/

import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {MenuComponent} from "./menu.component";
import {DelegateService} from "./common/services/delegate.service";
import {FormItemComponent} from "./common/components/form-item.component";
import {FormControlService} from "./common/templates/form-control.service";

@Component({
  selector: 'app',
  pipes: [],
  providers: [DelegateService, FormControlService],
  directives: [ROUTER_DIRECTIVES, MenuComponent],
  styleUrls: [
    './app.style.css'
  ],
  templateUrl: './app.component.html'
})
export class App {

  url = 'https://twitter.com/AngularClass';

  constructor() {
  }

}
