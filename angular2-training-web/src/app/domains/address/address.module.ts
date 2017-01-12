import {NgModule} from "@angular/core";
import {AddressComponent} from "./address.component";
import {AddressTableComponent} from "./address-table.component";
import {UiModule} from "../../common/ui/ui.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";
import {ADDRESS_ROUTER_PROVIDERS} from "./address.routes";

@NgModule({
  declarations: [AddressComponent, AddressTableComponent],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule.forChild(ADDRESS_ROUTER_PROVIDERS), UiModule],
  exports: [ AddressComponent,  AddressTableComponent],
})
export class AddressModule {}
