import {UserService} from "./user.service";
import {BaseEntityComponent} from "../../common/components/base-entity.component";
import {User} from "./user";
import {UserTemplate} from "./user.template";
import { Component } from '@angular/core';
import {FormControlService} from "../../common/templates/form-control.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Address} from "../address/address";
import {DropdownListItem} from "../../common/templates/form-metadata";
import {AddressService} from "../address/address.service";
/**
 * Created by zemo on 24/02/2017.
 */



@Component({
  selector: 'user',
  providers: [UserService, UserTemplate, AddressService],
  templateUrl: '../../common/components/base-entity.html',
})
export class UserComponent extends BaseEntityComponent<User, UserService, UserTemplate> {

  constructor(entityService:UserService,
              formControlService:FormControlService,
              route:ActivatedRoute,
              router:Router,
              userTemplate: UserTemplate,
              protected addressService: AddressService) {
    super(entityService, formControlService, route, router, userTemplate);
  }

  ngOnInit(): any {
    super.ngOnInit();
    this.setList<Address>('addressId', this.addressService.getAll(), (item) => {
        return <DropdownListItem>{key: item.id, label: item.city, value: item.id};
      }
    );
  }
}
