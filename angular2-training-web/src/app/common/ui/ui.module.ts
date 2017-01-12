import {NgModule} from "@angular/core";
import {FormItemComponent} from "./form-item.component";
import {PaginationComponent} from "./pagination/pagination.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {DelegateService} from "../services/delegate.service";
import {BroadcasterService} from "../services/broadcaster.service";
import {BroadcastEmmitterService} from "../services/broadcast-emitter.servie";
import {FormControlService} from "../templates/form-control.service";
import {BrowserModule} from "@angular/platform-browser";
import {RouterModule} from "@angular/router";

@NgModule({
  declarations: [FormItemComponent, PaginationComponent],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule],
  exports: [ FormItemComponent,  PaginationComponent],
  providers:    [DelegateService, BroadcasterService, BroadcastEmmitterService, FormControlService]
})
export class UiModule {}
