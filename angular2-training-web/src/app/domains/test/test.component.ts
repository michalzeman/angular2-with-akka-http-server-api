import { Component } from '@angular/core';
import {BaseEntityComponent} from "../../common/components/base-entity.component";
import {TestService} from "./test.service";
import {ActivatedRoute, Router, } from "@angular/router";
import {Test} from "./test";
import {FormMetadata, DomainMetadata} from "../../common/templates/form-metadata";
import {FormControlService} from "../../common/templates/form-control.service";
import {FormItemComponent} from "../../common/components/form-item.component";
import {TestTemplate} from "./test-template";

@Component({
  selector: 'test',
  providers: [TestService, TestTemplate],
  templateUrl: '../../common/components/base-entity.html',
})
export class TestComponent extends BaseEntityComponent<Test> {

  constructor(entityService:TestService,
              formControlService:FormControlService,
              route:ActivatedRoute,
              router:Router,
              testTemplate: TestTemplate) {
    super(entityService, formControlService, route, router, testTemplate);
  }

}

