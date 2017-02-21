import {Component} from '@angular/core';
import {BaseTableComponent} from "../../common/components/base-table.component";
import {Address} from "./address";
import {AddressService} from "./address.service";
import {Router, ActivatedRoute} from "@angular/router";
import {AddressTemplate} from "./address-templete";


@Component({
  selector: 'address-table',
  providers: [AddressService, AddressTemplate],
  templateUrl: '../../common/components/base-table.html',
})
export class AddressTableComponent extends BaseTableComponent<Address, AddressService, AddressTemplate> {

  constructor(crudService: AddressService, router: Router, route: ActivatedRoute, domainTemplate: AddressTemplate) {
    super(crudService, router, route, domainTemplate);
  }
}
