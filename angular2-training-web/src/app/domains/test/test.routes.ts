
import {TestTableComponent} from "./test-table.component";
import {Routes} from "@angular/router";
import {TestComponent} from "./test.component";

const routes: Routes = [{ path: 'test/:id', component: TestComponent},
{ path: 'test', component: TestComponent},
{ path: 'tests', component: TestTableComponent}];

export const TEST_ROUTER_PROVIDERS:Routes = routes;
