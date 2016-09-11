import { Routes } from '@angular/router';

import {Home} from './components/home/home';
import {TestComponent} from "./domains/test/test.component";
import {ItemComponent} from "./domains/item/item.component";
import {TestTableComponent} from "./domains/test/test-table.component";
import {ItmeTableComponent} from "./domains/item/item-table.component";

const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'test/:id', component: TestComponent},
  { path: 'test', component: TestComponent},
  { path: 'tests', component: TestTableComponent},
  { path: 'item/:id', component: ItemComponent},
  { path: 'item', component: ItemComponent},
  { path: 'items', component: ItmeTableComponent},
];

export const APP_ROUTER_PROVIDERS:Routes = routes;
