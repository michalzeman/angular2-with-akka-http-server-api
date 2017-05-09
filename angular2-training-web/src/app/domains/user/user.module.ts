/**
 * Created by zemo on 24/02/2017.
 */
import {NgModule} from "@angular/core";
import {UiModule} from "../../common/ui/ui.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";
import {ADDRESS_ROUTER_PROVIDERS} from "./address.routes";
import {UserComponent} from "./user.component";
import {UserTableComponent} from "./user-table.component";
import {USER_ROUTER_PROVIDERS} from "./user.routes";

@NgModule({
  declarations: [UserComponent, UserTableComponent],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule.forChild(USER_ROUTER_PROVIDERS), UiModule],
  exports: [ UserComponent,  UserTableComponent],
})
export class UserModule {}
