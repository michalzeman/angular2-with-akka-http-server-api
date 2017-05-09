import {UserComponent} from "./user.component";
import {Routes} from "@angular/router";
import {UserTableComponent} from "./user-table.component";
/**
 * Created by zemo on 24/02/2017.
 */

const routes: Routes = [{ path: 'user/:id', component: UserComponent},
  { path: 'user', component: UserComponent},
  { path: 'users', component: UserTableComponent}];

export const USER_ROUTER_PROVIDERS:Routes = routes;
