/**
 * Created by zemo on 22/08/16.
 */
import { provideRouter, RouterConfig } from '@angular/router';
import {ItemComponent} from "./item.component";
import {ItmeTableComponent} from "./item-table.component";

export const itemRoutes: RouterConfig = [
  { path: 'item/:id', component: ItemComponent},
  { path: 'item', component: ItemComponent},
  { path: 'items', component: ItmeTableComponent},
];
