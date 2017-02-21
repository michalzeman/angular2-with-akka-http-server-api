import {Component} from '@angular/core';
import {BaseTableComponent} from "../../common/components/base-table.component";
import {Item} from "./Item";
import {ItemService} from "./item.service";
import {ItemTemplate} from "./item-template";
import {ActivatedRoute} from "@angular/router";
import {Router} from "@angular/router";


@Component({
  selector: 'item-table',
  providers: [ItemService, ItemTemplate],
  templateUrl: '../../common/components/base-table.html',
})
export class ItemTableComponent extends BaseTableComponent<Item, ItemService, ItemTemplate> {


  constructor(crudService: ItemService, router: Router, route: ActivatedRoute, domainTemplate: ItemTemplate) {
    super(crudService, router, route, domainTemplate);
  }


}
