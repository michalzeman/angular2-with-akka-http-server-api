import { Routes } from '@angular/router';

import {Home} from './components/home/home';
import {TestComponent} from "./domains/test/test.component";
import {TestTableComponent} from "./domains/test/test-table.component";

const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'test/:id', component: TestComponent},
  { path: 'test', component: TestComponent},
  { path: 'tests', component: TestTableComponent},
];

export const APP_ROUTER_PROVIDERS:Routes = routes;
