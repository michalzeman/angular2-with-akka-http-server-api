import {Component} from '@angular/core';

@Component({
  selector: 'app',
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
