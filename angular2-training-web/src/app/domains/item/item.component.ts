import {Component} from '@angular/core';
import {BaseEntityComponent} from "../../common/components/base-entity.component";
import {Item} from "./Item";
import {FormMetadata} from "../../common/templates/form-metadata";
import {FormControlService} from "../../common/templates/form-control.service";
import {ActivatedRoute} from "@angular/router";
import {Router} from "@angular/router";
import {ItemService} from "./item.service";
import {ItemTemplate} from "./item-template";

@Component({
  selector: 'item',
  providers: [ItemService, ItemTemplate],
  templateUrl: '../../common/components/base-entity.html',
})
export class ItemComponent extends BaseEntityComponent<Item> {

  constructor(entityService: ItemService, formControlService: FormControlService,
              route: ActivatedRoute, router: Router, itemTemplate: ItemTemplate) {
    super(entityService, formControlService, route, router, itemTemplate);
  }

  protected getTitle(): string {
    return 'Item';
  }

}
