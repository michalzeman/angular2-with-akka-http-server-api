import { Component } from '@angular/core';
import {BaseEntityComponent} from "../../common/components/base-entity.component";
import {TestService} from "./test.service";
import {ActivatedRoute, Router, } from "@angular/router";
import {Test} from "./test";
import {FormMetadata, DomainMetadata, DropdownListItem} from "../../common/templates/form-metadata";
import {FormControlService} from "../../common/templates/form-control.service";
import {FormItemComponent} from "../../common/ui/form-item.component";
import {TestTemplate} from "./test-template";
import {ItemService} from "../item/item.service";
import {Item} from "../item/Item";

@Component({
  selector: 'test',
  providers: [TestService, TestTemplate, ItemService],
  templateUrl: '../../common/components/base-entity.html',
})
export class TestComponent extends BaseEntityComponent<Test> {

  constructor(entityService:TestService,
              formControlService:FormControlService,
              route:ActivatedRoute,
              router:Router,
              testTemplate: TestTemplate,
              protected itemService: ItemService) {
    super(entityService, formControlService, route, router, testTemplate);
  }


  ngOnInit(): any {
    super.ngOnInit();
    this.setList<Item>('itemId', this.itemService.getItemList(), (item) =>
      <DropdownListItem>{key: item.id, label: item.name, value: item.id}
    );
  }
}

