import {Component} from '@angular/core';
import {MenuComponent} from "./menu.component";
import {AlertComponent} from "./domains/alert/alert.component";

@Component({
  selector: 'app',
  pipes: [],
  directives: [MenuComponent, AlertComponent],
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
