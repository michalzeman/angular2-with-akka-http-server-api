/**
 * Created by zemo on 22/08/16.
 */

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';

/*
 * Platform and Environment providers/directives/pipes
 */
// import { PLATFORM_PROVIDERS } from '../platform/browser';
// import { ENV_PROVIDERS } from '../platform/environment';
import { APP_ROUTER_PROVIDERS} from './app.routes';


import { App }   from './app.component';
import {FormControlService} from "./common/templates/form-control.service";
import {BroadcastEmmitterService} from "./common/services/broadcast-emitter.servie";
import {BroadcasterService} from "./common/services/broadcaster.service";
import {DelegateService} from "./common/services/delegate.service";
import {ItmeTableComponent} from "./domains/item/item-table.component";
import {Home} from "./components/home/home";
import {TestComponent} from "./domains/test/test.component";
import {ItemComponent} from "./domains/item/item.component";
import {TestTableComponent} from "./domains/test/test-table.component";

@NgModule({
  bootstrap:    [App],
  declarations: [App, Home, TestComponent, TestTableComponent, ItemComponent, ItmeTableComponent],
  imports:      [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule.forRoot(APP_ROUTER_PROVIDERS)],
  providers:    [DelegateService, BroadcasterService, BroadcastEmmitterService, FormControlService],
})
export class AppModule {}
