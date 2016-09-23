import {Injectable} from "@angular/core";
import {BaseEntityServiceImpl} from "../../common/services/entity/base-entity.service";
import {Item} from "./Item";
import {DelegateService} from "../../common/services/delegate.service";
import {Http} from "@angular/http";

@Injectable()
export class ItemService extends BaseEntityServiceImpl<Item> {

  constructor(delegateService: DelegateService, http: Http) {
    super(delegateService, http, '/items');
  }

}
