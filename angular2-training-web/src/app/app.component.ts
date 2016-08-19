import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {MenuComponent} from "./menu.component";
import {DelegateService} from "./common/services/delegate.service";
import {FormControlService} from "./common/templates/form-control.service";
import {AlertComponent} from "./domains/alert/alert.component";
import {BroadcasterService} from "./common/services/broadcaster.service";
import {BroadcastEmmitterService} from "./common/services/broadcast-emitter.servie";

@Component({
  selector: 'app',
  pipes: [],
  providers: [DelegateService, BroadcasterService, BroadcastEmmitterService, FormControlService],
  directives: [ROUTER_DIRECTIVES, MenuComponent, AlertComponent],
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
