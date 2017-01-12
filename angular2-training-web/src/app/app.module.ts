/**
 * Created by zemo on 22/08/16.
 */

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';

import { APP_ROUTER_PROVIDERS} from './app.routes';


import { App }   from './app.component';
import {Home} from "./components/home/home";
import {TestComponent} from "./domains/test/test.component";
import {TestTableComponent} from "./domains/test/test-table.component";
import {MenuComponent} from "./menu.component";
import {AlertComponent} from "./domains/alert/alert.component";
import {UiModule} from "./common/ui/ui.module";
import {AddressModule} from "./domains/address/address.module";
import {ItemModule} from "./domains/item/item.module";

@NgModule({
  bootstrap:    [App],
  declarations: [App, Home, TestComponent, TestTableComponent, MenuComponent, AlertComponent],
  imports:      [BrowserModule, FormsModule, ReactiveFormsModule, HttpModule, RouterModule.forRoot(APP_ROUTER_PROVIDERS),
    UiModule, AddressModule, ItemModule],
  providers:    [],
})
export class AppModule {}
// providers:    [DelegateService, BroadcasterService, BroadcastEmmitterService, FormControlService],
