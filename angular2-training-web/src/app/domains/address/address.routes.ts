import { Routes } from '@angular/router';
import {AddressComponent} from "./address.component";
import {AddressTableComponent} from "./address-table.component";

const routes: Routes = [
  { path: 'address/:id', component: AddressComponent},
  { path: 'address', component: AddressComponent},
  { path: 'addresses', component: AddressTableComponent},
];

export const ADDRESS_ROUTER_PROVIDERS:Routes = routes;
