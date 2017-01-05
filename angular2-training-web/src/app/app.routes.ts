import { Routes } from '@angular/router';

import {Home} from './components/home/home';
import {TestComponent} from "./domains/test/test.component";
import {ItemComponent} from "./domains/item/item.component";
import {TestTableComponent} from "./domains/test/test-table.component";
import {ItmeTableComponent} from "./domains/item/item-table.component";
import {AddressTableComponent} from "./domains/address/address-table.component";
import {AddressComponent} from "./domains/address/address.component";

const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'test/:id', component: TestComponent},
  { path: 'test', component: TestComponent},
  { path: 'tests', component: TestTableComponent},
  { path: 'item/:id', component: ItemComponent},
  { path: 'item', component: ItemComponent},
  { path: 'items', component: ItmeTableComponent},
  { path: 'address/:id', component: AddressComponent},
  { path: 'address', component: AddressComponent},
  { path: 'addresses', component: AddressTableComponent},
];

export const APP_ROUTER_PROVIDERS:Routes = routes;
