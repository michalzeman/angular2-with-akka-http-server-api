import {NgModule} from "@angular/core";
import {UiModule} from "../../common/ui/ui.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";
import {ADDRESS_ROUTER_PROVIDERS} from "./address.routes";
import {ItemComponent} from "./item.component";
import {ItemTableComponent} from "./item-table.component";
import {ITEM_ROUTER_PROVIDERS} from "./item.routes";

@NgModule({
  declarations: [ItemComponent, ItemTableComponent],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule.forChild(ITEM_ROUTER_PROVIDERS), UiModule],
  exports: [ ItemComponent,  ItemTableComponent],
})
export class ItemModule {}
