/**
 * Created by zemo on 31/01/2017.
 */
import {NgModule} from "@angular/core";
import {UiModule} from "../../common/ui/ui.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";
import {ADDRESS_ROUTER_PROVIDERS} from "./address.routes";
import {TEST_ROUTER_PROVIDERS} from "./test.routes";
import {TestComponent} from "./test.component";
import {TestTableComponent} from "./test-table.component";

@NgModule({
  declarations: [TestComponent, TestTableComponent],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule.forChild(TEST_ROUTER_PROVIDERS), UiModule],
  exports: [ TestComponent,  TestTableComponent],
})
export class TestModule {}
