import { Component } from '@angular/core';
import {BaseTableComponent} from "../../common/components/base-table.component";
import {User} from "./user";
import {UserService} from "./user.service";
import {UserTemplate} from "./user.template";
import {Router, ActivatedRoute} from "@angular/router";
/**
 * Created by zemo on 24/02/2017.
 */

@Component({
  selector: 'test-table',
  providers: [UserService, UserTemplate],
  templateUrl: '../../common/components/base-table.html',
})
export class UserTableComponent extends BaseTableComponent<User, UserService, UserTemplate> {


  constructor(crudService: UserService, router: Router, route: ActivatedRoute, domainTemplate: UserTemplate) {
    super(crudService, router, route, domainTemplate);
  }
}
