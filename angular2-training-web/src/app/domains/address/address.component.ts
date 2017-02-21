import {BaseEntityComponent} from "../../common/components/base-entity.component";
import {Address} from "./address";
import {FormControlService} from "../../common/templates/form-control.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AddressTemplate} from "./address-templete";
import {AddressService} from "./address.service";
import {Component} from '@angular/core';

@Component({
  selector: 'address',
  providers: [AddressService, AddressTemplate],
  templateUrl: '../../common/components/base-entity.html',
})
export class AddressComponent extends BaseEntityComponent<Address, AddressService, AddressTemplate> {


  constructor(entityService: AddressService, formControlService: FormControlService, route: ActivatedRoute,
              router: Router, domainTemplate: AddressTemplate) {
    super(entityService, formControlService, route, router, domainTemplate);
  }
}
