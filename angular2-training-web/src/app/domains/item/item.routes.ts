import { Routes } from '@angular/router';
import {ItemComponent} from "./item.component";
import {ItemTableComponent} from "./item-table.component";

const routes: Routes = [
  { path: 'item/:id', component: ItemComponent},
  { path: 'item', component: ItemComponent},
  { path: 'items', component: ItemTableComponent},
];

export const ITEM_ROUTER_PROVIDERS:Routes = routes;
