import { Component } from '@angular/core';
import {BaseTableComponent} from "../../common/components/base-table.component";
import {Test} from "./test";
import {TestService} from "./test.service";
import {TestTemplate} from "./test-template";
import {Router, ActivatedRoute} from "@angular/router";

@Component({
  selector: 'test-table',
  pipes: [],
  providers: [TestService, TestTemplate],
  directives: [],
  templateUrl: '../../common/components/base-table.html',
})
export class TestTableComponent extends BaseTableComponent<Test> {

  constructor(crudService: TestService, router: Router, route: ActivatedRoute, domainTemplate: TestTemplate) {
    super(crudService, router, route, domainTemplate);
  }

}
